package agartha.site.controllers.utils

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import java.time.LocalDateTime

/**
 * Purpose of this file is utilities for Sessions
 *
 * Created by Jorgen Andersson on 2018-04-26.
 */
class SessionUtil {

    /**
     * Statics
     */
    companion object {

        /**
         * Extract sessions from practitioner based on their session is ongoing
         * Max one session can be counted per practitioner since it is not possible have two concurrent sessions
         * The argument practitioner Id is omitted
         * Abandon sessions are omitted
         *
         * @param practitioners list of practitioner
         * @param practitionerId current practitioner
         * @return List of ongoing sessions
         */
        fun filterSingleOngoingSession(
                practitioners: List<PractitionerDBO>,
                practitionerId: String): List<SessionDBO> {
            return practitioners
                    // Filter out current practitioner id
                    .filter { it._id != practitionerId }
                    .filter { it.hasOngoingSession() }
                    // Map to first matching overlapping session
                    .map {
                        // Map to last session and return
                        it.sessions.last()
                    }
        }

        /**
         * Extract all session from practitioner based on session start/end time
         * No consideration is taken on number of sessions per practitioner
         *
         * @param practitioners list of practitioner
         * @param startDateTime
         * @param endDateTime
         * @return Sessions with overlapping start/end time
         */
        fun filterAllSessionsActiveBetween(
                practitioners: List<PractitionerDBO>,
                startDateTime: LocalDateTime,
                endDateTime: LocalDateTime): List<SessionDBO> {
            return practitioners
                    .flatMap { it.sessions }
                    // Session was active during any time during these dateTimes
                    .filter { it.sessionOverlap(startDateTime, endDateTime) }
        }

        /**
         * Extract currently ongoing sessions
         * Max one session can be counted per practitioner since it is not possible have two concurrent sessions
         *
         * @param practitioners list of practitioners
         * @param practitionerId id for current practitioner
         * @return list of ongoing sessions
         */
        fun filterOngoingSessions(practitioners: List<PractitionerDBO>, practitionerId: String): List<SessionDBO> {
            return practitioners
                    // Filter out current practitioner
                    .filter { it._id != practitionerId }
                    // Filter out those without session to avoid null pointer exception in map below
                    .filter { it.sessions.isNotEmpty() }
                    // Get the last/latest session
                    .map { it.sessions.last() }
                    // Filter out the abandon and finished
                    .filter { it.ongoing() }
        }

        /**
         * Get all sessions for a specific circle
         *
         * @param practitioners list of practitioners
         * @param circleId Id of circle
         * @return list of sessions within this circle
         */
        fun getAllSessionsInCircle(practitioners: List<PractitionerDBO>, circleId: String): List<SessionDBO> {
            return practitioners
                    // Extract all sessions
                    .flatMap { it.sessions }
                    // Filter out all sessions for circles
                    .filter { it.circle != null }
                    // Filter out those with this circle Id
                    .filter { it.circle?._id == circleId }
        }
    }

}