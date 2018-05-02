package agartha.data.services

import agartha.data.objects.GeolocationDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import java.time.LocalDateTime

/**
 * Purpose of this file is inteface for Practitioner datasource service
 *
 * Created by Jorgen Andersson on 2018-04-25.
 */
interface ISessionService : IBaseService<PractitionerDBO> {

    /**
     * Start a new session/practice
     * @param practitionerId id for user starting the session
     * @param geolocation geolocation for session/practice
     * @param disciplineName type of discipline
     * @param practiceName type of practice
     * @param intentionName type of intention
     * @return the started session
     */
    fun startSession(
            practitionerId: String,
            geolocation: GeolocationDBO?,
            disciplineName: String,
            practiceName: String?,
            intentionName: String): SessionDBO

    /**
     * End an ongoing session
     * @param userId id for user for whom we should stop session
     * @param sessionId id for session to stop
     */
    fun endSession(userId: String, sessionId: Int)

    /**
     * Get all pracitioners with session started after this date time
     */
    fun getPractitionersWithSessionAfter(startDateTime: LocalDateTime): List<PractitionerDBO>
}