package agartha.data.services

import agartha.common.config.Settings.Companion.returnNegativeNumber
import agartha.data.db.conn.MongoConnection
import agartha.data.objects.*
import org.bson.Document
import org.litote.kmongo.*
import java.time.LocalDateTime

/**
 * Purpose of this implementation is functions for reading/writing practitioner data in storage
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class PractitionerService : IPractitionerService {

    // Get MongoDatabase
    private val database = MongoConnection.getDatabase()
    // MongoCollection
    protected val collection = database.getCollection<PractitionerDBO>(CollectionNames.PRACTITIONER_SERVICE.collectionName)


    override fun insert(item: PractitionerDBO): PractitionerDBO {
        return item.apply {
            collection.insertOne(item)
        }
    }

    override fun getById(id: String): PractitionerDBO? {
        return collection.findOneById(id)
    }

    override fun getAll(): List<PractitionerDBO> {
        return collection.find().toList()
    }

    /**
     * Update practitioner in database with 'Get involved'-information
     */
    override fun updatePractitionerWithInvolvedInformation(practitioner: PractitionerDBO,
                                                           fullName: String,
                                                           email: String,
                                                           description: String): PractitionerDBO {
        // Add the new 'Get involved'-information
        practitioner.addInvolvedInformation(fullName, email, description)
        // Update the practitioner
        return practitioner.apply {
            collection.updateOne(practitioner)
        }
    }

    /**
     * Start a new practitioners session
     * @param practitionerId identity for practitioner
     * @param practitioner the practitioner
     * @param session session to Add to practitioner
     * @return
     */
    override fun startSession(
            practitionerId: String,
            practitioner: PractitionerDBO,
            session: SessionDBO): SessionDBO {
        // Push session to practitioner
        pushSession(practitionerId, session)
        // If session has a circle then it should add a new item to the spiritBankLog
        // But not if the practitioner is a creator of the circle
        if (session.circle !== null && !practitioner.creatorOfCircle(session.circle)) {
            val cost = returnNegativeNumber(session.circle.minimumSpiritContribution)
            pushContributionPoints(practitionerId, cost, SpiritBankLogItemType.JOINED_CIRCLE)
        }
        // return next index
        return session
    }

    override fun endSession(practitionerId: String, contributionPoints: Long): PractitionerDBO? {
        // Get current practitioner
        val practitioner: PractitionerDBO? = getById(practitionerId)
        if (practitioner != null && practitioner.sessions.isNotEmpty()) {
            // get the current session
            val ongoingSession = practitioner.sessions.lastOrNull()
            // If there is a matching practitioner with sessions
            if (ongoingSession != null) {
                // Remove last item from sessions array
                collection.updateOneById(
                        practitionerId,
                        // Create Mongo Document to pop/remove item from array
                        Document("${MongoOperator.pop}",
                                // Create Mongo Document to indicate last item in array
                                Document("sessions", 1)))
                // Create new Session with ongoing session as base
                val session = SessionDBO(
                        geolocation = ongoingSession.geolocation,
                        discipline = ongoingSession.discipline,
                        intention = ongoingSession.intention,
                        startTime = ongoingSession.startTime,
                        endTime = LocalDateTime.now())
                // Add it to sessions array
                pushSession(practitionerId, session)

                // Add the new logItem about the ended session to the spiritBankLog for the practitioner
                storeSpiritBankLogEndedSession(practitionerId, practitioner, contributionPoints, ongoingSession)

                // Return the new updated practitioner
                return getById(practitionerId)
            }
        }
        return practitioner
    }

    /**
     * Add a circle to a practitioner
     */
    override fun addCircle(practitionerId: String, circle: CircleDBO): PractitionerDBO? {
        pushCircle(practitionerId, circle)
        return getById(practitionerId)
    }

    /**
     * Remove all the practitioners in the db
     */
    override fun removeAll(): Boolean {
        return try {
            collection.drop()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Remove all practitioners that is generated
     */
    override fun removeGenerated(): List<PractitionerDBO> {
        // Delete all that has this specific description
        collection.deleteMany("{ 'description' : 'Generated Practitioner' }")
        return collection.find().toList()
    }

    /**
     * Removes one item from the collection
     */
    override fun removeById(practitionerId: String): Boolean {
        val result = collection.deleteOneById(practitionerId)
        return result.deletedCount == 1L
    }


    /**
     * Push a new sessionDBO to the practitioners sessions
     * @param practitionerId - string - the practitioner to be updated
     * @param session - sessionDBO - that should be pushed to the practitioners session list
     */
    private fun pushSession(practitionerId: String, session: SessionDBO) {
        // Update first document found by Id, push the new document
        collection.updateOneById(
                practitionerId,
                Document("${MongoOperator.push}",
                        // Create Mongo Document to be added to sessions list
                        Document("sessions", session)))
    }


    /**
     * When ending a session the log into the spiritBank depend on if the practitioner is a creator of the circle
     * @param practitionerId    - string - the practitioners id
     * @param practitioner      - PractitionerDBO - the practitioner
     * @param contributionPoints- Long - the points that was contributed
     * @param ongoingSession    - Long - the ongoing session for the practitioner
     */
    private fun storeSpiritBankLogEndedSession(practitionerId: String,
                                               practitioner: PractitionerDBO,
                                               contributionPoints: Long,
                                               ongoingSession: SessionDBO) {
        // create variables
        var spiritBankLogType = SpiritBankLogItemType.SESSION
        var addedContributionPoints = contributionPoints
        // Check if practitioner is in a circle and if practitioner is a creator of that circle
        if (ongoingSession.circle !== null && practitioner.circles.contains(ongoingSession.circle)) {
            spiritBankLogType = SpiritBankLogItemType.ENDED_CREATED_CIRCLE
            // Calculate the points practitioner should get from those that joined the circle
            addedContributionPoints += calculatePointsFromPractitionersJoiningCreatorsCircle(ongoingSession.circle, ongoingSession.startTime)
        }
        // Push to the log
        pushContributionPoints(practitionerId, addedContributionPoints, spiritBankLogType)
    }

    /**
     * Push a new spiritBankLogItem to the spiritBankLog in the practitionerDBO
     * @param practitionerId - string - the practitioner to be updated
     * @param contributionPoints - Long - the points that was contributed
     */
    private fun pushContributionPoints(practitionerId: String, contributionPoints: Long, type: SpiritBankLogItemType) {
        // Update first document found by Id, push the new document
        collection.updateOneById(
                practitionerId,
                Document("${MongoOperator.push}",
                        // Create Mongo Document to be added to spiritBankLog list
                        Document("spiritBankLog", SpiritBankLogItemDBO(type = type, points = contributionPoints))))
    }

    /**
     * Add a circle to a practitioner
     * @param practitionerId id for practitioner to add circle
     * @param circle circle to add
     * @return practitioner with newly added circle
     */
    private fun pushCircle(practitionerId: String, circle: CircleDBO) {
        collection.updateOneById(
                practitionerId,
                Document("${MongoOperator.push}",
                        // Create Mongo Document to be added to sessions list
                        Document("circles", circle)))

    }

    /**
     * Calculates the contribution points gathered from practitioners joining
     * the circle that is in the ongoingSession for a practitioner
     *
     * @return number of contribution points
     */
    private fun calculatePointsFromPractitionersJoiningCreatorsCircle(circle: CircleDBO, startTime: LocalDateTime): Long {
        // Find all practitioners that has a session with this circle and is started after practitioners session started
        val sessionsInCircle: List<PractitionerDBO> = getAll().filter { it.hasSessionInCircleAfterStartTime(startTime, circle) }
        // Number of practitioner that started a session in "my" circle and payed the minimumSpiritContribution
        // should be multiplied by the minimumSpiritContribution
        return sessionsInCircle.size * circle.minimumSpiritContribution
    }

}