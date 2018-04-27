package agartha.site.objects

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.SessionDBO
import agartha.site.objects.response.CompanionReport
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Purpose of this file is Test for CompanionReport response object
 *
 * Created by Jorgen Andersson on 2018-04-24.
 */
class CompanionReportTest {

    private fun generateSessions(): List<SessionDBO> {
        return listOf(
                SessionDBO(0, "Yoga", false,
                        DateTimeFormat.stringToLocalDateTime("2018-04-18 12:00:00"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-18 12:40:00")),
                SessionDBO(1, "Mindfulness", false,
                        DateTimeFormat.stringToLocalDateTime("2018-04-19 12:00:00"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-19 12:30:00")),
                SessionDBO(2, "Meditation", false,
                        DateTimeFormat.stringToLocalDateTime("2018-04-20 12:00:00"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-20 12:20:00")),
                SessionDBO(1, "Mindfulness", false,
                        DateTimeFormat.stringToLocalDateTime("2018-04-19 12:00:00"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-19 12:30:00")),
                SessionDBO(1, "Mindfulness", false,
                        DateTimeFormat.stringToLocalDateTime("2018-04-19 12:00:00"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-19 12:30:00"))
                )
    }

    /**
     *
     */
    @Test
    fun companionPractitionerCount_emptyList_0() {
        val companion = CompanionReport(emptyList())
        assertThat(companion.practitionerCount).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun compaionPractitionerCount_existingList_5() {
        val companion = CompanionReport(generateSessions())
        assertThat(companion.practitionerCount).isEqualTo(5)
    }

    /**
     *
     */
    @Test
    fun companionSessionCount_emptyList_0() {
        val companion = CompanionReport(emptyList())
        assertThat(companion.sessionCount).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun compaionSessionCount_existingList_5() {
        val companion = CompanionReport(generateSessions())
        assertThat(companion.sessionCount).isEqualTo(5)
    }

    /**
     *
     */
    @Test
    fun compaionSessionMinutes_emptyList_0() {
        val companion = CompanionReport(emptyList())
        assertThat(companion.sessionMintes).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun compaionSessionMinutes_existingList_150() {
        val companion = CompanionReport(generateSessions())
        assertThat(companion.sessionMintes).isEqualTo(150)
    }

    /**
     *
     */
    @Test
    fun companionPracticeMap_emptyList_0() {
        val companion = CompanionReport(emptyList())
        assertThat(companion.practices).isEmpty()
    }

    /**
     *
     */
    @Test
    fun companionPracticeMap_existingList_3items() {
        val companion = CompanionReport(generateSessions())
        assertThat(companion.practices.size).isEqualTo(3)
    }

    @Test
    fun companionPracticeMap_existingList_mindfulHas3() {
        val companion = CompanionReport(generateSessions())
        assertThat(companion.practices.get("Mindfulness")).isEqualTo(3)
    }

    /**
     *
     */
    @Test
    fun companionPracticeMap_existingList_meditationHas1() {
        val companion = CompanionReport(generateSessions())
        assertThat(companion.practices.get("Meditation")).isEqualTo(1)
    }

    /**
     * 
     */
    @Test
    fun companionPracticeMap_existingList_yogaHas1() {
        val companion = CompanionReport(generateSessions())
        assertThat(companion.practices.get("Yoga")).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun companionPractitionerCount_noPractitionerAndEmptyList_0() {
        val companion = CompanionReport(0, emptyList())
        assertThat(companion.practitionerCount).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun companionSessionCount_noPractitionerAndEmptyList_0() {
        val companion = CompanionReport(0, emptyList())
        assertThat(companion.sessionCount).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun compaionPractitionerCount_PractitionerAndExistingSessionList_3() {
        val companion = CompanionReport(3, generateSessions())
        assertThat(companion.practitionerCount).isEqualTo(3)
    }

    /**
     *
     */
    @Test
    fun compaionSessionCount_PractitionerAndExistingSessionList_5() {
        val companion = CompanionReport(3, generateSessions())
        assertThat(companion.sessionCount).isEqualTo(5)
    }
}