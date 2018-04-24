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
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        /**
         * Format a string to datetime object
         * @param dateTimeAsString
         * @return
         */
        fun stringToLocalDateTime(dateTimeAsString: String): LocalDateTime {
            return LocalDateTime.parse(dateTimeAsString, dateTimeFormatter)
        }

        /**
         * Format a LocalDateTime object to use as Mongo Query
         * @param localDateTime object to format
         * @return String for Mongo Query
         */
        fun formatDateTimeAsMongoString(localDateTime: LocalDateTime) : String {
            val dateAsString = localDateTime.toLocalDate().format(dateFormatter)
            val timeAsString = localDateTime.toLocalTime().format(timeFormatter)
            //
            return "${dateAsString}T${timeAsString}.000Z"
        }
    }
}