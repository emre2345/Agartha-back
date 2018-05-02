package agartha.data.objects

import agartha.common.utils.DateTimeFormat
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime

class SessionDBOTest {

    val sessionWithoutTime = SessionDBO(0, null, "Yoga", "Mindfulness", "Love")

    /*************************************************
     * Variables - discipline + practice + intention *
     *************************************************/
    @Test
    fun discipline_disciplineName_yoga() {
        assertThat(sessionWithoutTime.discipline).isEqualTo("Yoga")
    }

    /**
     *
     */
    @Test
    fun discipline_practiceName_mindfulness() {
        assertThat(sessionWithoutTime.practice).isEqualTo("Mindfulness")
    }

    /**
     *
     */
    @Test
    fun discipline_intentionName_love() {
        assertThat(sessionWithoutTime.intention).isEqualTo("Love")
    }

    /*******************
     * sessionDuration *
     *******************/
    @Test
    fun sessionDuration_withNullEndTime_3() {
        val session = SessionDBO(0, null, "Yoga", "Mindfulness", "Love",
                LocalDateTime.now().minusMinutes(3),
                null)
        assertThat(session.sessionDurationMinutes()).isEqualTo(3)
    }

    /**
     *
     */
    @Test
    fun sessionDuration_withEndTime_10() {
        val session = SessionDBO(0, null,"Yoga", "Mindfulness", "Love",
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:17:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:27:00"))
        assertThat(session.sessionDurationMinutes()).isEqualTo(10)
    }

    /**
     *
     */
    @Test
    fun sessionDuration_withUnevenMinutes_5() {
        val session = SessionDBO(0, null,"Yoga", "Mindfulness", "Love",
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:15:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:20:10"))
        assertThat(session.sessionDurationMinutes()).isEqualTo(5)
    }

    /**
     *
     */
    @Test
    fun sessionDuration_floorMinutes_5() {
        val session = SessionDBO(0, null,"Yoga", "Mindfulness", "Love",
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:15:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:20:50"))
        assertThat(session.sessionDurationMinutes()).isEqualTo(5)
    }

    @Test
    fun sessionDuration_abandon_0() {
        val session = SessionDBO(0, null,"Yoga","Mindfulness", "Love",
                LocalDateTime.now().minusMinutes(185))
        assertThat(session.sessionDurationMinutes()).isEqualTo(0)
    }

    @Test
    fun sessionDuration_activeNotAbandon_30() {
        val session = SessionDBO(0,null, "Yoga", "Mindfulness", "Love",
                LocalDateTime.now().minusMinutes(30))
        assertThat(session.sessionDurationMinutes()).isEqualTo(30)
    }

    /******************
     * sessionOverLap *
     ******************/
    @Test
    fun sessionOverLap_startAndEndBefore_false() {
        val session = SessionDBO(0, null,"Yoga", "Mindfulness", "Love",
                DateTimeFormat.stringToLocalDateTime("2018-04-20 17:50:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 17:55:00"))
        assertThat(session.sessionOverlap(
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 19:00:00")
        )).isFalse()
    }

    /**
     *
     */
    @Test
    fun sessionOverLap_startAndEndAfter_false() {
        val session = SessionDBO(0, null,"Yoga", "Mindfulness", "Love",
                DateTimeFormat.stringToLocalDateTime("2018-04-20 19:05:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 19:10:00"))
        assertThat(session.sessionOverlap(
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 19:00:00")
        )).isFalse()
    }

    /**
     *
     */
    @Test
    fun sessionOverLap_startAndEndWithin_true() {
        val session = SessionDBO(0, null,"Yoga", "Mindfulness", "Love",
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:20:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:40:00"))
        assertThat(session.sessionOverlap(
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 19:00:00")
        )).isTrue()
    }

    /**
     *
     */
    @Test
    fun sessionOverLap_startBeforeEndWithin_true() {
        val session = SessionDBO(0, null,"Yoga", "Mindfulness", "Love",
                DateTimeFormat.stringToLocalDateTime("2018-04-20 17:30:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:30:00"))
        assertThat(session.sessionOverlap(
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 19:00:00")
        )).isTrue()
    }

    /**
     *
     */
    @Test
    fun sessionOverLap_startWithinEndAfter_true() {
        val session = SessionDBO(0, null,"Yoga", "Mindfulness", "Love",
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:30:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 19:30:00"))
        assertThat(session.sessionOverlap(
                DateTimeFormat.stringToLocalDateTime("2018-04-20 18:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-20 19:00:00")
        )).isTrue()
    }

    /**
     *
     */
    @Test
    fun sessionOverLap_sessionIsAbandon_false() {
        val session = SessionDBO(0, null,"Yoga", "Mindfulness", "Love",
                LocalDateTime.now().minusMinutes(200),
                null)
        assertThat(session.sessionOverlap(
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now()
        )).isFalse()
    }

    /**
     *
     */
    @Test
    fun sessionOverLap_sessionOngoing_true() {
        val session = SessionDBO(0, null,"Yoga", "Mindfulness", "Love",
                LocalDateTime.now().minusMinutes(150),
                null)
        assertThat(session.sessionOverlap(
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now()
        )).isTrue()
    }
}