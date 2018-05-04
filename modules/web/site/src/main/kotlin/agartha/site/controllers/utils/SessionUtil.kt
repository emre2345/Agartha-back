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

        /**
         * Find the distance from a geolocation to the closest session
         *
         * @param geolocation starting point
         * @param sessions list of sessions to find closest session from
         * @return distance in km to closest session
         */
        fun distanceToClosestSession(geolocation: GeolocationDBO, sessions: List<SessionDBO>) : Double {
            return sessions
                    // Filter out those with no geolocation
                    .filter {
                        it.geolocation != null
                    }
                    // Map to double
                    .map {
                        // The null ones have already been filtered out, this cast from nullable is safe (or it.geolocation!!)
                        distanceInKilometer(geolocation, it.geolocation as GeolocationDBO)
                    }
                    // Sort doubles ascending
                    .sorted()
                    // First one is the closest
                    .first()
        }

        /**
         * Function for calculating distance between two geo-locations
         * Source:
         * https://stackoverflow.com/questions/18170131/comparing-two-locations-using-their-longitude-and-latitude
         * if we want option for miles, val earthRadiusMiles: Double = 3958.75
         *
         * @param geolocation1
         * @param geolocation2
         * @return distance in km as Double
         */
        private fun distanceInKilometer(geolocation1: GeolocationDBO, geolocation2: GeolocationDBO): Double {

            // Equator radius: 6 378 km
            // Polar radius: 6 356 km
            val earthRadiusKm: Double = 6370.6934

            val dLat: Double = Math.toRadians(geolocation2.latitude - geolocation1.latitude)
            val dLng: Double = Math.toRadians(geolocation2.longitude - geolocation1.longitude)

            val sindLat: Double = Math.sin(dLat / 2)
            val sindLng: Double = Math.sin(dLng / 2)

            val a: Double = Math.pow(sindLat, 2.0) + (Math.pow(sindLng, 2.0)
                    * Math.cos(Math.toRadians(geolocation1.latitude))
                    * Math.cos(Math.toRadians(geolocation2.latitude)))

            val c: Double = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

            return earthRadiusKm * c
        }
    }

}