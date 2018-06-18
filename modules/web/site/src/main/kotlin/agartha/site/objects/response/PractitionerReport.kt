package agartha.site.objects.response

import agartha.data.objects.PractitionerDBO

/**
 * Purpose of this file holding information about the current practitioner
 *
 * Created by Jorgen Andersson on 2018-04-23.
 * @param practitionerId database practitioner id
 * @param lastSessionMinutes number of minutes for last session
 * @param totalSessionMinutes total number of minutes for practitioner sessions
 * @param isInvolved has the practitioner registered with name and email address
 */
data class PractitionerReport(
        val practitionerId: String?,
        val lastSessionMinutes: Long = 0,
        val totalSessionMinutes: Long = 0,
        var isInvolved: Boolean = false,
        val spiritBankPoints: Long = 0) {

    /**
     * @param practitioner database practitioner
     */
    constructor(practitioner: PractitionerDBO) : this(
            practitioner._id,
            practitioner.sessions
                    // Get the last session duration time or zero if non exists
                    .lastOrNull()?.sessionDurationMinutes() ?: 0,
            practitioner.sessions
                    // Map to session duration
                    .map {
                        it.sessionDurationMinutes()
                    }
                    // Sum it
                    .sum(),
            practitioner.involved(),
            practitioner.calculateSpiritBankPointsFromLog())

}