package agartha.site.controllers.utils

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime

/**
 * Purpose of this file is test Session Utils
 *
 * Created by Jorgen Andersson on 2018-04-26.
 */
class SessionUtilTest {

    @Test
    fun filterSingleSession_emptyInputList_emptyList() {
        val response = SessionUtil.filterSingleSessionActiveBetween(
                listOf(),
                "abc",
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response).isEmpty()
    }

    @Test
    fun filterSingleSessionPerPractitioner_nonFinishedSessionButNotAbandoned_oneSizeList() {
        val response = SessionUtil.filterSingleSessionActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Hatha",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(175))
                        ))
                ),
                "abc",
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response.size).isEqualTo(1)
    }

    @Test
    fun filterSingleSessionPerPractitioner_startedBeforeEndedWithin_oneSizeList() {
        val response = SessionUtil.filterSingleSessionActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Hatha",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(120),
                                        endTime = LocalDateTime.now().minusMinutes(50))
                        ))
                ),
                "abc",
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response.size).isEqualTo(1)
    }

    @Test
    fun filterSingleSessionPerPractitioner_startedAndEndedWithin_oneSizeList() {
        val response = SessionUtil.filterSingleSessionActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Hatha",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(30),
                                        endTime = LocalDateTime.now().minusMinutes(20))
                        ))
                ),
                "abc",
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response.size).isEqualTo(1)
    }

    @Test
    fun filterSingleSessionPerPractitioner_multipleSessions_oneSizeList() {
        val response = SessionUtil.filterSingleSessionActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Hatha",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(100),
                                        endTime = LocalDateTime.now().minusMinutes(50)),
                                SessionDBO(
                                        index = 1,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Hatha",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(30),
                                        endTime = LocalDateTime.now().minusMinutes(20))
                        ))
                ),
                "abc",
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response.size).isEqualTo(1)
    }

    @Test
    fun filterSingleSessionPerPractitioner_multipleSessionsLastSelected_index1() {
        val response = SessionUtil.filterSingleSessionActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Hatha",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(100),
                                        endTime = LocalDateTime.now().minusMinutes(50)),
                                SessionDBO(
                                        index = 1,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Hatha",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(30),
                                        endTime = LocalDateTime.now().minusMinutes(20))
                        ))
                ),
                "abc",
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response.get(0).index).isEqualTo(1)
    }

    @Test
    fun filterSingleSessionPerPractitioner_currentUserRemoved_oneSizeList() {
        val response = SessionUtil.filterSingleSessionActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Hatha",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(30),
                                        endTime = LocalDateTime.now().minusMinutes(20))
                        )),
                        PractitionerDBO(_id = "abc", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Hatha",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(30),
                                        endTime = LocalDateTime.now().minusMinutes(20))
                        ))
                ),
                "abc",
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response.size).isEqualTo(1)
    }

    @Test
    fun filterAllSessionsPerPractitioner_emptyInputList_emptyList() {
        val response = SessionUtil.filterAllSessionsActiveBetween(
                listOf(),
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response).isEmpty()
    }

    @Test
    fun filterAllSessionsPerPractitioner_practitionerWithNoSessions_emptyList() {
        val response = SessionUtil.filterAllSessionsActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa"),
                        PractitionerDBO(_id = "bbb")
                ),
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response).isEmpty()
    }

    @Test
    fun filterAllSessionsPerPractitioner_practitionerWithAbandonedSession_emptyList() {
        val response = SessionUtil.filterAllSessionsActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Tantra",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(200))
                        ))
                ),
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response).isEmpty()
    }

    @Test
    fun filterAllSessionsPerPractitioner_sessionsBeforeAbandonAndMatching_ListOf3() {
        val response = SessionUtil.filterAllSessionsActiveBetween(
                listOf(
                        // No sessions should be included, both sessions ends before time
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Tantra",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(500),
                                        endTime = LocalDateTime.now().minusMinutes(400)),
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Tantra",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(400),
                                        endTime = LocalDateTime.now().minusMinutes(300))

                        )),
                        // No session should be included, abandon
                        PractitionerDBO(_id = "bbb", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Tantra",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(400))
                        )),
                        // One session should be included, started but ongoing
                        PractitionerDBO(_id = "ccc", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Tantra",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(500),
                                        endTime = LocalDateTime.now().minusMinutes(400)),
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Tantra",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(100))

                        )),
                        // Both sessions should be included
                        PractitionerDBO(_id = "ddd", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Tantra",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(200),
                                        endTime = LocalDateTime.now().minusMinutes(100)),
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Tantra",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(60),
                                        endTime = LocalDateTime.now().minusMinutes(10))

                        ))
                ),
                LocalDateTime.now().minusMinutes(150),
                LocalDateTime.now())
        //
        assertThat(response.size).isEqualTo(3)
    }

    @Test
    fun distanceToClosestSession_fromBjornstorp_23komma6() {
        val sessions = listOf(
                SessionDBO(
                        index = 0,
                        geolocation = null,
                        discipline = "Nr 1", intention = "Nr 1"),
                SessionDBO(
                        index = 1,
                        geolocation = DevGeolocationSelect.MALMO_KOLLEKTIVA.geolocationDBO,
                        discipline = "Nr 1", intention = "Nr 1"),
                SessionDBO(
                        index = 2,
                        geolocation = DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO,
                        discipline = "Nr 2", intention = "Nr 2"),
                SessionDBO(
                        index = 3,
                        geolocation = DevGeolocationSelect.SYDNEY_OPERA_HOUSE.geolocationDBO,
                        discipline = "Nr 2", intention = "Nr 2")
        )

        val value = SessionUtil.distanceToClosestSession(
                DevGeolocationSelect.BJORNSTORP.geolocationDBO, sessions)
        assertThat(value).isGreaterThan(23.616430)
        assertThat(value).isLessThan(23.616431)
    }


}