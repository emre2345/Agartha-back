package agartha.data.services

import agartha.common.utils.DateTimeFormat
import agartha.data.db.conn.MongoConnection
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.bson.Document
import org.litote.kmongo.*
import java.time.LocalDateTime

/**
 * Purpose of this file is manipulating data for a practitioner in data storage
 *
 * Created by Jorgen Andersson on 2018-04-25.
 */
class SessionService : ISessionService {
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
     * Start a new user session
     */
    override fun startSession(userId: String, practition: String): Int {
        // Get current user
        val user: PractitionerDBO? = getById(userId)
        // Calculate next index (if any of user or user.sessions is null: rtn 0)
        val nextIndex = user?.sessions?.count() ?: 0
        // Create a new Session
        val session = SessionDBO(nextIndex, practition)
        // Create Mongo Document to be added to sessions list
        val sessionDoc = Document("sessions", session)
        // Update first document found by Id, push the new document
        collection.updateOneById(userId, Document("${MongoOperator.push}", sessionDoc))
        // return next index
        return nextIndex
    }


    /**
     * End users session
     */
    override fun endSession(userId: String, sessionId: Int) {

    }

    override fun getPractitionersWithSessionAfter(startDateTime: LocalDateTime): List<PractitionerDBO> {
        val mongoFormattedStart = DateTimeFormat.formatDateTimeAsMongoString(startDateTime)
        // Find practitioners with session start time after argument date
        val strStart = """{sessions: {${MongoOperator.elemMatch}: { startTime: { ${MongoOperator.gte}: ISODate('${mongoFormattedStart}') } } } }"""
        // Get the stuff
        return collection
                .find(strStart)
                .toList()
    }

}