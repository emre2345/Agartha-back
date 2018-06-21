package agartha.common.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Test
import java.time.LocalDateTime

/**
 * Created by Jorgen Andersson on 2018-06-21.
 */
class DateTimeFormatTest {

    @Ignore
    @Test
    fun testSomething() {

        val ldtStd = LocalDateTime.now()
        val ldtUtc = DateTimeFormat.localDateTimeUTC()

        println(ldtStd)
        println(ldtUtc)

        assertThat(1).isEqualTo(2)

    }
}