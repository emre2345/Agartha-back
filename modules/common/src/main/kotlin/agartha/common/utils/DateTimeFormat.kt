package agartha.common.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Purpose of this file is DateTime utilities
 *
 * Created by Jorgen Andersson on 2018-04-24.
 */
class DateTimeFormat {


    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        /**
         * Format a string to datetime object
         *
         * @param dateTimeAsString
         * @return
         */
        fun stringToLocalDateTime(dateTimeAsString: String): LocalDateTime {
            return LocalDateTime.parse(dateTimeAsString, dateTimeFormatter)
        }
    }
}