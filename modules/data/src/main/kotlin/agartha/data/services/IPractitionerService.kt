package agartha.data.services

import agartha.data.objects.*
import java.time.LocalDateTime

/**
 * Purpose of this file is inteface for Practitioner datasource service
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
     * @param geolocation geolocation for session/practice
     * @param disciplineName type of discipline
     * @param intentionName type of intention
     * @return the started session
     */
    fun startSession(
            practitionerId: String,
            geolocation: GeolocationDBO?,
            disciplineName: String,
            intentionName: String): SessionDBO

    /**
     * @param practitionerId id for user ending a session
     */
    fun endSession(
            practitionerId: String): Boolean
}