package agartha.site.controllers.utils

import agartha.data.objects.GeolocationDBO
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


        fun hovno(geolocation: GeolocationDBO, sessions: List<SessionDBO>) {
            val closesDistance = sessions
                    .filter {
                        it.geolocation != null
                    }
                    .map {
                        distanceInKilometer(geolocation, it.geolocation as GeolocationDBO)
                    }
                    .sorted()
                    .first()
        }

        /**
         * https://stackoverflow.com/questions/18170131/comparing-two-locations-using-their-longitude-and-latitude
         */
        private fun distanceInKilometer(geolocation1: GeolocationDBO, geolocation2: GeolocationDBO): Double {
            //val earthRadiusMiles: Double = 3958.75
            val earthRadiusKm: Double = 6371.0

            val dLat = Math.toRadians(geolocation2.latitude - geolocation1.latitude)
            val dLng = Math.toRadians(geolocation2.longitude - geolocation1.longitude)

            val sindLat = Math.sin(dLat / 2)
            val sindLng = Math.sin(dLng / 2)

            val a = Math.pow(sindLat, 2.0) + (Math.pow(sindLng, 2.0)
                    * Math.cos(Math.toRadians(geolocation1.latitude)) * Math.cos(Math.toRadians(geolocation2.latitude)))

            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

            return earthRadiusKm * c
        }
    }

}