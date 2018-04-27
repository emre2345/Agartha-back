package agartha.site.objects

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.site.objects.response.PractitionerReport
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Purpose of this file is Test for PractitionerReport response object
 *
 * Created by Jorgen Andersson on 2018-04-23.
 */
class PractitionerReportTest {

    private fun generateSessions() : List<SessionDBO> {
        return listOf(
                SessionDBO(0, "Yoga", false,
                        DateTimeFormat.stringToLocalDateTime("2018-04-18 12:00:00"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-18 12:40:00")),
                SessionDBO(1, "Mindfulness", false,
                        DateTimeFormat.stringToLocalDateTime("2018-04-19 12:00:00"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-19 12:30:00")),
                SessionDBO(2, "Meditation", false,
                        DateTimeFormat.stringToLocalDateTime("2018-04-20 12:00:00"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-20 12:20:00"))
        )
    }

    /**
     *
     */
    @Test
    fun practitionerLastSessionTime_WithNoSessions_zero() {
        val user = PractitionerReport(null)
        assertThat(user.lastSessionTime).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun practitionerLastSessionTime_WithSessions_20() {
        val user = PractitionerReport(PractitionerDBO("abc", sessions = generateSessions()))
        assertThat(user.lastSessionTime).isEqualTo(20)
    }

    /**
     *
     */
    @Test
    fun practitionerTotalSessionTime_WithNoSessions_zero() {
        val user = PractitionerReport(null)
        assertThat(user.totalSessionTime).isEqualTo(0)
    }

    /**
     * 
     */
    @Test
    fun practitionerTotalSessionTime_WithSessions_80() {
        val user = PractitionerReport(PractitionerDBO("abc", sessions = generateSessions()))
        assertThat(user.totalSessionTime).isEqualTo(90)
    }
}