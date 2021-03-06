package agartha.data.services

import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO

/**
 * Purpose of this interface for Practitioner datasource service
 * The interface extends the IBaseService holding functions all services must have
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
interface IPractitionerService : IBaseService<PractitionerDBO> {

    /**
     * Function to update a document in database collection
     * @param practitioner
     * @param fullName
     * @param email
     * @param description
     * @return inserted document as object
     */
    fun updatePractitionerWithInvolvedInformation(
            practitioner: PractitionerDBO,
            fullName: String,
            email: String,
            description: String): PractitionerDBO

    /**
     * Start a new session/practice
     * @param practitioner user starting the session
     * @param session session to Add to user
     * @return the started session
     */
    fun startSession(
            practitioner: PractitionerDBO,
            session: SessionDBO): SessionDBO

    /**
     * @param practitionerId id for user ending a session
     */
    fun endSession(
            practitionerId: String, contributionPoints: Long)

    fun endCircle(
            practitionerId: String,
            creator: Boolean,
            circle: CircleDBO?,
            contributionPoints: Long)

    /**
     * Add a circle id to a practitioner's registered circles array
     * @param practitionerId practitioner id to add circle to
     * @param circleId id to add to the array
     * @return practitioner with the new registered circles
     */
    fun addRegisteredCircle(
            practitionerId: String, circleId: String): PractitionerDBO?

    /**
     * Add a circle to a practitioner
     * @param practitionerId practitioner id to add circle to
     * @param circle circle to add to practitioner
     * @return
     */
    fun addCircle(
            practitionerId: String, circle: CircleDBO): PractitionerDBO?

    /**
     * Edit a circle to a practitioner
     * @param practitionerId practitioner id to add circle to
     * @param circle - to edit that belongs to practitioner
     * @return
     */
    fun editCircle(
            practitionerId: String, circle: CircleDBO): PractitionerDBO?

    /**
     * Remove all practitioners
     * @return boolean - true oif all went fine
     */
    fun removeAll(): Boolean

    /**
     * Remove all generated
     * @return a list with the practitioner that has not been removed
     */
    fun removeGenerated(): List<PractitionerDBO>

    /**
     * Remove one practitioner
     * @return boolean - true if all went fine
     */
    fun removeById(
            practitionerId: String): Boolean

    /**
     * Get the creator of a Circle
     * @param circle circle to get creator from
     */
    fun getCreatorOfCircle(circle: CircleDBO): PractitionerDBO?

    /**
     * Remove a circle from practitioner
     * @param practitionerId id for practitioner to remove from
     * @param circleId id for circle to remove
     * @return true if circle was removed
     */
    fun removeCircleById(
            practitionerId: String, circleId: String): Boolean

    /**
     * Calculates the cost for adding virtual sessions to a circle
     * Makes sure the practitioner have those points in its bank
     * Makes a new log for the practitioner spirit bank log with the cost
     *
     * @param practitioner the practitioner that wants to add virtual sessions
     * @param virtualRegistered the number of virtual registered that the practitioner wants to add
     * @return true if practitioner successfully paid the contributionsPoints
     */
    fun payForAddingVirtualSessions(
            practitioner: PractitionerDBO, virtualRegistered: Long): Boolean


    /**
     * Checks if the practitioner can afford to pay for all the virtual registered to the circle
     *
     * @param practitioner the practitioner that wants to add virtual sessions
     * @param virtualRegistered the number of virtual registered that the practitioner wants to add
     * @return true if practitioner can afford to pay with contribution points
     */
    fun checkPractitionerCanAffordVirtualRegistered(
            practitioner: PractitionerDBO, virtualRegistered: Long): Boolean

    /**
     * Gives feedback to the circle
     *
     * @param circle the circle that should get the feedback
     * @param feedbackPoints
     * @return true if push went fine
     */
    fun giveFeedback(circle: CircleDBO, feedbackPoints: Long): Boolean

    /**
     * Donate points from one practitioner to another
     * @param fromPractitionerId practitioner to remove points from
     * @param toPractitionerId practitioner to add points to
     * @param points number of ponts
     */
    fun donatePoints(
            fromPractitionerId: String, toPractitionerId: String, points: Long)
}