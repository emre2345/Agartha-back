package agartha.site.objects

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Purpose of this file is Test for Companion response object
 *
 * Created by Jorgen Andersson on 2018-04-24.
 */
class CompanionTest {

    fun generateSessions(): List<SessionDBO> {
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

    @Test
    fun companionSessionCount_emptyList_0() {
        val companion = Companion(emptyList())
        assertThat(companion.count).isEqualTo(0)
    }

    @Test
    fun compaionSessionCount_existingList_3() {
        val companion = Companion(generateSessions())
        assertThat(companion.count).isEqualTo(3)
    }

    @Test
    fun compaionSessionMinutes_emptyList_0() {
        val companion = Companion(emptyList())
        assertThat(companion.minutes).isEqualTo(0)
    }

    @Test
    fun compaionSessionMinutes_existingList_3() {
        val companion = Companion(generateSessions())
        assertThat(companion.minutes).isEqualTo(90)
    }
}