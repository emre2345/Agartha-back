package agartha.site.controllers

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.site.controllers.mocks.MockedSessionService
import agartha.site.objects.response.SessionReport
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions
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


    /**
     *
     */
    @Test
    fun practitionerController_insertSession_sessionIdIs1() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", LocalDateTime.now(), mutableListOf()))
        //
        val postRequest = testController.testServer.post("/session/abc/MyPractice", "", false)
        val httpResponse = testController.testServer.execute(postRequest)
        val body = String(httpResponse.body())
        Assertions.assertThat(body).isEqualTo("1")
    }

    private fun setupReport() {
        // Setup, create 5 practitioner
        mockedService.insert(PractitionerDBO("a", LocalDateTime.now(), mutableListOf(SessionDBO(0, "Yoga", false,
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:40:00")))))
        //
        mockedService.insert(PractitionerDBO("b", LocalDateTime.now(), mutableListOf(SessionDBO(0, "Mindfulness", false,
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:15:00")))))
        //
        mockedService.insert(PractitionerDBO("c", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, "Yoga", false,
                        DateTimeFormat.stringToLocalDateTime("2018-04-15 12:00:00"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-15 12:45:00")),
                SessionDBO(1, "Meditation", false,
                        DateTimeFormat.stringToLocalDateTime("2018-04-18 11:59:59"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-18 12:20:00")))))
        //
        mockedService.insert(PractitionerDBO("d", LocalDateTime.now(), mutableListOf(SessionDBO(0, "Mindfulness", false,
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:30:00")))))
        //
        mockedService.insert(PractitionerDBO("e", LocalDateTime.now(), mutableListOf(SessionDBO(0, "Mindfulness", false,
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:30:00")))))
    }

    /**
     *
     */
    @Test
    fun practitionerController_practitionerReport_userIdIsC() {
        setupReport()
        // Assume second (id b) is current user
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        Assertions.assertThat(data.practitionerReport.practitionerId).isEqualTo("c")
    }

    /**
     *
     */
    @Test
    fun practitionerController_practitionerReport_sessionTime20() {
        setupReport()
        // Assume second (id b) is current user
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        Assertions.assertThat(data.practitionerReport.lastSessionTime).isEqualTo(20)
    }

    /**
     *
     */
    @Test
    fun practitionerController_practitionerReport_totalSessionTime65() {
        setupReport()
        // Assume second (id b) is current user
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        Assertions.assertThat(data.practitionerReport.totalSessionTime).isEqualTo(65)
    }

    /**
     *
     */
    @Test
    fun practitionerController_compaionReport_practitionerCountIs4() {
        setupReport()
        // Assume second (id b) is current user
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        Assertions.assertThat(data.companionReport.practitionerCount).isEqualTo(4)
    }

    /**
     *
     */
    @Test
    fun practitionerController_compaionReport_sessionCountIs4() {
        setupReport()
        // Assume second (id b) is current user
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        Assertions.assertThat(data.companionReport.sessionCount).isEqualTo(4)
    }

    /**
     *
     */
    @Test
    fun practitionerController_companionReport_sessionMinutesIs115() {
        setupReport()
        // Assume second (id b) is current user
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        Assertions.assertThat(data.companionReport.sessionMintes).isEqualTo(115)
    }

    /**
     *
     */
    @Test
    fun practitionerController_companionReport_practicesHasYoga() {
        setupReport()
        // Assume second (id b) is current user
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        Assertions.assertThat(data.companionReport.practices.containsKey("Yoga")).isTrue()
    }

    /**
     *
     */
    @Test
    fun practitionerController_companionReport_practicesHasMindful() {
        setupReport()
        // Assume second (id b) is current user
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        Assertions.assertThat(data.companionReport.practices.containsKey("Mindfulness")).isTrue()
    }

    /**
     * Current users practition should not be included
     */
    @Test
    fun practitionerController_companionReport_practicesHasNotMeditation() {
        setupReport()
        // Assume second (id b) is current user
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        Assertions.assertThat(data.companionReport.practices.containsKey("Meditation")).isFalse()
    }
}