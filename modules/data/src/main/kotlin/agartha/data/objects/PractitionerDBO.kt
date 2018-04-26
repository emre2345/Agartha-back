package agartha.data.objects

import java.time.LocalDateTime

/**
 * Purpose of this file is representing data object for a practicing person
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
data class PractitionerDBO(
        val _id: String? = null,
        val created: LocalDateTime = LocalDateTime.now(),
        val sessions: List<SessionDBO> = listOf(),
        val fullName: String? = null,
        val email: String? = null,
        val description: String? = null) {

    /**
     * Check if practitioner has at least one session considered active in this timespan
     *
     * @param startDateTime
     * @param endDateTime
     * @return true if user has at least one session in this timespan
     */
    fun hasSessionBetween(startDateTime: LocalDateTime, endDateTime: LocalDateTime): Boolean {
        return this.sessions
                .filter {
                    it.sessionOverlap(startDateTime, endDateTime)
                }
                .isNotEmpty()
    }
}