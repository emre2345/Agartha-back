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
     * Get all practitioners with session after a specific datetime
     *
     * @param startDate
     */
    fun getPractitionersWithSessionAfter(startDate : LocalDateTime): List<PractitionerDBO>

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
     * Counts all the ongoing sessions and matching them with an user
     * @param user the user that the match should focus on
     * @return a list of sorted sessions
     */
    fun matchSessions(user: PractitionerDBO): List<SessionDBO>
}