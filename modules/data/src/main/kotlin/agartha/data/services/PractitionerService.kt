package agartha.data.services

import agartha.common.config.Settings.Companion.returnNegativeNumber
import agartha.data.db.conn.MongoConnection
import agartha.data.objects.*
import com.mongodb.client.result.UpdateResult
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
    private val collection = database.getCollection<PractitionerDBO>(CollectionNames.PRACTITIONER_SERVICE.collectionName)

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
     * @param practitioner the practitioner
     * @param session session to Add to practitioner
     * @return
     */
    override fun startSession(
            practitioner: PractitionerDBO,
            session: SessionDBO): SessionDBO {
        // PractitionerId will never be an empty string, but kotlin wont allow us to access practitioner._id without it maybe being null
        val practitionerId: String = practitioner._id ?: ""
        // Push session to practitioner
        pushSession(practitionerId, session)
        // If session has a circle then it should add a new item to the spiritBankLog
        // But not if the practitioner is a creator of the circle
        if (session.circle != null && !practitioner.creatorOfCircle(session.circle)) {
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
                // Update the session with a endTime
                updateSessionWithEndTime(practitionerId)

                // Add the new logItem about the ended session to the spiritBankLog for the practitioner
                storeSpiritBankLogEndedSession(practitioner, contributionPoints, ongoingSession)

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
     * Update a sessionDBO to the practitioners sessions
     * @param practitionerId - string - the practitioner to be updated
     * @param ongoingSession - sessionDBO - that should be pushed to the practitioners session list
     */
    private fun updateSessionWithEndTime(practitionerId: String) {
        // Get index of latest session
        val index = getById(practitionerId)?.sessions?.size ?: -1
        // If we have a latest session
        if (index > 0) {
            // Update and set end time for this index to now
            collection.updateOneById(
                    practitionerId,
                    Document("${MongoOperator.set}",
                            Document("sessions.${index - 1}.endTime", LocalDateTime.now())))
        }
    }


    /**
     * Update a circleDBO to the practitioners session
     * @param practitionerId - string - the practitioner to be updated
     */
    private fun updateCircleWithEndTime(practitionerId: String, circleToUpdate: CircleDBO) {
        // Get index of the circle that we want to update
        val index = getById(practitionerId)?.circles?.indexOf(circleToUpdate) ?: -1
        // If we have a the circle we are looking for
        if (index >= 0) {
            // Update and set end time for this index to now
            collection.updateOneById(
                    practitionerId,
                    Document("${MongoOperator.set}",
                            Document("circles.$index.endTime", LocalDateTime.now())))
        }
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
     * When ending a session log into the spiritBank depending on if the practitioner is a creator of the circle
     *
     * @param practitioner      - PractitionerDBO - the practitioner
     * @param contributionPoints- Long - the points that was contributed
     * @param ongoingSession    - Long - the ongoing session for the practitioner
     */
    private fun storeSpiritBankLogEndedSession(practitioner: PractitionerDBO,
                                               contributionPoints: Long,
                                               ongoingSession: SessionDBO) {
        // PractitionerId will never be an empty string, but kotlin wont allow us to access practitioner._id without it maybe being null
        val practitionerId: String = practitioner._id ?: ""
        // create variables
        var spiritBankLogType = SpiritBankLogItemType.SESSION
        var addedContributionPoints = contributionPoints
        // Check if practitioner is in a circle and if practitioner is a creator of that circle
        if (ongoingSession.circle != null && practitioner.creatorOfCircle(ongoingSession.circle)) {
            spiritBankLogType = SpiritBankLogItemType.ENDED_CREATED_CIRCLE
            // Calculate the points practitioner should get from those that joined the circle
            addedContributionPoints += calculatePointsFromPractitionersJoiningCreatorsCircle(ongoingSession.circle, ongoingSession.startTime)
            // Close the creators circle
            updateCircleWithEndTime(practitionerId, ongoingSession.circle)
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

    override fun removeCircleById(practitionerId: String, circleId: String): Boolean {
        val result: UpdateResult = collection.updateOneById(
                practitionerId,
                Document("${MongoOperator.pull}",
                        Document("circles", Document("_id", circleId))))
        //
        return result.modifiedCount == 1L
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