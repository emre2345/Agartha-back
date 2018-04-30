package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.site.controllers.mocks.MockedSessionService
import agartha.site.objects.response.CompanionReport
import agartha.site.objects.response.SessionReport
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import java.time.LocalDateTime

/**
 * Purpose of this file is to test the Practitioner controller
 *
 * Created by Jorgen Andersson on 2018-04-25.
 */
class SessionControllerTest {

    companion object {
        val mockedService = MockedSessionService()
        val testController = ControllerServer()


        @BeforeClass
        @JvmStatic
        fun setupClass() {
            SessionController(mockedService)
            spark.Spark.awaitInitialization()
        }
    }

    /**
     * Clear Mock after each test
     */
    @After
    fun afterTest() {
        mockedService.clear()
    }


    private fun setupReport() {
        //
        mockedService.insert(PractitionerDBO("a", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, "Yoga", "Tantra", "Transformation", false,
                        LocalDateTime.now().minusDays(13),
                        LocalDateTime.now().minusDays(13)),
                SessionDBO(1, "Yoga", "Tantra", "Empowerment", false,
                        LocalDateTime.now().minusDays(11),
                        LocalDateTime.now().minusDays(11)),
                SessionDBO(2, "Meditation", "Transendental", "Harmony", false,
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().minusDays(5)),
                SessionDBO(3, "Yoga", "Hatha", "Freedom", false,
                        LocalDateTime.now().minusMinutes(41),
                        LocalDateTime.now().minusMinutes(1)))))
        //
        mockedService.insert(PractitionerDBO("b", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, "Meditation","Mindfulness", "Love", false,
                        LocalDateTime.now().minusMinutes(20),
                        LocalDateTime.now().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("c", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, "Yoga", "Hatha", "Love", false,
                        LocalDateTime.now().minusDays(13),
                        LocalDateTime.now().minusDays(13)),
                SessionDBO(1, "Yoga", "Hatha", "Freedom", false,
                        LocalDateTime.now().minusDays(11),
                        LocalDateTime.now().minusDays(11)),
                SessionDBO(2, "Yoga", "Hatha", "Love", false,
                        LocalDateTime.now().minusDays(3).minusMinutes(45),
                        LocalDateTime.now().minusDays(3)),
                SessionDBO(3, "Meditation", "Mindfulness","Harmony", false,
                        LocalDateTime.now().minusMinutes(20).minusSeconds(10),
                        LocalDateTime.now()))))
        //
        mockedService.insert(PractitionerDBO("d", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, "Meditation","Mindfulness", "Empathy", false,
                        LocalDateTime.now().minusMinutes(35),
                        LocalDateTime.now().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("e", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, "Meditation","Mindfulness", "Empowerment", false,
                        LocalDateTime.now().minusMinutes(35),
                        LocalDateTime.now().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("f", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, "Meditation","Transendental", "Celebration", false,
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().minusDays(5)))))
    }

    /**
     *
     */
    @Test
    fun sessionController_insertSession_sessionIdIs1() {
        val body: String = "{\"discipline\": \"Yoga\",\n\"practice\": \"Hatha\",\n\"intention\": \"Salary raise\"\n}"
        // Setup
        mockedService.insert(PractitionerDBO("abc", LocalDateTime.now(), mutableListOf()))
        //
        val postRequest = testController.testServer.post("/session/abc", body, false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        assertThat(responseBody).isEqualTo("1")
    }

    /**
     *
     */
    @Test
    fun sessionController_insertSessionWithoutPractice_sessionIdIs1() {
        val body: String = "{\"discipline\": \"Yoga\",\n\"intention\": \"Salary raise\"\n}"
        // Setup
        mockedService.insert(PractitionerDBO("abc", LocalDateTime.now(), mutableListOf()))
        //
        val postRequest = testController.testServer.post("/session/abc", body, false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        assertThat(responseBody).isEqualTo("1")
    }

    /**
     *
     */
    @Test
    fun sessionController_sessionReport_userIdIsC() {
        setupReport()
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.practitionerReport.practitionerId).isEqualTo("c")
    }

    /**
     *
     */
    @Test
    fun sessionController_sessionReport_sessionTime20() {
        setupReport()
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.practitionerReport.lastSessionTime).isEqualTo(20)
    }

    /**
     *
     */
    @Test
    fun sessionController_sessionReport_totalSessionTime65() {
        setupReport()
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.practitionerReport.totalSessionTime).isEqualTo(65)
    }

    /**
     *
     */
    @Test
    fun sessionController_sessionReport_practitionerCountIs4() {
        setupReport()
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.companionReport.practitionerCount).isEqualTo(4)
    }

    /**
     *
     */
    @Test
    fun sessionController_sessionReport_sessionCountIs4() {
        setupReport()
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.companionReport.sessionCount).isEqualTo(4)
    }

    /**
     *
     */
    @Test
    fun sessionController_sessionReport_sessionMinutesIs115() {
        setupReport()
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.companionReport.sessionMinutes).isEqualTo(115)
    }

    /**
     *
     */
    @Test
    fun sessionController_sessionReport_intentionsHasLove() {
        setupReport()
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.companionReport.intentions.containsKey("Love")).isTrue()
    }

    /**
     *
     */
    @Test
    fun sessionController_sessionReport_intentionsHasFreedom() {
        setupReport()
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.companionReport.intentions.containsKey("Freedom")).isTrue()
    }

    /**
     * Current users practice should not be included
     */
    @Test
    fun sessionController_sessionReport_intentionsHasNotTransformation() {
        setupReport()
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.companionReport.intentions.containsKey("Transformation")).isFalse()
    }

    /**
     * Older than current session should not be counted
     */
    @Test
    fun sessionController_sessionReport_intentionsHasNotHarmony() {
        setupReport()
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.companionReport.intentions.containsKey("Harmony")).isFalse()
    }

    /**
     *
     */
    @Test
    fun sessionController_companionReport_practitionerCountIs6() {
        setupReport()
        val getRequest = testController.testServer.get("/session/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = jacksonObjectMapper().readValue(body, CompanionReport::class.java)
        assertThat(data.practitionerCount).isEqualTo(6)

    }

    /**
     *
     */
    @Test
    fun sessionController_companionReport_sessionsCountIs8() {
        setupReport()
        val getRequest = testController.testServer.get("/session/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = jacksonObjectMapper().readValue(body, CompanionReport::class.java)
        assertThat(data.sessionCount).isEqualTo(8)
    }

    /**
     *
     */
    @Test
    fun sessionController_companionReport_sessionMinutesIs() {
        setupReport()
        val getRequest = testController.testServer.get("/session/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = jacksonObjectMapper().readValue(body, CompanionReport::class.java)
        assertThat(data.sessionMinutes).isEqualTo(180)
    }

    /**
     *
     */
    @Test
    fun sessionController_companionReport_intentionsHasHarmony() {
        setupReport()
        val getRequest = testController.testServer.get("/session/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = jacksonObjectMapper().readValue(body, CompanionReport::class.java)
        assertThat(data.intentions.containsKey("Harmony")).isTrue()
    }
}