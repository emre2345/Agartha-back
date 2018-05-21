package agartha.site.objects.response

import agartha.data.objects.GeolocationDBO
import agartha.data.objects.SessionDBO
import java.time.LocalDateTime

/**
 * Purpose of this file is holding information about a set of practitioner's sessions
 *
 * Created by Rebecca Fransson on 2018-05-07.
 */
data class CompanionsSessionReport(
        // Session related data
        val geolocation: GeolocationDBO? = null,
        val discipline: String,
        val intention: String,
        val startTime: LocalDateTime = LocalDateTime.now(),
        val endTime: LocalDateTime? = null) {

    /**
     * constructor that creates the companionSession object from the sessionSBO
     */
    constructor(session: SessionDBO) : this(
            session.geolocation,
            session.discipline,
            session.intention,
            session.startTime,
            session.endTime)
}
