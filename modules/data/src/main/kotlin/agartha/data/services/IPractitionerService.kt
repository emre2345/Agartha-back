package agartha.data.services

import agartha.data.objects.PractitionerDBO

/**
 * Purpose of this file is inteface for Practitioner datasource service
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
interface IPractitionerService : IBaseService<PractitionerDBO> {

    /**
     * Get number of active sessions
     * @return number of users with active session
     */
    fun getActiveCount(): Int

    /**
     * Start a new session
     * @param userId id for user starting the session
     * @param practition type of practice
     * @return session id / index for session for this user
     */
    fun startSession(userId: String, practition: String): Int

    /**
     * End an ongoing session
     * @param userId id for user for whom we should stop session
     * @param sessionId id for session to stop
     */
    fun endSession(userId: String, sessionId: Int)
}