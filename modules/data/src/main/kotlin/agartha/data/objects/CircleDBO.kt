package agartha.data.objects

import agartha.common.utils.DateTimeFormat
import org.bson.types.ObjectId
import java.time.LocalDateTime

/**
 * Purpose of this file is data object for a practitioner's circle
 *
 * Created by Jorgen Andersson on 2018-06-07.
 */
data class CircleDBO(
        val _id: String = ObjectId().toHexString(),
        val name: String,
        val description: String,
        val geolocation: GeolocationDBO? = null,
        // Time when started
        val startTime: LocalDateTime,
        // Time when ended
        val endTime: LocalDateTime,
        val intentions: List<IntentionDBO>,
        val disciplines: List<DisciplineDBO>,
        val minimumSpiritContribution: Long,
        val language: String,
        // Number of virtual registered to this circle
        val virtualRegistered: Long = 0,
        // Feedback in the form of numbers, the range is decided by the client
        val feedback: List<Long> = emptyList()) {

    /**
     * Function to see if circle is active at this moment
     * @return true if circle is active now
     */
    fun active(): Boolean {
        return this.startTime.isBefore(DateTimeFormat.localDateTimeUTC()) and this.endTime.isAfter(DateTimeFormat.localDateTimeUTC())
    }
}
