package agartha.data.services

import agartha.common.utils.DateTimeFormat
import agartha.data.db.conn.MongoConnection
import agartha.data.objects.PractitionerDBO
import org.litote.kmongo.*
import java.time.LocalDateTime

/**
 * Purpose of this file is manipulating data for a practitioner in data storage
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
     * Get all practitioner with session that has been ongoing any time after argument dateTime
     * @param dateTime LocalDateTime
     * @return List of pracitioner with at least one session active after argument dateTime
     */
    override fun getPractitionersWithSessionAfter(dateTime: LocalDateTime): List<PractitionerDBO> {
        val mongoFormattedStart = DateTimeFormat.formatDateTimeAsMongoString(dateTime)
        // Practitioner should have start dateTime after argument dateTime
        val start = """{sessions: {${MongoOperator.elemMatch}: { startTime: { ${MongoOperator.gte}: ISODate('${mongoFormattedStart}') } } } }"""
        // OR
        // Practitioner should have end dateTime after argument dateTime
        val end = """{sessions: {${MongoOperator.elemMatch}: { endTime: { ${MongoOperator.gte}: ISODate('${mongoFormattedStart}') } } } }"""
        //
        // Join the two for getting practitioners with start or end time in argument date, ie find overlapping sessions
        val condition = """{${MongoOperator.or}: [${start},${end}]}"""
        // Get the stuff
        return collection
                .find(condition)
                .toList()
    }
}