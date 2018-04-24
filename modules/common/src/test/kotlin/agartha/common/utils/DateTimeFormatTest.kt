package agartha.common.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class DateTimeFormatTest {

    @Test
    fun dateTimeFormat_testFormat_sameAsStart() {
        val dateAsString = "2018-04-15 17:30:00"
        val localDateTime = DateTimeFormat.stringToLocalDateTime(dateAsString)
        val mongoDateTime = DateTimeFormat.formatDateTimeAsMongoString(localDateTime)
        assertThat(mongoDateTime).isEqualTo("2018-04-15T17:30:00.000Z")
    }

}