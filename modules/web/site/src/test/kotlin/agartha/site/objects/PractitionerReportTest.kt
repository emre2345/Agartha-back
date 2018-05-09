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
                SessionDBO(0, null,"Yoga", "Wellbeing",
                        DateTimeFormat.stringToLocalDateTime("2018-04-18 12:00:00"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-18 12:40:00")),
                SessionDBO(1, null,"Meditation","Wellbeing",
                        DateTimeFormat.stringToLocalDateTime("2018-04-19 12:00:00"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-19 12:30:00")),
                SessionDBO(2,null, "Meditation","Wellbeing",
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
        assertThat(user.lastSessionMinutes).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun practitionerLastSessionTime_WithSessions_20() {
        val user = PractitionerReport(PractitionerDBO("abc", sessions = generateSessions()))
        assertThat(user.lastSessionMinutes).isEqualTo(20)
    }

    /**
     *
     */
    @Test
    fun practitionerTotalSessionTime_WithNoSessions_zero() {
        val user = PractitionerReport(null)
        assertThat(user.totalSessionMinutes).isEqualTo(0)
    }

    /**
     * 
     */
    @Test
    fun practitionerTotalSessionTime_WithSessions_80() {
        val user = PractitionerReport(PractitionerDBO("abc", sessions = generateSessions()))
        assertThat(user.totalSessionMinutes).isEqualTo(90)
    }
}