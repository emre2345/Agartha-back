package agartha.site.objects

import agartha.data.objects.SessionDBO
import agartha.site.objects.response.CompanionsSessionReport
import org.assertj.core.api.Assertions
import org.junit.Test

class CompanionsSessionReportTest {
    val session: SessionDBO = SessionDBO(index = 0, intention = "Wellbeing", discipline = "Yoga")
    val report: CompanionsSessionReport = CompanionsSessionReport(session)
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
}