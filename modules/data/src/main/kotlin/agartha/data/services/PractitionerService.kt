package agartha.data.services

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.bson.Document
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.find
import org.litote.kmongo.updateOneById

/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class PractitionerService : MongoBaseService<PractitionerDBO>(CollectionNames.PRACTITIONER_SERVICE) {

    fun getActiveCount(): Int {
        return collection
                // Find all where any of the sessions has active is true
                .find("{sessions.active:true}")
                // Count 'em
                .count()
    }

    fun insertSession(id: String, session: SessionDBO) {
        collection.updateOneById(id, Document("${MongoOperator.push}", session))
    }

}