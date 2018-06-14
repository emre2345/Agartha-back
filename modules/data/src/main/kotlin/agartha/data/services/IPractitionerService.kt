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
            practitionerId: String, contributionPoints: Long): PractitionerDBO?

    /**
     * Add a circle to a practitioner
     * @param practitionerId practitioner id to add circle to
     * @param circle circle to add to practitioner
     * @return
     */
    fun addCircle(
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
     * Remove a circle from practitioner
     * @param practitionerId id for practitioner to remove from
     * @param circleId id for circle to remove
     * @return true if circle was removed
     */
    fun removeCircleById(
            practitionerId: String, circleId: String): Boolean
}