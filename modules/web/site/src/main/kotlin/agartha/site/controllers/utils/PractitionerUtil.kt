package agartha.site.controllers.utils

import agartha.data.objects.PractitionerDBO
import java.time.LocalDateTime

/**
 * Purpose of this file is utilities for Practitioners
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-05-03.
 */
class PractitionerUtil {

    companion object {
        /**
         * Filter out the practitioners that has session between a start and a end date
         */
        fun filterPractitionerWithSessionsBetween(
                practitioners: List<PractitionerDBO>,
                startDateTime: LocalDateTime,
                endDateTime: LocalDateTime) : List<PractitionerDBO> {

            return practitioners
                    // Filter out those with existing sessions
                    .filter { it.sessions.isNotEmpty() }
                    // Filter out those with overlapping sessions
                    .filter { it.hasSessionBetween(startDateTime, endDateTime) }

        }
    }
}