package agartha.site.objects

import agartha.data.objects.SessionDBO

/**
 * Purpose of this file is holding information about practitioners sessions
 *
 * Created by Jorgen Andersson on 2018-04-24.
 * @param count number of sessions
 * @param minutes duration in minutes for these sessions
 */
data class Companion(val count: Int, val minutes: Long, val practices : Map<String, Int>) {

    constructor(sessions: List<SessionDBO>) : this(
            // Count number of session
            sessions.count(),
            // Sum duration for sessions
            sessions.map { it.sessionDurationMinutes() }.sum(),
            // map session count to each practice
            sessions.groupBy { it.practition }.map { it.key to it.value.size }.toMap()
    )

}