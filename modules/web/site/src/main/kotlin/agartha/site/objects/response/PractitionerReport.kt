package agartha.site.objects.response

import agartha.data.objects.PractitionerDBO

/**
 * Purpose of this file holding information about the current user/practitioner
 *
 * Created by Jorgen Andersson on 2018-04-23.
 * @param practitionerId database user id
 * @param lastSessionMinutes number of minutes for last session
 * @param totalSessionMinutes total number of minutes for user sessions
 * @param isInvolved has the user registered with name and email address
 */
data class PractitionerReport(
        val practitionerId: String?,
        val lastSessionMinutes: Long = 0,
        val totalSessionMinutes: Long = 0,
        var isInvolved: Boolean = false,
        val spiritBankPoints: Long = 0) {

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
            user.involved(),
            user.calculateSpiritBankPointsFromLog())

}