package agartha.data.services

import agartha.data.db.conn.MongoConnection
import agartha.data.objects.GeolocationDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
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
     * @param disciplineName name of discipline
     * @param intentionName name of intention
     * @return index for created practice
     */
    override fun startSession(
            practitionerId: String,
            geolocation: GeolocationDBO?,
            disciplineName: String,
            intentionName: String): SessionDBO {
        // Create a new Session
        val session = SessionDBO(geolocation, disciplineName, intentionName)
        // Push session to practitioner
        pushSession(practitionerId, session)
        // return next index
        return session
    }

    override fun endSession(practitionerId: String): PractitionerDBO? {
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
                // Return the new updated practitioner
                return getById(practitionerId)
            }
        }
        return user
    }

    override fun removeAll(): Boolean {
        return try {
            collection.drop()
            true
        }catch(e: Exception) {
            false
        }
    }

    override fun removeGenerated(): List<PractitionerDBO> {
        // Delete all that has this specific description
        collection.deleteMany("{ 'description' : 'Generated Practitioner' }")
        return collection.find().toList()
    }

    override fun removeById(practitionerId: String): Boolean {
        return try {
            collection.deleteOneById(practitionerId)
            true
        }catch(e: Exception) {
            false
        }
    }


    private fun pushSession(practitionerId: String, session: SessionDBO) {
        // Update first document found by Id, push the new document
        collection.updateOneById(
                practitionerId,
                Document("${MongoOperator.push}",
                        // Create Mongo Document to be added to sessions list
                        Document("sessions", session)))
    }

}