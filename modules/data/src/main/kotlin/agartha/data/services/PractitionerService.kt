package agartha.data.services

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.bson.Document
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.find
import org.litote.kmongo.updateOneById
import java.time.LocalDateTime

/**
 * Purpose of this file is manipulating data for a practitioner in data storage
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class PractitionerService : MongoBaseService<PractitionerDBO>(CollectionNames.PRACTITIONER_SERVICE), IPractitionerService {

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
                .toList() as List<PractitionerDBO>
    }

}