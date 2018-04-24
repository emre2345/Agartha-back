package agartha.data.objects

import agartha.common.utils.DateTimeFormat
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime

class SessionDBOTest {

    @Test
    fun sessionDuration_whithNullEndTime_3() {
        val session = SessionDBO(0, "Yoga", true,
                LocalDateTime.now().minusMinutes(3),
                null)
        assertThat(session.sessionDurationMinutes()).isEqualTo(3)
    }

    @Test
    fun sessionDuration_withEndTime_10() {
        val session = SessionDBO(0, "Yoga", true,
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:17:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:27:00"))
        assertThat(session.sessionDurationMinutes()).isEqualTo(10)
    }

    @Test
    fun sessionDuration_withUnevenMinutes_5() {
        val session = SessionDBO(0, "Yoga", true,
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:15:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:20:10"))
        assertThat(session.sessionDurationMinutes()).isEqualTo(5)
    }

    @Test
    fun sessionDuration_floorMinutes_5() {
        val session = SessionDBO(0, "Yoga", true,
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:15:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:20:50"))
        assertThat(session.sessionDurationMinutes()).isEqualTo(5)
    }}