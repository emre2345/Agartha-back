package agartha.site.objects.response

import agartha.data.objects.GeolocationDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import java.time.LocalDateTime

/**
 * Purpose of this file is holding information about a set of practitioner's sessions
 *
 * Created by Rebecca Fransson on 2018-05-07.
 */
data class CompanionsSessionReport(
        val index: Int,
        val geolocation: GeolocationDBO? = null,
        val discipline: String,
        val practice: String? = null,
        val intention: String,
        val startTime: LocalDateTime = LocalDateTime.now(),
        val endTime: LocalDateTime? = null) {

    // The number of match-points this companions session has, 0 = no match and 3 = full match
    val matchPoints: Number = 0

    /**
     * constructor that creates the companionSession object from the sessionSBO
     */
    constructor(session: SessionDBO) : this(
            session.index,
            session.geolocation,
            session.discipline,
            session.practice,
            session.intention,
            session.startTime,
            session.endTime
    )

    /**
     * @param user that
     * @return number of ponits for this match
     */
    fun giveMatchPoints(user: PractitionerDBO): Number{
        return 1
    }
}
