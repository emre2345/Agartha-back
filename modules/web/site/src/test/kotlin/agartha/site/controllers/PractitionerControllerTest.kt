package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.objects.request.PractitionerInvolvedInformation
import agartha.site.objects.response.PractitionerReport
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import java.time.LocalDateTime

/**
 * Purpose of this file is to test the Practitioner controller
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

    private fun setupReport() {
        //
        mockedService.insert(PractitionerDBO("a", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, null,"Yoga", "Transformation",
                        LocalDateTime.now().minusDays(13),
                        LocalDateTime.now().minusDays(13)),
                SessionDBO(1, null,"Yoga", "Empowerment",
                        LocalDateTime.now().minusDays(11),
                        LocalDateTime.now().minusDays(11)),
                SessionDBO(2, null,"Meditation", "Harmony",
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().minusDays(5)),
                SessionDBO(3, null,"Yoga", "Freedom",
                        LocalDateTime.now().minusMinutes(41),
                        LocalDateTime.now().minusMinutes(1)))))
        //
        mockedService.insert(PractitionerDBO("b", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, null,"Meditation", "Love",
                        LocalDateTime.now().minusMinutes(20),
                        LocalDateTime.now().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("c", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, null,"Yoga", "Love",
                        LocalDateTime.now().minusDays(13),
                        LocalDateTime.now().minusDays(13)),
                SessionDBO(1, null,"Yoga", "Freedom",
                        LocalDateTime.now().minusDays(11),
                        LocalDateTime.now().minusDays(11)),
                SessionDBO(2, null,"Yoga", "Love",
                        LocalDateTime.now().minusDays(3).minusMinutes(45),
                        LocalDateTime.now().minusDays(3)),
                SessionDBO(3, null,"Meditation","Harmony",
                        LocalDateTime.now().minusMinutes(20).minusSeconds(10),
                        LocalDateTime.now()))))
        //
        mockedService.insert(PractitionerDBO("d", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, null,"Meditation", "Empathy",
                        LocalDateTime.now().minusMinutes(35),
                        LocalDateTime.now().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("e", LocalDateTime.now(), mutableListOf(
                SessionDBO(0,null, "Meditation", "Empowerment",
                        LocalDateTime.now().minusMinutes(35),
                        LocalDateTime.now().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("f", LocalDateTime.now(), mutableListOf(
                SessionDBO(0,null, "Meditation", "Celebration",
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().minusDays(5)))))
    }

    /**
     *
     */
    @Test
    fun getInformation_emptyUserId_status200() {
        val getRequest = testController.testServer.get("/practitioner", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    /**
     *
     */
    @Test
    fun getInformation_emptyUserId_userCreated() {
        val getRequest = testController.testServer.get("/practitioner", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PractitionerReport = jacksonObjectMapper().readValue(body, PractitionerReport::class.java)
        assertThat(data.practitionerId?.length).isEqualTo(24)
    }

    /**
     *
     */
    @Test
    fun getInformation_userId_userExists() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", LocalDateTime.now(), mutableListOf()))
        //
        val getRequest = testController.testServer.get("/practitioner/abc", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PractitionerReport = jacksonObjectMapper().readValue(body, PractitionerReport::class.java)
        assertThat(data.practitionerId).isEqualTo("abc")
    }

    /**
     *
     */
    @Test
    fun updatePractitioner_insertedUser_updatedUserWithInvolvedInformation() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", LocalDateTime.now(), mutableListOf()))
        val involvedInformation = PractitionerInvolvedInformation(
                "Rebecca",
                "rebecca@kollektiva.se",
                "Jag gillar yoga!")
        //
        val getRequest = testController.testServer.post("/practitioner/abc", jacksonObjectMapper().writeValueAsString(involvedInformation), false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object (cannot handle LocalDateTime)
        // https://stackoverflow.com/questions/27952472/serialize-deserialize-java-8-java-time-with-jackson-json-mapper/27952473
        //val data: PractitionerDBO = jacksonObjectMapper().readValue(body, PractitionerDBO::class.java)
        assertThat(body).startsWith("{\"_id\":\"abc\"")
    }

    /**
     *
     */
    @Test
    fun insertSession_withPracticeName_1() {
        val body: String = "{\"discipline\": \"Yoga\",\n\"practice\": \"Hatha\",\n\"intention\": \"Salary raise\"\n}"
        // Setup
        mockedService.insert(PractitionerDBO("abc", LocalDateTime.now(), mutableListOf()))
        //
        val postRequest = testController.testServer.post("/practitioner/session/abc", body, false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        // Exception com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
        //     Cannot construct instance of `java.time.LocalDateTime` (no Creators, like default construct, exist):
        //     cannot deserialize from Object value (no delegate- or property-based Creator)
        //
        //val session: SessionDBO = jacksonObjectMapper().readValue(responseBody, SessionDBO::class.java)
        assertThat(responseBody).startsWith("{\"index\":1")
    }

    /**
     *
     */
    @Test
    fun insertSession_withoutPracticeName_1() {
        val body: String = "{\"discipline\": \"Yoga\",\n\"intention\": \"Salary raise\"\n}"
        // Setup
        mockedService.insert(PractitionerDBO("abc", LocalDateTime.now(), mutableListOf()))
        //
        val postRequest = testController.testServer.post("/practitioner/session/abc", body, false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        //
        assertThat(responseBody).startsWith("{\"index\":1")
    }


    /**
     *
     */
    @Test
    fun getInformation_userId_C() {
        setupReport()
        val getRequest = testController.testServer.get("/practitioner/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PractitionerReport = jacksonObjectMapper().readValue(body, PractitionerReport::class.java)
        assertThat(data.practitionerId).isEqualTo("c")
    }

    /**
     *
     */
    @Test
    fun getInformation_sessionTime_20() {
        setupReport()
        val getRequest = testController.testServer.get("/practitioner/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PractitionerReport = jacksonObjectMapper().readValue(body, PractitionerReport::class.java)
        assertThat(data.lastSessionTime).isEqualTo(20)
    }

    /**
     *
     */
    @Test
    fun getInformation_totalSessionTime_65() {
        setupReport()
        val getRequest = testController.testServer.get("/practitioner/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PractitionerReport = jacksonObjectMapper().readValue(body, PractitionerReport::class.java)
        assertThat(data.totalSessionTime).isEqualTo(65)
    }
}