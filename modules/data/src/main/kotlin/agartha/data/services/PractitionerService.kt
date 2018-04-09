package agartha.data.services

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.bson.Document
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.find
import org.litote.kmongo.updateOneById

/**
 * Purpose of this file is manipulating data for a practitioner in data storage
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class PractitionerService : MongoBaseService<PractitionerDBO>(CollectionNames.PRACTITIONER_SERVICE) {

    /**
     * Get current number of people practicing
     * @return
     */
    fun getActiveCount(): Int {
        return collection
                // Find all where any of the sessions has active is true
                .find("{sessions.active:true}")
                // Count 'em
                .count()
    }

    /**
     * Start a new user session
     */
    fun startSession(userId: String, practition: String): Int {
        // Get current user
        val user = getById(userId)
        // Calculate next index (if any of user or user.sessions is null: rtn 0)
        val nextIndex = (user?.sessions?.count() ?: 0) + 1
        // Create a new Session
        val session = SessionDBO(nextIndex, practition)
        // Create Mongo Document to be added to sessions list
        val sessionDoc = Document("sessions", session)
        // Update first document found by Id, push the new document
        collection.updateOneById(userId, Document("${MongoOperator.push}", sessionDoc))
        // return next index
        return nextIndex
    }

    fun endSession(userId: String, sessionId: Int) {

    }

}