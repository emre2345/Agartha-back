package agartha.data.services

import agartha.data.objects.PractitionerDBO
import java.time.LocalDateTime

/**
 * Purpose of this file is inteface for Practitioner datasource service
 *
 * Created by Jorgen Andersson on 2018-04-25.
 */
interface ISessionService : IBaseService<PractitionerDBO> {

    /**
     * Start a new session
     * @param practitionerId id for user starting the session
     * @param practiceName type of practice
     * @return session id / index for session for this user
     */
    fun startSession(practitionerId: String, practiceName: String, intentionName: String): Int

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