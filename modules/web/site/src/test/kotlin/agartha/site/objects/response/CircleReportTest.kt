package agartha.site.objects.response

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.CircleDBO
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Created by Jorgen Andersson on 2018-06-18.
 */
class CircleReportTest {

    private val circle = CircleDBO(
            _id = "a",
            name = "My Circle",
            description = "My Description",
            startTime = DateTimeFormat.localDateTimeUTC(),
            endTime = DateTimeFormat.localDateTimeUTC(),
            intentions = listOf(),
            disciplines = listOf(),
            minimumSpiritContribution = 11)

    @Test
    fun name_match_myCircle() {
        val report = CircleReport(circle, listOf(), 42)
        assertThat(report.name).isEqualTo("My Circle")
    }

    @Test
    fun description_match_MyDescription() {
        val report = CircleReport(circle, listOf(), 42)
        assertThat(report.description).isEqualTo("My Description")
    }

    @Test
    fun sessions_noSessions_0() {
        val report = CircleReport(circle, listOf(), 42)
        assertThat(report.numberOfPractitioners).isEqualTo(0)
    }

    @Test
    fun sessions_sessions_2() {
        val report = CircleReport(circle, listOf(
                SessionDBO(discipline = "", intention = ""),
                SessionDBO(discipline = "", intention = "")
        ), 42)
        assertThat(report.numberOfPractitioners).isEqualTo(2)
    }

    @Test
    fun sessions_points_42() {
        val report = CircleReport(circle, listOf(), 42)
        assertThat(report.generatedPoints).isEqualTo(42)
    }
}