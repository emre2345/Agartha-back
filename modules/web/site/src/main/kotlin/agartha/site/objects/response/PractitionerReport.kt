package agartha.site.objects.response

import agartha.data.objects.PractitionerDBO

/**
 * Purpose of this file holding information about the current user/practitioner
 *
 * Created by Jorgen Andersson on 2018-04-23.
 * @param practitionerId database user id
 * @param lastSessionTime number of minutes for last session
 * @param totalSessionTime total number of minutes for user sessions
 */
data class PractitionerReport(val practitionerId: String?, val lastSessionTime: Long = 0, val totalSessionTime: Long = 0, var isInvolved: Boolean = false) {

    /**
     * @param practitioner database users
     */
    constructor(user: PractitionerDBO) : this(
            user._id,
            user.sessions
                    // Get the last session duration time or zero if non exists
                    .lastOrNull()?.sessionDurationMinutes() ?: 0,
            user.sessions
                    // Map to session duration
                    .map {
                        it.sessionDurationMinutes()
                    }
                    // Sum it
                    .sum(),
            user.involved())

}