package agartha.site.controllers

import agartha.data.objects.*
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.request.PractitionerInvolvedInformation
import agartha.site.objects.response.PractitionerReport
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
                SessionDBO(null, "Yoga", "Transformation",
                        LocalDateTime.now().minusDays(13),
                        LocalDateTime.now().minusDays(13)),
                SessionDBO(null, "Yoga", "Empowerment",
                        LocalDateTime.now().minusDays(11),
                        LocalDateTime.now().minusDays(11)),
                SessionDBO(null, "Meditation", "Harmony",
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().minusDays(5)),
                SessionDBO(null, "Yoga", "Freedom",
                        LocalDateTime.now().minusMinutes(41),
                        LocalDateTime.now().minusMinutes(1)))))
        //
        mockedService.insert(PractitionerDBO("b", LocalDateTime.now(), mutableListOf(
                SessionDBO(null, "Meditation", "Love",
                        LocalDateTime.now().minusMinutes(20),
                        LocalDateTime.now().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("c", LocalDateTime.now(), mutableListOf(
                SessionDBO(null, "Yoga", "Love",
                        LocalDateTime.now().minusDays(13),
                        LocalDateTime.now().minusDays(13)),
                SessionDBO(null, "Yoga", "Freedom",
                        LocalDateTime.now().minusDays(11),
                        LocalDateTime.now().minusDays(11)),
                SessionDBO(null, "Yoga", "Love",
                        LocalDateTime.now().minusDays(3).minusMinutes(45),
                        LocalDateTime.now().minusDays(3)),
                SessionDBO(null, "Meditation", "Harmony",
                        LocalDateTime.now().minusMinutes(20).minusSeconds(10),
                        LocalDateTime.now()))))
        //
        mockedService.insert(PractitionerDBO("d", LocalDateTime.now(), mutableListOf(
                SessionDBO(null, "Meditation", "Empathy",
                        LocalDateTime.now().minusMinutes(35),
                        LocalDateTime.now().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("e", LocalDateTime.now(), mutableListOf(
                SessionDBO(null, "Meditation", "Empowerment",
                        LocalDateTime.now().minusMinutes(35),
                        LocalDateTime.now().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("f", LocalDateTime.now(), mutableListOf(
                SessionDBO(null, "Meditation", "Celebration",
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().minusDays(5)))))
        // Ongoing sessions
        mockedService.insert(PractitionerDBO("g", LocalDateTime.now(), mutableListOf(
                SessionDBO(null, "Meditation", "Celebration",
                        LocalDateTime.now().minusMinutes(15)))))
        mockedService.insert(PractitionerDBO("h", LocalDateTime.now(), mutableListOf(
                SessionDBO(null, "Meditation", "Love",
                        LocalDateTime.now().minusMinutes(25)))))
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
        val data: PractitionerReport = ControllerUtil.stringToObject(body, PractitionerReport::class.java)
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
        val data: PractitionerReport = ControllerUtil.stringToObject(body, PractitionerReport::class.java)
        assertThat(data.practitionerId).isEqualTo("abc")
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
        val data: PractitionerReport = ControllerUtil.stringToObject(body, PractitionerReport::class.java)
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
        val data: PractitionerReport = ControllerUtil.stringToObject(body, PractitionerReport::class.java)
        assertThat(data.lastSessionMinutes).isEqualTo(20)
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
        val data: PractitionerReport = ControllerUtil.stringToObject(body, PractitionerReport::class.java)
        assertThat(data.totalSessionMinutes).isEqualTo(65)
    }

    /**
     *
     */
    @Test
    fun updatePractitioner_insertedUser_updatedUserWithInvolvedInformation() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", LocalDateTime.now(), mutableListOf()))
        //
        val involvedInformation = PractitionerInvolvedInformation(
                "Santa Clause",
                "santa@agartha.com",
                "Jag gillar yoga!")
        //
        val getRequest = testController.testServer.post(
                "/practitioner/abc",
                ControllerUtil.objectToString(involvedInformation),
                false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        val data: PractitionerDBO = ControllerUtil.stringToObject(body, PractitionerDBO::class.java)
        assertThat(data._id).isEqualTo("abc")
    }

    /**
     *
     */
    @Test
    fun startSession_discipline_Yoga() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", LocalDateTime.now(), mutableListOf()))
        //
        val postRequest = testController.testServer.post(
                "/practitioner/session/start/abc",
                "{\"discipline\":\"Yoga\",\"intention\":\"Salary raise\"}",
                false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        val data: SessionDBO = ControllerUtil.stringToObject(responseBody, SessionDBO::class.java)
        //
        assertThat(data.discipline).isEqualTo("Yoga")
    }

    /**
     *
     */
    @Test
    fun endSession_response_pracSessionsEndTimeIsNotNull() {
        // Setup
        mockedService.insert(
                PractitionerDBO("abc", LocalDateTime.now(), mutableListOf(
                        SessionDBO(null, "D", "I", LocalDateTime.now()))))

        val postRequest = testController.testServer.post("/practitioner/session/end/abc/8", "", false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        val prac = ControllerUtil.stringToObject(responseBody, PractitionerDBO::class.java)
        assertThat(prac.sessions.last().endTime).isNotNull()
    }

    /**
     *
     */
    @Test
    fun endSession_response_pracSpiritBankLogHas8PointsInLastLog() {
        // Setup
        mockedService.insert(
                PractitionerDBO("abc", LocalDateTime.now(), mutableListOf(
                        SessionDBO(null, "D", "I", LocalDateTime.now()))))

        val postRequest = testController.testServer.post("/practitioner/session/end/abc/8", "", false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        val prac = ControllerUtil.stringToObject(responseBody, PractitionerDBO::class.java)
        assertThat(prac.spiritBankLog.last().points).isEqualTo(8)
    }

    /**
     *
     */
    @Test
    fun endSession_response_pracSpiritBankLogHasTypeSESSIONInLastLog() {
        // Setup
        mockedService.insert(
                PractitionerDBO("abc", LocalDateTime.now(), mutableListOf(
                        SessionDBO(null, "D", "I", LocalDateTime.now()))))

        val postRequest = testController.testServer.post("/practitioner/session/end/abc/8", "", false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        val prac = ControllerUtil.stringToObject(responseBody, PractitionerDBO::class.java)
        assertThat(prac.spiritBankLog.last().type).isEqualTo(SpiritBankLogItemType.SESSION)
    }

    /**
     *
     */
    @Test
    fun endSession_response_pracSpiritBankLogStoredOneNewLog() {
        // Setup
        mockedService.insert(
                PractitionerDBO("abc", LocalDateTime.now(), mutableListOf(
                        SessionDBO(null, "D", "I", LocalDateTime.now()))))

        val postRequest = testController.testServer.post("/practitioner/session/end/abc/8", "", false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        val prac = ControllerUtil.stringToObject(responseBody, PractitionerDBO::class.java)
        assertThat(prac.spiritBankLog.size).isEqualTo(2)
    }

    @Test
    fun joinCircle_missingParams_404() {
        val request = testController.testServer.post("/cicle/join/a/", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(404)
    }

    @Test
    fun joinCircle_userIdMissing_400() {
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(DisciplineDBO("D", "D")),
                        intentions = listOf(IntentionDBO("I", "I")),
                        startTime = LocalDateTime.now().minusMinutes(10),
                        endTime = LocalDateTime.now().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        val request = testController.testServer.post("/practitioner/circle/join/a/1/D/I", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun joinCircle_circleIdMissing_400() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a"))
        val request = testController.testServer.post("/practitioner/circle/join/a/1/D/I", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun joinCircle_status_200() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a"))
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(DisciplineDBO("D", "D")),
                        intentions = listOf(IntentionDBO("I", "I")),
                        startTime = LocalDateTime.now().minusMinutes(10),
                        endTime = LocalDateTime.now().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post("/practitioner/circle/join/a/1/D/I", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun joinCircle_notStarted_status400() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a"))
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(DisciplineDBO("D", "D")),
                        intentions = listOf(IntentionDBO("I", "I")),
                        startTime = LocalDateTime.now().plusMinutes(5),
                        endTime = LocalDateTime.now().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post("/practitioner/circle/join/a/1/D/I", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun joinCircle_sessionAdded_size1() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a"))
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(DisciplineDBO("D", "D")),
                        intentions = listOf(IntentionDBO("I", "I")),
                        startTime = LocalDateTime.now().minusMinutes(10),
                        endTime = LocalDateTime.now().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post("/practitioner/circle/join/a/1/D/I", "", false)
        testController.testServer.execute(request)
        // validate the data source
        val practitioner = mockedService.getById("a")
        assertThat(practitioner!!.sessions.size).isEqualTo(1)
    }

    @Test
    fun joinCircle_addedSessionNotFinished_endTimeNull() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a"))
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(DisciplineDBO("D", "D")),
                        intentions = listOf(IntentionDBO("I", "I")),
                        startTime = LocalDateTime.now().minusMinutes(10),
                        endTime = LocalDateTime.now().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post("/practitioner/circle/join/a/1/D/I", "", false)
        val response = testController.testServer.execute(request)
        val session = ControllerUtil.stringToObject(String(response.body()), SessionDBO::class.java)
        assertThat(session.endTime).isNull()
    }

    @Test
    fun joinCircle_invalidDiscipline_status400() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a"))
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(DisciplineDBO("D", "D")),
                        intentions = listOf(IntentionDBO("I", "I")),
                        startTime = LocalDateTime.now().minusMinutes(10),
                        endTime = LocalDateTime.now().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post("/practitioner/circle/join/a/1/Q/I", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun joinCircle_invalidIntention_status400() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a"))
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(DisciplineDBO("D", "D")),
                        intentions = listOf(IntentionDBO("I", "I")),
                        startTime = LocalDateTime.now().minusMinutes(10),
                        endTime = LocalDateTime.now().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post("/practitioner/circle/join/a/1/D/Q", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun joinCircle_circleHasNoDiciplinesNoIntention_status200() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a"))
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(),
                        intentions = listOf(),
                        startTime = LocalDateTime.now().minusMinutes(10),
                        endTime = LocalDateTime.now().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        val request = testController.testServer.post("/practitioner/circle/join/a/1/D/I", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun joinCircle_circleHasNoDiciplinesNoIntention_sessionStored() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a"))
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(),
                        intentions = listOf(),
                        startTime = LocalDateTime.now().minusMinutes(10),
                        endTime = LocalDateTime.now().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        val request = testController.testServer.post("/practitioner/circle/join/a/1/D/I", "", false)
        val response = testController.testServer.execute(request)
        // validate the data source
        val practitioner = mockedService.getById("a")
        assertThat(practitioner!!.sessions.size).isEqualTo(1)
    }

    @Test
    fun joinCircle_circleHasNoDiciplinesNoIntention_disciplineStored() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a"))
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(),
                        intentions = listOf(),
                        startTime = LocalDateTime.now().minusMinutes(10),
                        endTime = LocalDateTime.now().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        val request = testController.testServer.post("/practitioner/circle/join/a/1/D/I", "", false)
        val response = testController.testServer.execute(request)
        // validate the data source
        val practitioner = mockedService.getById("a")
        assertThat(practitioner!!.sessions.get(0).discipline).isEqualTo("D")
    }

    @Test
    fun joinCircle_circleHasNoDiciplinesNoIntention_intentionStored() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a"))
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(),
                        intentions = listOf(),
                        startTime = LocalDateTime.now().minusMinutes(10),
                        endTime = LocalDateTime.now().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        val request = testController.testServer.post("/practitioner/circle/join/a/1/D/I", "", false)
        val response = testController.testServer.execute(request)
        // validate the data source
        val practitioner = mockedService.getById("a")
        assertThat(practitioner!!.sessions.get(0).intention).isEqualTo("I")
    }
}