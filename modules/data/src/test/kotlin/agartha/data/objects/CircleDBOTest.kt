package agartha.data.objects

import agartha.common.utils.DateTimeFormat
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

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
            minimumSpiritContribution = 12L,
            language = "Swedish",
            virtualRegistered = 3,
            feedback = listOf(2,3,4))

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
    fun circle_language_swedish() {
        assertThat(circle.language).isEqualTo("Swedish")
    }

    @Test
    fun circle_feedbackLastValue_4() {
        assertThat(circle.feedback.last()).isEqualTo(4)
    }

    @Test
    fun circle_virtualRegistered_3() {
        assertThat(circle.virtualRegistered).isEqualTo(3)
    }

    @Test
    fun circle_active_false() {
        assertThat(circle.active()).isFalse()
    }

    @Test
    fun circle_active_true() {
        val c = CircleDBO(
                name = "Circle name",
                description = "Circle description",
                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(1),
                endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(1),
                intentions = listOf(),
                disciplines = listOf(),
                minimumSpiritContribution = 12L,
                language = "Swedish",
                virtualRegistered = 3)
        assertThat(c.active()).isTrue()
    }
}