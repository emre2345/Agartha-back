package agartha.site.controllers.utils

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
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
         * Extract sessions from practitioner based on their session start/end time
         * Max one session can be counted per practitioner since it is not possible have two concurrent sessions
         *
         * @param practitioners list of practitioner
         * @param practitionerId current practitioner
         * @param startDateTime
         * @param endDateTime
         * @return Sessions with overlapping start/end time
         */
        fun filterSingleSessionActiveBetween(
                practitioners: List<PractitionerDBO>,
                practitionerId: String,
                startDateTime: LocalDateTime,
                endDateTime: LocalDateTime): List<SessionDBO> {
            return practitioners
                    // Filter out current user id
                    .filter { it._id != practitionerId }
                    // Map to first matching overlapping session
                    .map {
                        // Filter out overlapping sessions
                        it.sessions
                                .filter {
                                    // Session was active during any time during these dateTimes
                                    it.sessionOverlap(startDateTime, endDateTime)
                                }
                                // Return last overlapping session for each practitioner
                                .last()
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
    }
}