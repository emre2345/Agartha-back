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
    fun filterSingleSessionPerPractitioner_practitionerWithNoSessions_emptyList() {
        val response = SessionUtil.filterSingleSessionActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa"),
                        PractitionerDBO(_id = "bbb")
                ),
                "abc",
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response).isEmpty()
    }

    @Test
    fun filterSingleSessionPerPractitioner_practitionerWithAbandonedSession_emptyList() {
        val response = SessionUtil.filterSingleSessionActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(200))
                        ))
                ),
                "abc",
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response).isEmpty()
    }

    @Test
    fun filterSingleSessionPerPractitioner_practitionerWithSessionEndedBefore_emptyList() {
        val response = SessionUtil.filterSingleSessionActiveBetween(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(120),
                                        endTime = LocalDateTime.now().minusMinutes(70))
                        ))
                ),
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
                                        practition = "Yoga",
                                        active = true,
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
                                        practition = "Yoga",
                                        active = true,
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
                                        practition = "Yoga",
                                        active = true,
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
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(100),
                                        endTime = LocalDateTime.now().minusMinutes(50)),
                                SessionDBO(
                                        index = 1,
                                        practition = "Yoga",
                                        active = true,
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
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(100),
                                        endTime = LocalDateTime.now().minusMinutes(50)),
                                SessionDBO(
                                        index = 1,
                                        practition = "Yoga",
                                        active = true,
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
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(30),
                                        endTime = LocalDateTime.now().minusMinutes(20))
                        )),
                        PractitionerDBO(_id = "abc", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        practition = "Yoga",
                                        active = true,
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
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(200))
                        ))
                ),
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response).isEmpty()
    }

    @Test
    fun filterAllSessionsPerPractitioner_tada_fail() {
        val response = SessionUtil.filterAllSessionsActiveBetween(
                listOf(
                        // No sessions should be included, both sessions ends before time
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(500),
                                        endTime = LocalDateTime.now().minusMinutes(400)),
                                SessionDBO(
                                        index = 0,
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(400),
                                        endTime = LocalDateTime.now().minusMinutes(300))

                        )),
                        // No session should be included, abandon
                        PractitionerDBO(_id = "bbb", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(400))
                        )),
                        // One session should be included, started but ongoing
                        PractitionerDBO(_id = "ccc", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(500),
                                        endTime = LocalDateTime.now().minusMinutes(400)),
                                SessionDBO(
                                        index = 0,
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(100))

                        )),
                        // Both sessions should be included
                        PractitionerDBO(_id = "ddd", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(200),
                                        endTime = LocalDateTime.now().minusMinutes(100)),
                                SessionDBO(
                                        index = 0,
                                        practition = "Yoga",
                                        active = true,
                                        startTime = LocalDateTime.now().minusMinutes(60),
                                        endTime = LocalDateTime.now().minusMinutes(10))

                        ))
                ),
                LocalDateTime.now().minusMinutes(150),
                LocalDateTime.now())
        //
        assertThat(response.size).isEqualTo(3)
    }
}