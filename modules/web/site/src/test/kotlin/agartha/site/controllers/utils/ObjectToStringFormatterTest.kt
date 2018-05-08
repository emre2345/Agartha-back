package agartha.site.controllers.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by Jorgen Andersson on 2018-05-08.
 */
class ObjectToStringFormatterTest {

    private val expected = "{\"id\":\"ABC\",\"created\":\"2018-05-08T08:10:52.409Z\"}"

    data class ObjectTester(
            val id: String = "ABC",
            val created: LocalDateTime = LocalDateTime.parse("2018-05-08 08:10:52.409", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")))

    @Test
    fun localDateTime_objectToString_expected() {
        val response: String = ObjectToStringFormatter().getFormatter().writeValueAsString(ObjectTester())
        assertThat(response).isEqualTo(expected)
    }

    @Test
    fun localDateTime_stringToObject_expected() {
        val obj: ObjectTester = ObjectToStringFormatter().getFormatter().readValue(expected, ObjectTester::class.java)
        assertThat(obj.created.year).isEqualTo(2018)
        assertThat(obj.created.monthValue).isEqualTo(5)
        assertThat(obj.created.dayOfMonth).isEqualTo(8)
    }
}