package agartha.data.objects

import agartha.common.config.Settings
import java.time.Duration
import java.time.LocalDateTime


/**
 * Purpose of this file is data object for a practitioner's session
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
data class SessionDBO(
        // Session index for this user
        val index: Int,
        // Geolocation for current session/practice
        val geolocation: GeolocationDBO? = null,
        // Type of discipline
        val discipline: String,
        // Type of intention on the practice
        val intention: String,
        // Time when started
        val startTime: LocalDateTime = LocalDateTime.now(),
        // Time when ended
        val endTime: LocalDateTime? = null) {


    /**
     * Function to calculate duration for a session
     * @return number of minutes for session
     */
    fun sessionDurationMinutes(): Long {
        // If the session has been abandoned, inactive for too long for user to still be active
        if (isAbandoned()) {
            return 0
        }
        // If the session has not ended yet, return diff between now and start time
        if (endTime == null) {
            return Duration.between(startTime, LocalDateTime.now()).toMinutes()
        }
        // If session is ended return diff between end and start time
        return Duration.between(startTime, endTime).toMinutes()
    }

    /**
     * Function to see if this session was ongoing between these dates
     *
     * @param startTime
     * @param endTime
     * @return true if session was active during these timestamps
     */
    fun sessionOverlap(startTime: LocalDateTime, endTime: LocalDateTime): Boolean {
        // If session has an end time and it is between these dates
        if (this.endTime != null && this.endTime.isAfter(startTime) && this.endTime.isBefore(endTime)) {
            return true
        }
        // If session has a start time between these dates and is not condistered abandoned
        if (this.startTime.isAfter(startTime) && this.startTime.isBefore(endTime) && !isAbandoned()) {
            return true
        }
        // I session not have an end time (ongoing) and not abandoned
        if (this.endTime == null && !isAbandoned()) {
            return true
        }
        // Session has an end time and not in between start and stop
        return false
    }

    /**
     * A session is considered abandon if user has not finshed session within this number of minutes
     * @return true if session is considered abandon, otherwise false
     */
    private fun isAbandoned(): Boolean {
        // If end time is null (session not finished) and session started more than 3 hours ago
        return this.endTime == null &&
                this.startTime.isBefore(LocalDateTime.now().minusMinutes(Settings.ABONDON_SESSION_MINUTES))
    }
}
