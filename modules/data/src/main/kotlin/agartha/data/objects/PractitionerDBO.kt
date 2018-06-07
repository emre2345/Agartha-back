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
        var fullName: String? = null,
        var email: String? = null,
        var description: String? = null,
        val spiritBankLog: List<SpiritBankLogItemDBO> = listOf( SpiritBankLogItemDBO(type = SpiritBankLogItemType.STARTED, points = 50))
) {
    /**
     * Adds the 'get involved'-information to the practitioner
     *
     * @param fullName
     * @param email
     * @param description
     */
    fun addInvolvedInformation(fullName: String, email: String, description: String) {
        this.fullName = fullName
        this.email = email
        this.description = description
    }

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

    /**
     * Check if practitioner has an ongoing session
     */
    fun hasOngoingSession(): Boolean {
        if (sessions.isEmpty()) {
            return false;
        }
        return sessions.last().ongoing()
    }

    /**
     * Check if practitioner has left 'get involved'-information
     * Function cannot have name isInvolved (considered as property), hence rename
     *
     * @return true if user has left information
     */
    fun involved(): Boolean {
        return this.fullName != null && this.email != null && this.description != null
    }
}