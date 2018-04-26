package agartha.site.objects.response

import agartha.data.objects.SessionDBO

/**
 * Purpose of this file holding information about the current user/practitioner
 *
 * Created by Jorgen Andersson on 2018-04-23.
 * @param practitionerId database user id
 * @param lastSessionTime number of minutes for last session
 * @param totalSessionTime total number of minutes for user sessions
 */
data class PractitionerReport(val practitionerId: String?, val lastSessionTime: Long = 0, val totalSessionTime: Long = 0) {

    /**
     * @param practitioner database users
     */
    constructor(userId: String?, sessions: List<SessionDBO>) : this(
            userId,
            sessions
                    // Get the last session duration time or zero if non exists
                    .lastOrNull()?.sessionDurationMinutes() ?: 0,
            sessions
                    // Map to session duration
                    .map {
                        it.sessionDurationMinutes()
                    }
                    // Sum it
                    .sum())

}