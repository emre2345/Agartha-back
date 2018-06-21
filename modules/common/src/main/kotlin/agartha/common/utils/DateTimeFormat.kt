package agartha.common.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Purpose of this file is DateTime utilities
 *
 * Created by Jorgen Andersson on 2018-04-24.
 */
class DateTimeFormat {


    companion object {
        private val dateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("UTC"))

        /**
         * Format a string to datetime object
         *
         * @param dateTimeAsString
         * @return
         */
        fun stringToLocalDateTime(dateTimeAsString: String): LocalDateTime {
            return LocalDateTime.parse(dateTimeAsString, dateTimeFormatter)
        }

        fun stringToUTC(dateTimeAsString: String): LocalDateTime {
            return LocalDateTime.ofInstant(stringToIntant(dateTimeAsString), ZoneId.of("UTC"))
        }

        fun localDateTimeUTC(): LocalDateTime {
            return LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"))
        }

        private fun stringToIntant(dateTimeAsString: String): Instant {
            return Instant.parse(dateTimeAsString)
        }
    }

}