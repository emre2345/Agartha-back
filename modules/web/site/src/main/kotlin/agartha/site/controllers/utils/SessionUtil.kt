package agartha.site.controllers.utils

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import java.time.LocalDateTime

/**
 * Purpose of this file is utiltites for Sessions
 *
 * Created by Jorgen Andersson on 2018-04-26.
 */
class SessionUtil {

    /**
     * Statics
     */
    companion object {

        /**
         * Extract sessions from practitioner based on their start/end time
         * Max one session can be counted per practitioner since it is not possible have two concurrent sessions
         *
         * @param practitioners list of practitioner
         * @param practitionerId current practitioner
         * @param startDateTime
         * @param endDateTime
         * @return Sessions with overlapping start/end time
         */
        fun filterSessionsBetween(
                practitioners: List<PractitionerDBO>,
                practitionerId: String,
                startDateTime: LocalDateTime,
                endDateTime: LocalDateTime): List<SessionDBO> {
            return practitioners
                    // Filter out current user id
                    .filter {
                        it._id != practitionerId
                    }
                    // Filter out those with overlapping sessions
                    .filter {
                        it.hasSessionBetween(startDateTime, endDateTime)
                    }
                    // Map to first matching overlapping session
                    .map {
                        // Filter out overlapping sessions
                        it.sessions
                                .filter {
                                    // Start time should be between
                                    it.sessionOverlap(startDateTime, endDateTime)
                                }
                                // Return last overlapping session for each practitioner
                                .last()
                    }
                    // Convert to list
                    .toList()
        }
    }
}