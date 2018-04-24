package agartha.site.objects

import agartha.data.objects.PractitionerDBO

/**
 * Purpose of this file is response object for the current Practitioner
 *
 * Created by Jorgen Andersson on 2018-04-23.
 * @param userId database user id
 * @param lastSessionTime number of minutes for last session
 * @param totalSessionTime total number of minutes for user sessions
 */
class Practitioner(val userId: String?, val lastSessionTime: Long = 0, val totalSessionTime: Long = 0) {

    /**
     * @param practitioner datbase users
     */
    constructor(practitioner: PractitionerDBO) : this(
            practitioner._id,
            practitioner
                    // For each session
                    .sessions
                    // Get the last session duration time or zero if non exists
                    .lastOrNull()?.sessionDurationMinutes() ?: 0,
            practitioner
                    // For each session
                    .sessions
                    // Map to session duration
                    .map { it.sessionDurationMinutes() }
                    // Sum it
                    .sum())

}