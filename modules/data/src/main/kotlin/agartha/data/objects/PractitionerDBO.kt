package agartha.data.objects

import agartha.common.utils.DateTimeFormat
import java.time.LocalDateTime

/**
 * Purpose of this file is representing data object for a practicing person
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
data class PractitionerDBO(
        val _id: String? = null,
        val created: LocalDateTime = DateTimeFormat.localDateTimeUTC(),
        val sessions: List<SessionDBO> = listOf(),
        val circles: List<CircleDBO> = listOf(),
        var fullName: String? = null,
        var email: String? = null,
        var description: String? = null,
        val spiritBankLog: List<SpiritBankLogItemDBO> = listOf( SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = 50)),
        // List of _id connected to a circle that the practitioner has registered to
        val registeredCircles: List<String> = listOf()
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
        return this.sessions.any {
            it.sessionOverlap(startDateTime, endDateTime)
        }
    }

    /**
     * Check if practitioner has any sessions with a specific circle
     * and if those sessions is started after the given startTime
     *
     * @param startDateTime - when session started
     * @param circle        - a specific circle
     * @return true if user has at least one session after the given startTime
     */
    fun hasSessionInCircleAfterStartTime(startDateTime: LocalDateTime, circle: CircleDBO): Boolean {
        return this.sessions
                .filter {
                    circle == it.circle
                }.any {
                    it.sessionAfter(startDateTime)
                }
    }

    /**
     * Check if practitioner has an ongoing session
     */
    fun hasOngoingSession(): Boolean {
        if (sessions.isEmpty()) {
            return false
        }
        return sessions.last().ongoing()
    }

    /**
     * Check if practitioner is a creator of a specific circle
     * by looking at all the practitioners circles and looking for the one with the right id
     */
    fun creatorOfCircle(circle: CircleDBO?): Boolean {
        val hej = this.circles.firstOrNull{ it._id == circle?._id }
        return hej != null
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

    /**
     * Calculates the sum of all the points in the practitioners spiritBankLog
     */
    fun calculateSpiritBankPointsFromLog(): Long {
        return this.spiritBankLog.map { it.points }.sum()
    }
}