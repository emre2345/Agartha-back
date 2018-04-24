package agartha.site.objects

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Purpose of this file is Test for Practitioner response object
 *
 * Created by Jorgen Andersson on 2018-04-23.
 */
class PractitionerTest {

    fun createDate(dayOfMonth: String, minutesOfHour: String): LocalDateTime {
        return LocalDateTime.parse("2018-04-${dayOfMonth} 12:${minutesOfHour}:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    @Test
    fun practitionerLastSessionTime_WithNoSessions_zero() {
        val user = Practitioner(null)
        assertThat(user.lastSessionTime).isEqualTo(0)
    }

    @Test
    fun practitionerLastSessionTime_WithSessions_20() {
        val user = Practitioner(PractitionerDBO(
                listOf(
                        SessionDBO(0, "Yoga", false, createDate("18", "00"), createDate("18", "40")),
                        SessionDBO(1, "Mindfulness", false, createDate("19", "00"), createDate("19", "30")),
                        SessionDBO(2, "Yes", false, createDate("20", "00"), createDate("20", "20"))
                ),
                LocalDateTime.now(),
                "abc"

        ))
        assertThat(user.lastSessionTime).isEqualTo(20)
    }

    @Test
    fun practitionerTotalSessionTime_WithNoSessions_zero() {
        val user = Practitioner(null)
        assertThat(user.totalSessionTime).isEqualTo(0)
    }

    @Test
    fun practitionerTotalSessionTime_WithSessions_80() {
        val user = Practitioner(PractitionerDBO(
                listOf(
                        SessionDBO(0, "Yoga", false, createDate("18", "00"), createDate("18", "40")),
                        SessionDBO(1, "Mindfulness", false, createDate("19", "00"), createDate("19", "30")),
                        SessionDBO(2, "Yes", false, createDate("20", "00"), createDate("20", "20"))
                ),
                LocalDateTime.now(),
                "abc"

        ))
        assertThat(user.totalSessionTime).isEqualTo(90)
    }
}