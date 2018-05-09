package agartha.site.objects.response

import agartha.data.objects.SessionDBO

/**
 * Purpose of this file is holding information about a set of practitioner's sessions
 *
 * Created by Jorgen Andersson on 2018-04-24.
 * @param companionCount number of companions for the practitioner
 * @param sessionCount number of sessions
 * @param sessionSumMinutes duration in minutes for these sessions
 * @param intentions Map of intentions to number of sessions
 */
data class CompanionReport(
        val companionCount: Int,
        val sessionCount: Int,
        val sessionSumMinutes: Long,
        val intentions : Map<String, Int>) {

    /**
     * Constructor where multiple sessions per practitioner can be counted
     * @param companionCount
     * @param sessions
     */
    constructor(companionCount: Int, sessions: List<SessionDBO>) : this(
            // Number of practitioners
            companionCount,
            // Count number of session
            sessions.count(),
            // Sum duration for sessions
            sessions.map { it.sessionDurationMinutes() }.sum(),
            // map session count to each practice
            sessions.groupBy { it.intention }.map { it.key to it.value.size }.toMap()
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