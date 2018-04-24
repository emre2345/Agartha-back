package agartha.data.objects

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
        // Type of practitioning
        val practition: String,
        // Is this still active
        val active: Boolean = true,
        // Time when started
        val startTime: LocalDateTime = LocalDateTime.now(),
        // Time when ended
        val endTime: LocalDateTime? = null) {


    fun sessionDurationMinutes() : Long {
        // If the session has been abandoned, inactive for too long for user to still be active
        if (endTime == null && !active) {
            return 0
        }
        // If the session has not ended yet, return diff between now and start time
        if (endTime == null) {
            return Duration.between(startTime, LocalDateTime.now()).toMinutes()
        }
        // If session is ended return diff between end and start time
        return Duration.between(startTime, endTime).toMinutes()
    }
}
