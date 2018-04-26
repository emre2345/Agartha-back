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
     * Update item in database
     */
    override fun updatePractitioner(id: String, practitioner: PractitionerDBO): PractitionerDBO {
        //collection.updateOneById(id, PractitionerDBO)
        // TODO: implement!
        return practitioner
        /*
        fun update(id: String, item: T): T {
            return item.apply {
                collection.updateOneById(id, item)
            }
        }*/
    }

    /**
     * Get all practitioners with session ongoing between these dates
     *
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    override fun getPractitionersWithSessionBetween(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<PractitionerDBO> {
        //
        val mongoFormattedStart = DateTimeFormat.formatDateTimeAsMongoString(startDateTime)
        val mongoFormattedEnd = DateTimeFormat.formatDateTimeAsMongoString(endDateTime)
        //
        // Find practitioners with session start time between argument dates
        val strStart = """{sessions: {${MongoOperator.elemMatch}: { startTime: { ${MongoOperator.gte}: ISODate('${mongoFormattedStart}'), ${MongoOperator.lt}: ISODate('${mongoFormattedEnd}') } } } }"""
        // Find practitioners with session end time between argument dates
        val strEnd = """{sessions: {${MongoOperator.elemMatch}: { endTime: { ${MongoOperator.gte}: ISODate('${mongoFormattedStart}'), ${MongoOperator.lt}: ISODate('${mongoFormattedEnd}') } } } }"""
        // Join the two for getting practitioners with start or end time in argument date, ie find overlapping sessions
        val sessionsWithOverlappingStartAndEndTime = """{${MongoOperator.or}: [${strStart},${strEnd}]}"""
        //
        // Get the stuff
        return collection
                .find(sessionsWithOverlappingStartAndEndTime)
                .toList()
    }

}