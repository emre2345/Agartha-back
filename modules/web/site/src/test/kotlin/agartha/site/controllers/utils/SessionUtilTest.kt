package agartha.site.controllers.utils

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Purpose of this file is test Session Utils
 *
 * Created by Jorgen Andersson on 2018-04-26.
 */
class SessionUtilTest {

    /**
     *
     */
    @Test
    fun filterSingleSession_emptyInputList_emptyList() {
        val response = SessionUtil.filterSingleOngoingSession(
                listOf(),
                "abc")
        assertThat(response).isEmpty()
    }

    /**
     *
     */
    @Test
    fun filterSingleSessionPerPractitioner_nonFinishedSessionButNotAbandoned_oneSizeList() {
        val response = SessionUtil.filterSingleOngoingSession(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(175))
                        ))
                ),
                "abc")
        assertThat(response.size).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun filterSingleSessionPerPractitioner_startedBeforeEndedWithin_zeroSizeList() {
        val response = SessionUtil.filterSingleOngoingSession(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(120),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(50))
                        ))
                ),
                "abc")
        assertThat(response.size).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun filterSingleSessionPerPractitioner_startedAndEndedWithin_zeroSizeList() {
        val response = SessionUtil.filterSingleOngoingSession(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(20))
                        ))
                ),
                "abc")
        assertThat(response.size).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun filterSingleSessionPerPractitioner_multipleSessions_oneSizeList() {
        val response = SessionUtil.filterSingleOngoingSession(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(100),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(50)),
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30))
                        ))
                ),
                "abc")
        assertThat(response.size).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun filterSingleSessionPerPractitioner_multipleSessionsLastSelected_Love() {
        val response = SessionUtil.filterSingleOngoingSession(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(100),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(50)),
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Love",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30))
                        ))
                ),
                "abc")
        assertThat(response.get(0).intention).isEqualTo("Love")
    }

    /**
     *
     */
    @Test
    fun filterSingleSessionPerPractitioner_currentUserRemoved_oneSizeList() {
        val response = SessionUtil.filterSingleOngoingSession(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30))
                        )),
                        PractitionerDBO(_id = "abc", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30))
                        ))
                ),
                "abc")
        assertThat(response.size).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun filterAllSessionsPerPractitioner_emptyInputList_emptyList() {
        val response = SessionUtil.filterAllSessionsActiveBetween(
                listOf(),
                DateTimeFormat.localDateTimeUTC().minusMinutes(60),
                DateTimeFormat.localDateTimeUTC())
        assertThat(response).isEmpty()
    }

    /**
     *
     */
    @Test
    fun filterAllSessionsPerPractitioner_practitionerWithNoSessions_emptyList() {
        val response = SessionUtil.filterAllSessionsActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa"),
                        PractitionerDBO(_id = "bbb")
                ),
                DateTimeFormat.localDateTimeUTC().minusMinutes(60),
                DateTimeFormat.localDateTimeUTC())
        assertThat(response).isEmpty()
    }

    /**
     *
     */
    @Test
    fun filterAllSessionsPerPractitioner_practitionerWithAbandonedSession_emptyList() {
        val response = SessionUtil.filterAllSessionsActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(200))
                        ))
                ),
                DateTimeFormat.localDateTimeUTC().minusMinutes(60),
                DateTimeFormat.localDateTimeUTC())
        assertThat(response).isEmpty()
    }

    /**
     *
     */
    @Test
    fun filterAllSessionsPerPractitioner_sessionsBeforeAbandonAndMatching_ListOf3() {
        val response = SessionUtil.filterAllSessionsActiveBetween(
                listOf(
                        // No sessions should be included, both sessions ends before time
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(500),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(400)),
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(400),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(300))

                        )),
                        // No session should be included, abandon
                        PractitionerDBO(_id = "bbb", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(400))
                        )),
                        // One session should be included, started but ongoing
                        PractitionerDBO(_id = "ccc", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(500),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(400)),
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(100))

                        )),
                        // Both sessions should be included
                        PractitionerDBO(_id = "ddd", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(200),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(100)),
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(60),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10))

                        ))
                ),
                DateTimeFormat.localDateTimeUTC().minusMinutes(150),
                DateTimeFormat.localDateTimeUTC())
        //
        assertThat(response.size).isEqualTo(3)
    }

    /**
     *
     */
    @Test
    fun filterOngoingSessions_emptyInputList_0() {
        val response = SessionUtil.filterOngoingSessions(
                listOf(),
                "abc")
        assertThat(response).isEmpty()
    }

    /**
     *
     */
    @Test
    fun filterOngoing_ongoing_1() {
        val response = SessionUtil.filterOngoingSessions(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(175))
                        ))
                ),
                "abc")
        assertThat(response.size).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun filterOngoing_abandon_0() {
        val response = SessionUtil.filterOngoingSessions(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(185))
                        ))
                ),
                "abc")
        assertThat(response.size).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun filterOngoing_endedSession_0() {
        val response = SessionUtil.filterOngoingSessions(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(120),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(50))
                        ))
                ),
                "abc")
        assertThat(response.size).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun filterOngoing_endedSessionLongAgo_0() {
        val response = SessionUtil.filterOngoingSessions(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusDays(30),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusDays(29))
                        ))
                ),
                "abc")
        assertThat(response.size).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun filterOngoing_multipleSessions_1() {
        val response = SessionUtil.filterOngoingSessions(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(100),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(50)),
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30))
                        ))
                ),
                "abc")
        assertThat(response.size).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun filterOngoing_currentUserRemoved_1() {
        val response = SessionUtil.filterOngoingSessions(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30))
                        )),
                        PractitionerDBO(_id = "abc", sessions = listOf(
                                SessionDBO(
                                        geolocation = null,
                                        discipline = "Yoga",
                                        intention = "Wellbeing",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30))
                        ))
                ),
                "abc")
        assertThat(response.size).isEqualTo(1)
    }

    private fun generateCircle(id: String) : CircleDBO {
        return CircleDBO(
                _id = id,
                name = "",
                description = "",
                startTime = DateTimeFormat.localDateTimeUTC(),
                endTime = DateTimeFormat.localDateTimeUTC(),
                disciplines = listOf(),
                intentions = listOf(),
                minimumSpiritContribution = 4)
    }

    @Test
    fun getSessionsInCircle_noPractitioners_0() {
        val response = SessionUtil.getAllSessionsInCircle(
                listOf(),
                "c1")
        assertThat(response.size).isEqualTo(0)
    }

    @Test
    fun getSessionsInCircle_circleIsNull_0() {
        val response = SessionUtil.getAllSessionsInCircle(
                listOf(
                        PractitionerDBO(
                                _id = "a",
                                sessions = listOf(SessionDBO(discipline = "", intention = "")))
                ),
                "c1")
        assertThat(response.size).isEqualTo(0)
    }

    @Test
    fun getSessionsInCircle_noMatch_0() {
        val response = SessionUtil.getAllSessionsInCircle(
                listOf(
                        PractitionerDBO(
                                _id = "a",
                                sessions = listOf(SessionDBO(discipline = "", intention = "", circle = generateCircle("c2"))))
                ),
                "c1")
        assertThat(response.size).isEqualTo(0)
    }

    @Test
    fun getSessionsInCircle_match_1() {
        val response = SessionUtil.getAllSessionsInCircle(
                listOf(
                        PractitionerDBO(
                                _id = "a",
                                sessions = listOf(SessionDBO(discipline = "", intention = "", circle = generateCircle("c1"))))
                ),
                "c1")
        assertThat(response.size).isEqualTo(1)
    }
}