package agartha.site.objects

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.SessionDBO
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
    fun companionSessionCount_emptyList_0() {
        val companion = CompanionReport(emptyList())
        assertThat(companion.count).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun compaionSessionCount_existingList_5() {
        val companion = CompanionReport(generateSessions())
        assertThat(companion.count).isEqualTo(5)
    }

    /**
     *
     */
    @Test
    fun compaionSessionMinutes_emptyList_0() {
        val companion = CompanionReport(emptyList())
        assertThat(companion.minutes).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun compaionSessionMinutes_existingList_150() {
        val companion = CompanionReport(generateSessions())
        assertThat(companion.minutes).isEqualTo(150)
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
}