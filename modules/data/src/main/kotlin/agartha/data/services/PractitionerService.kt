package agartha.data.services

import agartha.common.config.Settings.Companion.COST_ADD_VIRTUAL_SESSION_POINTS
import agartha.common.config.Settings.Companion.returnNegativeNumber
import agartha.common.utils.DateTimeFormat
import agartha.data.db.conn.MongoConnection
import agartha.data.objects.*
import com.mongodb.client.result.UpdateResult
import org.bson.Document
import org.litote.kmongo.*

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
     * @param practitionerId identity of practitioner
     * @param contributionPoints points to be awarded to practitioner
     */
    override fun endSession(practitionerId: String, contributionPoints: Long) {
        val session = getLastSession(practitionerId)
        if (session != null) {
            // Update the session with a endTime
            updateSessionWithEndTimeNow(practitionerId)
            // Push the points as a ended session to the log
            pushObjectToPractitionersArray(practitionerId,
                    PractitionersArraysEnum.SPIRIT_BANK_LOG,
                    SpiritBankLogItemDBO(
                            type = SpiritBankLogItemType.ENDED_SESSION,
                            points = contributionPoints))
        }
    }

    /**
     * End a practitioners circle by adding points to practitioner
     * and if creator end circle
     * and push feedback from practitioner to circle-creators circle
     * @param practitionerId identity of practitioner
     * @param creator is above practitioner id creator of circle
     * @param circle circle to end
     * @param contributionPoints points to be awarded to practitioner
     * @param feedBackPoints points for feedback to the circle
     */
    override fun endCircle(
            practitionerId: String,
            creator: Boolean,
            circle: CircleDBO?,
            contributionPoints: Long,
            feedbackPoints: Long?) {
        // if practitioner is creator or circle
        if (creator && circle != null) {
            updateCircleWithEndTimeNow(practitionerId, circle)
        }
        // If we are adding points to practitioner for ending circle
        if (contributionPoints > 0) {
            // Push the points as a ended circle to the log
            pushObjectToPractitionersArray(practitionerId,
                    PractitionersArraysEnum.SPIRIT_BANK_LOG,
                    SpiritBankLogItemDBO(
                            type = SpiritBankLogItemType.ENDED_CREATED_CIRCLE,
                            points = contributionPoints))
        }
        // If the circle got any feedback then push the feedback to the list
        if(feedbackPoints != null && circle != null){
            // Get creator of circle
            val creatorOfCircle: PractitionerDBO? = getCreatorOfCircle(circle)
            if (creatorOfCircle?._id != null){
                // Get index of the circle that we want to push the points to its feedback list
                val index = getIndexOfCircleForPractitioner(creatorOfCircle._id, circle)
                // If we have a the circle we are looking for
                if (index >= 0) {
                    // Push the points to the feedback list for this circle
                    collection.updateOneById(
                            creatorOfCircle._id,
                            Document("${MongoOperator.push}",
                                    Document("${PractitionersArraysEnum.CIRCLES.value}.$index.feedback", feedbackPoints)))
                }
            }
        }
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
     * @param virtualRegistered the number of sessions the practitioner wants to add
     * @return true if practitioner successfully paid the contributionsPoints
     */
    override fun payForAddingVirtualSessions(practitioner: PractitionerDBO, virtualRegistered: Long): Boolean {
        // PractitionerId will never be an empty string, but kotlin wont allow us to access practitioner._id without it maybe being null
        val practitionerId: String = practitioner._id ?: ""
        // Multiply the cost for adding a virtual session with the number of sessions that it wants to add
        val pointsToPay = COST_ADD_VIRTUAL_SESSION_POINTS * virtualRegistered
        // Practitioner needs to have pointsToPay in its bank
        if (checkPractitionerCanAffordVirtualRegistered(practitioner, virtualRegistered)) {
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
     * @param practitioner the practitioner that wants to add virtual sessions
     * @param virtualRegistered the number of sessions the practitioner wants to add
     * @return true if practitioner can afford this many virtual registered
     */
    override fun checkPractitionerCanAffordVirtualRegistered(practitioner: PractitionerDBO, virtualRegistered: Long): Boolean {
        // Multiply the cost for adding a virtual session with the number of virtualRegistered that it wants to add
        val pointsToPay = COST_ADD_VIRTUAL_SESSION_POINTS * virtualRegistered
        // Does the practitioner afford to pay?
        return practitioner.calculateSpiritBankPointsFromLog() >= pointsToPay
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
        val index = getIndexOfCircleForPractitioner(practitionerId, circleToEdit)
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

    private fun getLastSession(practitionerId: String): SessionDBO? {
        return getById(practitionerId)?.sessions?.lastOrNull()
    }

    /**
     * Get the creator of a circle
     * @param circle that the creator should have in its circles
     * @return practitioner that is creator of circle
     */
    private fun getCreatorOfCircle(circle: CircleDBO): PractitionerDBO? {
        // Go through all the practitioners
        return getAll().firstOrNull {
            // Is practitioner creator of this circle?
            it.creatorOfCircle(circle) }
    }

    /**
     * Get the index of a circle in the practitioners circle-list
     * @param practitionerId id of the practitioner that owns the circle
     * @param circle circle we are looking for index
     * @return int - the circles index in the list
     */
    private fun getIndexOfCircleForPractitioner(practitionerId: String, circle: CircleDBO): Int {
        val circles = getById(practitionerId)?.circles
        val circleToUpdate = circles?.find { it._id == circle._id }
        return circles?.indexOf(circleToUpdate) ?: -1
    }

}