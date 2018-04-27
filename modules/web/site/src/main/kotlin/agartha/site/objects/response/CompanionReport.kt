package agartha.site.objects.response

import agartha.data.objects.SessionDBO

/**
 * Purpose of this file is holding information about a set of practitioner's sessions
 *
 * Created by Jorgen Andersson on 2018-04-24.
 * @param practitionerCount number of practitioners
 * @param sessionCount number of sessions
 * @param sessionMinutes duration in minutes for these sessions
 * @param practices Map of practices to number of sessions
 */
data class CompanionReport(
        val practitionerCount: Int,
        val sessionCount: Int,
        val sessionMinutes: Long,
        val practices : Map<String, Int>) {

    /**
     * Constructor where multiple sessions per practitioner can be counted
     * @param practitionerCount
     * @param sessions
     */
    constructor(practitionerCount: Int, sessions: List<SessionDBO>) : this(
            // Number of practitioners
            practitionerCount,
            // Count number of session
            sessions.count(),
            // Sum duration for sessions
            sessions.map { it.sessionDurationMinutes() }.sum(),
            // map session count to each practice
            sessions.groupBy { it.practition }.map { it.key to it.value.size }.toMap()
    )

    /**
     * Constructor where only one session per practitioner counts
     * @param sessions
     */
    constructor(sessions: List<SessionDBO>) : this(
            // Number of practitioners
            sessions.count(),
            // Count number of session
            sessions)
}