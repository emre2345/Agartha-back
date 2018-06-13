package agartha.data.services

import agartha.data.objects.CircleDBO
import agartha.data.objects.GeolocationDBO
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
     * @param item to be inserted
     * @return inserted document as object
     */
    fun updatePractitionerWithInvolvedInformation(
            user: PractitionerDBO,
            fullName: String,
            email: String,
            description: String): PractitionerDBO

    /**
     * Start a new session/practice
     * @param practitionerId id for user starting the session
     * @param session session to Add to user
     * @return the started session
     */
    fun startSession(
            practitionerId: String,
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
            practitionerId: String, circle: CircleDBO) : PractitionerDBO?

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
}