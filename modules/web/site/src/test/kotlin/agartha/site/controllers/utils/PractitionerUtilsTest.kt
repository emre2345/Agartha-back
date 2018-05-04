package agartha.site.controllers.utils

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime

/**
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-05-03.
 */
class PractitionerUtilsTest {
    /**
     *
     */
    @Test
    fun filterSingleSessionPerPractitioner_practitionerWithNoSessions_emptyList() {
        val response = PractitionerUtil.filterPractitionerWithSessionsBetween(
                listOf(
                        PractitionerDBO(_id = "aaa"),
                        PractitionerDBO(_id = "bbb")
                ),
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response).isEmpty()
    }
    /**
     *
     */
    @Test
    fun filterSingleSessionPerPractitioner_practitionerWithAbandonedSession_emptyList() {
        val response = PractitionerUtil.filterPractitionerWithSessionsBetween(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Hatha",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(200))
                        ))
                ),
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response).isEmpty()
    }
    /**
     *
     */
    @Test
    fun filterSingleSessionPerPractitioner_practitionerWithSessionEndedBefore_emptyList() {
        val response = PractitionerUtil.filterPractitionerWithSessionsBetween(
                listOf(
                        PractitionerDBO(_id = "aaa", sessions = listOf(
                                SessionDBO(
                                        index = 0,
                                        geolocation = null,
                                        discipline = "Yoga",
                                        practice = "Hatha",
                                        intention = "Wellbeing",
                                        startTime = LocalDateTime.now().minusMinutes(120),
                                        endTime = LocalDateTime.now().minusMinutes(70))
                        ))
                ),
                LocalDateTime.now().minusMinutes(60),
                LocalDateTime.now())
        assertThat(response).isEmpty()
    }
}