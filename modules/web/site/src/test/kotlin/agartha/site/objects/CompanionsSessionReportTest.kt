package agartha.site.objects

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.site.objects.response.CompanionsSessionReport
import org.assertj.core.api.Assertions
import org.junit.Test

class CompanionsSessionReportTest {
    val userNoMatch: PractitionerDBO = PractitionerDBO(sessions = listOf(SessionDBO(index = 0, intention = "Empowerment", discipline = "Meditation")))
    val userFullMatch: PractitionerDBO = PractitionerDBO(sessions = listOf(SessionDBO(index = 0, intention = "Empowerment", discipline = "Yoga")))
    val report: CompanionsSessionReport = CompanionsSessionReport(SessionDBO(index = 0, intention = "Wellbeing", discipline = "Yoga"))
    /**
     *
     */
    @Test
    fun companionsSessionReport_discipline_yoga() {
        Assertions.assertThat(report.discipline).isEqualTo("Yoga")
    }

    /**
     *
     */
    @Test
    fun companionsSessionReport_intention_wellbeing() {
        Assertions.assertThat(report.intention).isEqualTo("Wellbeing")
    }

    /**
     *
     */
    @Test
    fun companionsSessionReport_matchPoint_0() {
        report.giveMatchPoints(userNoMatch)
        Assertions.assertThat(report.matchPoints).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun companionsSessionReport_matchPoint_2() {
        report.giveMatchPoints(userFullMatch)
        Assertions.assertThat(report.matchPoints).isEqualTo(0)
    }
}