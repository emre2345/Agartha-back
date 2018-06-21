package agartha.data.objects

import agartha.common.utils.DateTimeFormat
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Purpose of this class is to test the SessionDBO
 */
class SessionDBOTest {


    /*************************************************
     * Variables - discipline + practice + intention *
     *************************************************/
    @Test
    fun discipline_disciplineName_yoga() {
        val sessionWithoutTime = SessionDBO( null, "Yoga", "Love")
        assertThat(sessionWithoutTime.discipline).isEqualTo("Yoga")
    }


    /**
     *
     */
    @Test
    fun discipline_intentionName_love() {
        val sessionWithoutTime = SessionDBO( null, "Yoga", "Love")
        assertThat(sessionWithoutTime.intention).isEqualTo("Love")
    }

    /*******************
     * sessionDuration *
     *******************/
    @Test
    fun sessionDuration_withNullEndTime_3() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.localDateTimeUTC().minusMinutes(3),
                null)
        assertThat(session.sessionDurationMinutes()).isEqualTo(3)
    }

    /**
     *
     */
    @Test
    fun sessionDuration_withEndTime_10() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:17:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:27:00"))
        assertThat(session.sessionDurationMinutes()).isEqualTo(10)
    }

    /**
     *
     */
    @Test
    fun sessionDuration_withUnevenMinutes_5() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:15:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:20:10"))
        assertThat(session.sessionDurationMinutes()).isEqualTo(5)
    }

    /**
     *
     */
    @Test
    fun sessionDuration_floorMinutes_5() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:15:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:20:50"))
        assertThat(session.sessionDurationMinutes()).isEqualTo(5)
    }

    /**
     *
     */
    @Test
    fun sessionDuration_abandon_0() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.localDateTimeUTC().minusMinutes(185))
        assertThat(session.sessionDurationMinutes()).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun sessionDuration_activeNotAbandon_30() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.localDateTimeUTC().minusMinutes(30))
        assertThat(session.sessionDurationMinutes()).isEqualTo(30)
    }

    /******************
     * sessionOverLap *
     ******************/
    @Test
    fun sessionOverLap_startAndEndBefore_false() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 17:50:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 17:55:00"))
        assertThat(session.sessionOverlap(
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:00:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 19:00:00")
        )).isFalse()
    }

    /**
     *
     */
    @Test
    fun sessionOverLap_startAndEndAfter_false() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 19:05:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 19:10:00"))
        assertThat(session.sessionOverlap(
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:00:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 19:00:00")
        )).isFalse()
    }

    /**
     *
     */
    @Test
    fun sessionOverLap_startAndEndWithin_true() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:20:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:40:00"))
        assertThat(session.sessionOverlap(
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:00:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 19:00:00")
        )).isTrue()
    }

    /**
     *
     */
    @Test
    fun sessionOverLap_startBeforeEndWithin_true() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 17:30:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:30:00"))
        assertThat(session.sessionOverlap(
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:00:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 19:00:00")
        )).isTrue()
    }

    /**
     *
     */
    @Test
    fun sessionOverLap_startWithinEndAfter_true() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:30:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 19:30:00"))
        assertThat(session.sessionOverlap(
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 18:00:00"),
                DateTimeFormat.stringToLocalDateTimeUTC("2018-04-20 19:00:00")
        )).isTrue()
    }

    /**
     *
     */
    @Test
    fun sessionOverLap_sessionIsAbandon_false() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.localDateTimeUTC().minusMinutes(200),
                null)
        assertThat(session.sessionOverlap(
                DateTimeFormat.localDateTimeUTC().minusMinutes(60),
                DateTimeFormat.localDateTimeUTC()
        )).isFalse()
    }

    /**
     *
     */
    @Test
    fun sessionOverLap_sessionOngoing_true() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.localDateTimeUTC().minusMinutes(150),
                null)
        assertThat(session.sessionOverlap(
                DateTimeFormat.localDateTimeUTC().minusMinutes(60),
                DateTimeFormat.localDateTimeUTC()
        )).isTrue()
    }

    /**
     *
     */
    @Test
    fun sessionAfter_startedBefore_false() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.localDateTimeUTC().minusMinutes(150),
                null)
        assertThat(session.sessionAfter(
                DateTimeFormat.localDateTimeUTC().minusMinutes(60)
        )).isFalse()
    }

    /**
     *
     */
    @Test
    fun sessionAfter_startedAfter_true() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.localDateTimeUTC().minusMinutes(50),
                null)
        assertThat(session.sessionAfter(
                DateTimeFormat.localDateTimeUTC().minusMinutes(60)
        )).isTrue()
    }

    /**
     *
     */
    @Test
    fun ongoing_abandon_false() {
        val session = SessionDBO (null, "Yoga", "Love",
                DateTimeFormat.localDateTimeUTC().minusMinutes(185),
                null)
        assertThat(session.ongoing()).isFalse()
    }

    /**
     *
     */
    @Test
    fun ongoing_ended_false() {
        val session = SessionDBO(null, "Yoga", "Love",
                DateTimeFormat.localDateTimeUTC().minusMinutes(30),
                DateTimeFormat.localDateTimeUTC().minusMinutes(20))
        assertThat(session.ongoing()).isFalse()
    }

    /**
     * 
     */
    @Test
    fun ongoing_ongoing_true() {
        val session = SessionDBO( null, "Yoga", "Love",
                DateTimeFormat.localDateTimeUTC().minusMinutes(175),
                null)
        assertThat(session.ongoing()).isTrue()
    }
}