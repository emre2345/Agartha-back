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
    fun filterSession_emptyInputList_emptyList() {
        val response = SessionUtil.filterSessionsBetween(
                listOf(),
                "abc",
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response).isEmpty()
    }

    @Test
    fun filterSessions_practitionerWithNoSessions_emptyList() {
        val response = SessionUtil.filterSessionsBetween(
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
    fun filterSessions_practitionerWithAbandonedSession_emptyList() {
        val response = SessionUtil.filterSessionsBetween(
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
    fun filterSessions_practitionerWithSessionEndedBefore_emptyList() {
        val response = SessionUtil.filterSessionsBetween(
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
    fun filterSessions_nonFinishedSessionButNotAbandoned_oneSizeList() {
        val response = SessionUtil.filterSessionsBetween(
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
    fun filterSessions_startedBeforeEndedWithin_oneSizeList() {
        val response = SessionUtil.filterSessionsBetween(
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
    fun filterSessions_startedAndEndedWithin_oneSizeList() {
        val response = SessionUtil.filterSessionsBetween(
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
    fun filterSessions_multipleSessions_oneSizeList() {
        val response = SessionUtil.filterSessionsBetween(
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
    fun filterSessions_multipleSessionsLastSelected_index1() {
        val response = SessionUtil.filterSessionsBetween(
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
    fun filterSession_currentUserRemoved_oneSizeList() {
        val response = SessionUtil.filterSessionsBetween(
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
}