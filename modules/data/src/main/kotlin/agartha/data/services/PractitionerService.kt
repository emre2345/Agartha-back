package agartha.data.services

import agartha.common.config.Settings.Companion.COST_ADD_VIRTUAL_SESSION_POINTS
import agartha.common.config.Settings.Companion.returnNegativeNumber
import agartha.common.utils.DateTimeFormat
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
        pushObjectToPractitionersArray(practitionerId, PractitionersArraysEnum.SESSIONS, session)
        // If session has a circle then it should add a new item to the spiritBankLog
        // But not if the practitioner is a creator of the circle
        if (session.circle != null && !practitioner.creatorOfCircle(session.circle)) {
            val cost = returnNegativeNumber(session.circle.minimumSpiritContribution)
            pushObjectToPractitionersArray(practitionerId, PractitionersArraysEnum.SPIRIT_BANK_LOG, SpiritBankLogItemDBO(type = SpiritBankLogItemType.JOINED_CIRCLE, points = cost))
        }
        // return next index
        return session
    }

    /**
     * End a practitioner session
     * Observe: Session must be ended before the circle,
     * (we calculate total contribution for a circle by using sessions ended before circle is ended)
     */
    override fun endSession(practitionerId: String, contributionPoints: Long): PractitionerDBO? {
        // Get current practitioner
        val practitioner: PractitionerDBO? = getById(practitionerId)
        if (practitioner != null && practitioner.sessions.isNotEmpty()) {
            // get the current session
            val ongoingSession = practitioner.sessions.lastOrNull()
            // If there is a matching practitioner with the session
            if (ongoingSession != null) {
                // Update the session with a endTime
                updateSessionWithEndTimeNow(practitionerId)
                // If the practitioner is in a circle that the practitioner is the creator of
                if (ongoingSession.circle != null && practitioner.creatorOfCircle(ongoingSession.circle)) {
                    // Calculate the total points for contribution
                    val totalCalculatedContributionPoints = calculatePointsFromPractitionersJoiningCreatorsCircle(ongoingSession.circle, ongoingSession.startTime, contributionPoints)
                    // Close the circle
                    updateCircleWithEndTimeNow(practitionerId, ongoingSession.circle)
                    // Push the points as a ended circle to the log
                    pushObjectToPractitionersArray(practitionerId,
                            PractitionersArraysEnum.SPIRIT_BANK_LOG,
                            SpiritBankLogItemDBO(
                                    type = SpiritBankLogItemType.ENDED_CREATED_CIRCLE,
                                    points = totalCalculatedContributionPoints))
                } else {
                    // Push the points as a ended session to the log
                    pushObjectToPractitionersArray(practitionerId,
                            PractitionersArraysEnum.SPIRIT_BANK_LOG,
                            SpiritBankLogItemDBO(
                                    type = SpiritBankLogItemType.ENDED_SESSION,
                                    points = contributionPoints))
                }

                // Return the new updated practitioner
                return getById(practitionerId)
            }
        }
        return practitioner
    }


    /**
     * Add a circle id to a practitioner registeredCircles list
     * @return practitioner with the new registered circle ids
     */
    override fun addRegisteredCircle(practitionerId: String, circleId: String): PractitionerDBO? {
        pushObjectToPractitionersArray(practitionerId, PractitionersArraysEnum.REGISTERED_CIRCLES, circleId)
        return getById(practitionerId)
    }

    /**
     * Add a circle to a practitioner
     * @return practitioner with the circles
     */
    override fun addCircle(practitionerId: String, circle: CircleDBO): PractitionerDBO? {
        pushObjectToPractitionersArray(practitionerId, PractitionersArraysEnum.CIRCLES, circle)
        return getById(practitionerId)
    }

    /**
     * Edit a circle to a practitioner
     * @return practitioner with the edited attributes
     */
    override fun editCircle(practitionerId: String, circle: CircleDBO): PractitionerDBO? {
        // Update the circle
        updateEditedCircle(practitionerId, circle)
        return getById(practitionerId)
    }

    /**
     * Remove all the practitioners in the db
     * @return true if all went fine
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
     * @return list of practitioners that is left after the remove
     */
    override fun removeGenerated(): List<PractitionerDBO> {
        // Delete all that has this specific description
        collection.deleteMany("{ 'description' : 'Generated Practitioner' }")
        return collection.find().toList()
    }

    /**
     * Removes one Practitioner from the collection
     * @return True if the deletion went fine
     */
    override fun removeById(practitionerId: String): Boolean {
        val result = collection.deleteOneById(practitionerId)
        return result.deletedCount == 1L
    }


    /**
     * Removes one circle from the practitioner-collection
     * @return True if the deletion went fine
     */
    override fun removeCircleById(practitionerId: String, circleId: String): Boolean {
        val result: UpdateResult = collection.updateOneById(
                practitionerId,
                Document("${MongoOperator.pull}",
                        Document(PractitionersArraysEnum.CIRCLES.value, Document("_id", circleId))))
        //
        return result.modifiedCount == 1L
    }



    /**
     * Calculates the cost for adding virtual sessions to a circle
     * Makes sure the practitioner have those points in its bank
     * Makes a new log for the practitioner spirit bank log with the cost
     *
     * @param practitioner the practitioner that wants to add virtual sessions
     * @param numberOfSessions the number of sessions the practitioner wants to add
     * @return true if practitioner successfully paid the contributionsPoints
     */
    override fun payForAddingVirtualSessions(practitioner: PractitionerDBO, numberOfSessions: Long): Boolean {
        // PractitionerId will never be an empty string, but kotlin wont allow us to access practitioner._id without it maybe being null
        val practitionerId: String = practitioner._id ?: ""
        // Multiply the cost for adding a virtual session with the number of sessions that it wants to add
        val pointsToPay = COST_ADD_VIRTUAL_SESSION_POINTS * numberOfSessions
        // Practitioner needs to have pointsToPay in its bank
        if(practitioner.calculateSpiritBankPointsFromLog() >= pointsToPay){
            // Add a new log to the practitioners spirit bank log
            pushObjectToPractitionersArray(practitionerId,
                    PractitionersArraysEnum.SPIRIT_BANK_LOG,
                    SpiritBankLogItemDBO(
                            type = SpiritBankLogItemType.ADD_VIRTUAL_TO_CIRCLE,
                            points = returnNegativeNumber(pointsToPay)))
            return true
        }
        return false
    }


    /**
     * Update the last session for a user by setting endTime to now
     *
     * @param practitionerId - string - the practitioner to be updated
     */
    private fun updateSessionWithEndTimeNow(practitionerId: String) {
        // Get index of latest session
        val index = getById(practitionerId)?.sessions?.size ?: -1
        // If we have a latest session
        if (index > 0) {
            // Update and set end time for this index to now
            collection.updateOneById(
                    practitionerId,
                    Document("${MongoOperator.set}",
                            Document("${PractitionersArraysEnum.SESSIONS.value}.${index - 1}.endTime", DateTimeFormat.localDateTimeUTC())))
        }
    }


    /**
     * Updates practitioners created circle by setting endTime to now
     *
     * @param practitionerId - string - the practitioner to be updated
     * @param circleToUpdate - CircleDBO - the circle that should be updated
     */
    private fun updateCircleWithEndTimeNow(practitionerId: String, circleToUpdate: CircleDBO) {
        // Get index of the circle that we want to update
        val index = getById(practitionerId)?.circles?.indexOf(circleToUpdate) ?: -1
        // If we have a the circle we are looking for
        if (index >= 0) {
            // Update and set end time for this index to now
            collection.updateOneById(
                    practitionerId,
                    Document("${MongoOperator.set}",
                            Document("${PractitionersArraysEnum.CIRCLES.value}.$index.endTime", DateTimeFormat.localDateTimeUTC())))
        }
    }

    /**
     * Updates practitioners created circle to the edited circle
     *
     * @param practitionerId - string - the practitioner to be updated
     * @param circleToEdit - CircleDBO - the circle that is edited
     */
    private fun updateEditedCircle(practitionerId: String, circleToEdit: CircleDBO) {
        // Get index of the circle that we want to update
        val circles = getById(practitionerId)?.circles
        val circleToUpdate = circles?.find { it._id == circleToEdit._id }
        val index = circles?.indexOf(circleToUpdate) ?: -1
        // If we have a the circle we are looking for
        if (index >= 0) {
            // Update and set end time for this index to now
            collection.updateOneById(
                    practitionerId,
                    Document("${MongoOperator.set}",
                            Document("${PractitionersArraysEnum.CIRCLES.value}.$index", circleToEdit)))
        }
    }


    /**
     * Add a object to a practitioners array in  DB
     *
     * @param practitionerId id for practitioner to add circle
     * @param item the object that should be to added
     * @return practitioner with newly added object
     */
    private fun <T> pushObjectToPractitionersArray(practitionerId: String, objectName: PractitionersArraysEnum, item: T) {
        collection.updateOneById(
                practitionerId,
                Document("${MongoOperator.push}",
                        // Create Mongo Document to be added to sessions list
                        Document(objectName.value, item)))
    }

    /**
     * Calculates the contribution points gathered from practitioners joining
     * the circle that is in the ongoingSession for a practitioner
     * And then adds the contributionPoints from the client(number of points from the endedSessions)
     *
     * @return number of contribution points
     */
    private fun calculatePointsFromPractitionersJoiningCreatorsCircle(circle: CircleDBO, startTime: LocalDateTime, contributionPoints: Long): Long {
        // Find all practitioners that has a session with this circle and is started after practitioners session started
        val sessionsInCircle: List<PractitionerDBO> = getAll().filter { it.hasSessionInCircleAfterStartTime(startTime, circle) }
        // Number of practitioner that started a session in "my" circle and payed the minimumSpiritContribution
        // will be multiplied by the minimumSpiritContribution
        // then the contributionPoints from the ended sessions will be added
        return (sessionsInCircle.size * circle.minimumSpiritContribution) + contributionPoints
    }

}