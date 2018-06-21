package agartha.data.objects

import agartha.common.utils.DateTimeFormat
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime

/**
 * Purpose of this file is to test CircleDBO
 *
 * Created by Jorgen Andersson on 2018-06-07.
 */
class CircleDBOTest {

    val circle = CircleDBO(
            name = "Circle name",
            description = "Circle description",
            startTime = DateTimeFormat.localDateTimeUTC().plusHours(4),
            endTime = DateTimeFormat.localDateTimeUTC().plusHours(6),
            intentions = listOf(
                    IntentionDBO("Intention 1", "Intention 1")),
            disciplines = listOf(
                    DisciplineDBO("Discipline 1", "Discipline 1"),
                    DisciplineDBO("Discipline 2", "Discipline 2")),
            minimumSpiritContribution = 12L)

    @Test
    fun circle_name_CircleName() {
        assertThat(circle.name).isEqualTo("Circle name")
    }

    @Test
    fun circle_description_CircleDescription() {
        assertThat(circle.description).isEqualTo("Circle description")
    }

    @Test
    fun circle_startTime_IsBefore4Hours1minute() {
        assertThat(circle.startTime).isBefore(DateTimeFormat.localDateTimeUTC().plusMinutes((4 * 60) + 1))
    }

    @Test
    fun circle_startTime_IsAfter3Hours59minute() {
        assertThat(circle.startTime).isAfter(DateTimeFormat.localDateTimeUTC().plusMinutes((3 * 60) + 59))
    }

    @Test
    fun circle_endTime_IsBefore6Hours1minute() {
        assertThat(circle.endTime).isBefore(DateTimeFormat.localDateTimeUTC().plusMinutes((6 * 60) + 1))
    }

    @Test
    fun circle_endTime_IsAfter5Hours59minute() {
        assertThat(circle.endTime).isAfter(DateTimeFormat.localDateTimeUTC().plusMinutes((5 * 60) + 59))
    }

    @Test
    fun circle_intentionsSize_1() {
        assertThat(circle.intentions.size).isEqualTo(1)
    }

    @Test
    fun circle_disciplinesSize_2() {
        assertThat(circle.disciplines.size).isEqualTo(2)
    }

    @Test
    fun circle_minimumSpiritContribution_12() {
        assertThat(circle.minimumSpiritContribution).isEqualTo(12)
    }

    @Test
    fun circle_active_false() {
        val c = CircleDBO(
                name = "Circle name",
                description = "Circle description",
                startTime = DateTimeFormat.localDateTimeUTC().plusHours(4),
                endTime = DateTimeFormat.localDateTimeUTC().plusHours(6),
                intentions = listOf(),
                disciplines = listOf(),
                minimumSpiritContribution = 12L)
        assertThat(c.active()).isFalse()
    }

    @Test
    fun circle_active_true() {
        val c = CircleDBO(
                name = "Circle name",
                description = "Circle description",
                startTime = DateTimeFormat.localDateTimeUTC().minusHours(1),
                endTime = DateTimeFormat.localDateTimeUTC().plusHours(1),
                intentions = listOf(),
                disciplines = listOf(),
                minimumSpiritContribution = 12L)
        assertThat(c.active()).isTrue()
    }
}