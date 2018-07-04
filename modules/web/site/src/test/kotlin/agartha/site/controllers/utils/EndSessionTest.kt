package agartha.site.controllers.utils

import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime

/**
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-06-28.
 */
class EndSessionTest {

    private val sessionStartTime = LocalDateTime.now().minusMinutes(10)

    private val currentCirclePractitioner = PractitionerDBO(
            _id = "a",
            sessions = listOf(
                    SessionDBO(discipline = "", intention = "", startTime = sessionStartTime, circle = createCircle("c1"))),
            circles = listOf(createCircle("c1")))

    private val circlePractitioners = listOf(
            PractitionerDBO(_id = "b", sessions = listOf(
                    SessionDBO(discipline = "", intention = "", startTime = sessionStartTime.plusSeconds(2), circle = createCircle("c1")))),
            PractitionerDBO(_id = "c", sessions = listOf(
                    SessionDBO(discipline = "", intention = "", startTime = sessionStartTime.plusSeconds(3), circle = createCircle("c1")))),
            PractitionerDBO(_id = "d", sessions = listOf(
                    SessionDBO(discipline = "", intention = "", startTime = sessionStartTime.plusSeconds(4), circle = createCircle("c1")))))

    private fun createCircle(id: String): CircleDBO {
        return CircleDBO(
                _id = id,
                name = "c",
                description = "",
                startTime = sessionStartTime,
                endTime = sessionStartTime.plusMinutes(60),
                intentions = listOf(),
                disciplines = listOf(),
                minimumSpiritContribution = 12,
                language = "Esperanto")
    }

    @Test
    fun nocircle_circlePoints_0() {
        val session = EndSession(
                PractitionerDBO(
                        _id = "a", sessions = listOf(
                        SessionDBO(discipline = "", intention = "", startTime = LocalDateTime.now().minusMinutes(10)))),
                listOf(),
                12,
                100)
        assertThat(session.circlePoints).isEqualTo(0)
    }

    @Test
    fun nocircle_creator_false() {
        val session = EndSession(
                PractitionerDBO(
                        _id = "a", sessions = listOf(
                        SessionDBO(discipline = "", intention = "", startTime = LocalDateTime.now().minusMinutes(10)))),
                listOf(),
                12,
                100)
        assertThat(session.creator).isFalse()
    }

    @Test
    fun circleNotCreator_circlePoints_0() {
        val start = LocalDateTime.now().minusMinutes(10)
        val session = EndSession(
                PractitionerDBO(
                        _id = "a",
                        sessions = listOf(
                                SessionDBO(
                                        discipline = "",
                                        intention = "",
                                        startTime = start,
                                        circle = createCircle("c1")))),
                listOf(),
                12,
                100)
        assertThat(session.circlePoints).isEqualTo(0)
    }

    @Test
    fun circleNotCreator_creator_false() {
        val session = EndSession(
                currentCirclePractitioner,
                circlePractitioners,
                12,
                100)
        assertThat(session.creator).isTrue()
    }

    @Test
    fun circleCreator_circlePoints_36() {
        val session = EndSession(
                currentCirclePractitioner,
                circlePractitioners,
                12,
                100)
        // 3 practitioner * 12 points * 100 percent
        assertThat(session.circlePoints).isEqualTo(36)
    }

    @Test
    fun circleCreator_circlePoints_32() {
        val session = EndSession(
                currentCirclePractitioner,
                circlePractitioners,
                12,
                90)
        // 3 practitioner * 12 points * 90 percent
        assertThat(session.circlePoints).isEqualTo(32)
    }
}