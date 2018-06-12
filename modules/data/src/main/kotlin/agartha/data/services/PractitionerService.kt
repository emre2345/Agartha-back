package agartha.data.services

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
    override fun updatePractitionerWithInvolvedInformation(user: PractitionerDBO, fullName: String, email: String, description: String): PractitionerDBO {
        // Add the new 'Get involved'-information
        user.addInvolvedInformation(fullName, email, description)
        // Update the user
        return user.apply {
            collection.updateOne(user)
        }
    }

    /**
     * Start a new user session
     * @param practitionerId identity for practitioner
     * @param session session to Add to practitioner
     * @return
     */
    override fun startSession(
            practitionerId: String,
            session: SessionDBO): SessionDBO {
        // Push session to practitioner
        pushSession(practitionerId, session)
        // If session has a circle then it should add a new item to the spiritBankLog
        if(session.circle !== null){
            val cost = session.circle.minimumSpiritContribution - (session.circle.minimumSpiritContribution)*2
            pushContributionPoints(practitionerId, cost, SpiritBankLogItemType.JOINED_CIRCLE)
        }
        // return next index
        return session
    }

    override fun endSession(practitionerId: String, contributionPoints: Long): PractitionerDBO? {
        // Get current user
        val user: PractitionerDBO? = getById(practitionerId)
        if (user != null && user.sessions.isNotEmpty()) {
            // get the current session
            val ongoingSession = user.sessions.lastOrNull()
            // If there is a matching user with sessions
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
                pushContributionPoints(practitionerId, contributionPoints, SpiritBankLogItemType.SESSION)
                // Return the new updated practitioner
                return getById(practitionerId)
            }
        }
        return user
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

}