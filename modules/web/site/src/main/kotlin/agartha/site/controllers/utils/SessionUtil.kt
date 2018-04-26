package agartha.site.controllers.utils

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import java.time.LocalDateTime

/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-04-26.
 */
class SessionUtil {

    /**
     * Statics
     */
    companion object {
        /**
         *
         */
        fun filterSessionsBetween(practitioners: List<PractitionerDBO>, startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<SessionDBO> {
            return practitioners
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
                                // Return first overlapping session for each practitioner
                                .first()
                    }
                    // Convert to list
                    .toList()
        }
    }
}