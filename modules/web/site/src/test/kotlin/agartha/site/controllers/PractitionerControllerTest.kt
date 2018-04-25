package agartha.site.controllers

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.objects.PractitionerReport
import agartha.site.objects.SessionReport
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import java.time.LocalDateTime

/**
 * Purpose of this file is to test the PractitionerReport controller
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class PractitionerControllerTest {

    companion object {
        val mockedService = MockedPractitionerService()
        val testController = ControllerServer()


        @BeforeClass
        @JvmStatic
        fun setupClass() {
            PractitionerController(mockedService)
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
    fun practitionerController_testEmptyUserId_status200() {
        val getRequest = testController.testServer.get("/session/", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    /**
     *
     */
    @Test
    fun practitionerController_testEmptyUserId_userCreated() {
        val getRequest = testController.testServer.get("/session/", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PractitionerReport = jacksonObjectMapper().readValue(body, PractitionerReport::class.java)
        assertThat(data.userId?.length).isEqualTo(36)
    }

    /**
     *
     */
    @Test
    fun practitionerController_testUserId_userExists() {
        // Setup
        mockedService.insert(PractitionerDBO(mutableListOf(), LocalDateTime.now(), "abc"))
        //
        val getRequest = testController.testServer.get("/session/abc", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PractitionerReport = jacksonObjectMapper().readValue(body, PractitionerReport::class.java)
        assertThat(data.userId).isEqualTo("abc")
    }

    /**
     *
     */
    @Test
    fun practitionerController_insertSession_sessionIdIs1() {
        // Setup
        mockedService.insert(PractitionerDBO(mutableListOf(), LocalDateTime.now(), "abc"))
        //
        val postRequest = testController.testServer.post("/session/abc/MyPractice", "", false)
        val httpResponse = testController.testServer.execute(postRequest)
        val body = String(httpResponse.body())
        assertThat(body).isEqualTo("1")
    }

    private fun setupReport() {
        // Setup, create 5 practitioner
        mockedService.insert(PractitionerDBO(mutableListOf(SessionDBO(0, "Yoga", false,
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:40:00"))), LocalDateTime.now(), "a"))
        //
        mockedService.insert(PractitionerDBO(mutableListOf(SessionDBO(0, "Mindfulness", false,
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:15:00"))), LocalDateTime.now(), "b"))
        //
        mockedService.insert(PractitionerDBO(mutableListOf(
                SessionDBO(0, "Yoga", false,
                        DateTimeFormat.stringToLocalDateTime("2018-04-15 12:00:00"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-15 12:45:00")),
                SessionDBO(1, "Meditation", false,
                        DateTimeFormat.stringToLocalDateTime("2018-04-18 11:59:59"),
                        DateTimeFormat.stringToLocalDateTime("2018-04-18 12:20:00"))), LocalDateTime.now(), "c"))
        //
        mockedService.insert(PractitionerDBO(mutableListOf(SessionDBO(0, "Mindfulness", false,
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:30:00"))), LocalDateTime.now(), "d"))
        //
        mockedService.insert(PractitionerDBO(mutableListOf(SessionDBO(0, "Mindfulness", false,
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-18 12:30:00"))), LocalDateTime.now(), "e"))
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
        assertThat(data.practitionerReport.userId).isEqualTo("c")
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
        assertThat(data.practitionerReport.lastSessionTime).isEqualTo(20)
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
        assertThat(data.practitionerReport.totalSessionTime).isEqualTo(65)
    }

    /**
     *
     */
    @Test
    fun practitionerController_compaionReport_countIs4() {
        setupReport()
        // Assume second (id b) is current user
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.companionReport.count).isEqualTo(4)
    }

    /**
     *
     */
    @Test
    fun practitionerController_compaionReport_minutesIs115() {
        setupReport()
        // Assume second (id b) is current user
        val getRequest = testController.testServer.get("/session/report/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.companionReport.minutes).isEqualTo(115)
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
        assertThat(data.companionReport.practices.containsKey("Yoga")).isTrue()
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
        assertThat(data.companionReport.practices.containsKey("Mindfulness")).isTrue()
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
        assertThat(data.companionReport.practices.containsKey("Meditation")).isFalse()
    }
}