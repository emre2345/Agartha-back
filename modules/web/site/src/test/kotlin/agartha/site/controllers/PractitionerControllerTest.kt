package agartha.site.controllers

import agartha.common.config.Settings.Companion.SPIRIT_BANK_START_POINTS
import agartha.common.utils.DateTimeFormat
import agartha.data.objects.*
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.request.PractitionerInvolvedInformation
import agartha.site.objects.response.PractitionerReport
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

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
        mockedService.insert(PractitionerDBO("a", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                SessionDBO(null, "Yoga", "Transformation",
                        DateTimeFormat.localDateTimeUTC().minusDays(13),
                        DateTimeFormat.localDateTimeUTC().minusDays(13)),
                SessionDBO(null, "Yoga", "Empowerment",
                        DateTimeFormat.localDateTimeUTC().minusDays(11),
                        DateTimeFormat.localDateTimeUTC().minusDays(11)),
                SessionDBO(null, "Meditation", "Harmony",
                        DateTimeFormat.localDateTimeUTC().minusDays(5),
                        DateTimeFormat.localDateTimeUTC().minusDays(5)),
                SessionDBO(null, "Yoga", "Freedom",
                        DateTimeFormat.localDateTimeUTC().minusMinutes(41),
                        DateTimeFormat.localDateTimeUTC().minusMinutes(1)))))
        //
        mockedService.insert(PractitionerDBO("b", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                SessionDBO(null, "Meditation", "Love",
                        DateTimeFormat.localDateTimeUTC().minusMinutes(20),
                        DateTimeFormat.localDateTimeUTC().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("c", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                SessionDBO(null, "Yoga", "Love",
                        DateTimeFormat.localDateTimeUTC().minusDays(13),
                        DateTimeFormat.localDateTimeUTC().minusDays(13)),
                SessionDBO(null, "Yoga", "Freedom",
                        DateTimeFormat.localDateTimeUTC().minusDays(11),
                        DateTimeFormat.localDateTimeUTC().minusDays(11)),
                SessionDBO(null, "Yoga", "Love",
                        DateTimeFormat.localDateTimeUTC().minusDays(3).minusMinutes(45),
                        DateTimeFormat.localDateTimeUTC().minusDays(3)),
                SessionDBO(null, "Meditation", "Harmony",
                        DateTimeFormat.localDateTimeUTC().minusMinutes(20).minusSeconds(10),
                        DateTimeFormat.localDateTimeUTC()))))
        //
        mockedService.insert(PractitionerDBO("d", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                SessionDBO(null, "Meditation", "Empathy",
                        DateTimeFormat.localDateTimeUTC().minusMinutes(35),
                        DateTimeFormat.localDateTimeUTC().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("e", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                SessionDBO(null, "Meditation", "Empowerment",
                        DateTimeFormat.localDateTimeUTC().minusMinutes(35),
                        DateTimeFormat.localDateTimeUTC().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("f", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                SessionDBO(null, "Meditation", "Celebration",
                        DateTimeFormat.localDateTimeUTC().minusDays(5),
                        DateTimeFormat.localDateTimeUTC().minusDays(5)))))
        // Ongoing sessions
        mockedService.insert(PractitionerDBO("g", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                SessionDBO(null, "Meditation", "Celebration",
                        DateTimeFormat.localDateTimeUTC().minusMinutes(15)))))
        mockedService.insert(PractitionerDBO("h", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                SessionDBO(null, "Meditation", "Love",
                        DateTimeFormat.localDateTimeUTC().minusMinutes(25)))))
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
        mockedService.insert(PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf()))
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
        mockedService.insert(PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf()))
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
        mockedService.insert(PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf()))
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

    @Test
    fun startSession_emptyDiscipline_status400() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf()))
        //
        val request = testController.testServer.post(
                "/practitioner/session/start/abc",
                """{"discipline":"","intention":"Salary raise"}""",
                false)
        val response = testController.testServer.execute(request)
        //
        assertThat(response.code()).isEqualTo(400)
    }

    /**
     *
     */
    @Test
    fun endSession_response_pracSessionsEndTimeIsNotNull() {
        // Setup
        mockedService.insert(
                PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                        SessionDBO(null, "D", "I", DateTimeFormat.localDateTimeUTC()))))

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
                PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                        SessionDBO(null, "D", "I", DateTimeFormat.localDateTimeUTC()))))

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
                PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                        SessionDBO(null, "D", "I", DateTimeFormat.localDateTimeUTC()))))

        val postRequest = testController.testServer.post("/practitioner/session/end/abc/8", "", false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        val prac = ControllerUtil.stringToObject(responseBody, PractitionerDBO::class.java)
        assertThat(prac.spiritBankLog.last().type).isEqualTo(SpiritBankLogItemType.ENDED_SESSION)
    }

    /**
     *
     */
    @Test
    fun endSession_response_pracSpiritBankLogStoredOneNewLog() {
        // Setup
        mockedService.insert(
                PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                        SessionDBO(null, "D", "I", DateTimeFormat.localDateTimeUTC()))))

        val postRequest = testController.testServer.post("/practitioner/session/end/abc/8", "", false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        val prac = ControllerUtil.stringToObject(responseBody, PractitionerDBO::class.java)
        assertThat(prac.spiritBankLog.size).isEqualTo(2)
    }

    /**
     *
     */
    @Test
    fun endSession_endedSessionInOwnCircle_spiritBankLogStoredType() {
        val circle = CircleDBO(
                name = "",
                description = "",
                startTime = DateTimeFormat.localDateTimeUTC(),
                endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(15),
                disciplines = listOf(),
                intentions = listOf(),
                minimumSpiritContribution = 4)
        // Setup prac with started session in own circle
        mockedService.insert(
                PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                        SessionDBO(null, "D", "I", DateTimeFormat.localDateTimeUTC(),
                                circle = circle)),
                        circles = listOf(circle)))
        // Setup prac with started session in someone else circle
        mockedService.insert(
                PractitionerDBO("dfg", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                        SessionDBO(null, "D", "I", DateTimeFormat.localDateTimeUTC(),
                                circle = circle))))

        val postRequest = testController.testServer.post("/practitioner/session/end/abc/8", "", false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        val prac = ControllerUtil.stringToObject(responseBody, PractitionerDBO::class.java)
        assertThat(prac.spiritBankLog.last().type).isEqualTo(SpiritBankLogItemType.ENDED_CREATED_CIRCLE)
    }

    @Test
    fun joinCircle_missingParams_404() {
        val request = testController.testServer.post(
                "/cicle/join/a/",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
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
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun joinCircle_intentionEmpty_400() {
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "a",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(DisciplineDBO("D", "D")),
                        intentions = listOf(IntentionDBO("I", "I")),
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":""}""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun joinCircle_circleIdMissing_400() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a"))
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
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
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
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
                        startTime = DateTimeFormat.localDateTimeUTC().plusMinutes(5),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
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
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
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
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
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
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"Q","intention":"I"}""",
                false)
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
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"Q"}""",
                false)
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
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
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
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
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
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
        val response = testController.testServer.execute(request)
        // validate the data source
        val practitioner = mockedService.getById("a")
        assertThat(practitioner!!.sessions.get(0).discipline).isEqualTo("D")
    }

    @Test
    fun joinCircle_circleHasNoDisciplinesNoIntention_intentionStored() {
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
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
        val response = testController.testServer.execute(request)
        // validate the data source
        val practitioner = mockedService.getById("a")
        assertThat(practitioner!!.sessions.get(0).intention).isEqualTo("I")
    }

    @Test
    fun joinCircle_circleCostContributionPointsFromSpiritBank_logItemStoredInSpiritBankLog() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a",
                spiritBankLog = listOf(SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = SPIRIT_BANK_START_POINTS))))
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(),
                        intentions = listOf(),
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 2))))
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
        val response = testController.testServer.execute(request)
        // validate the data source
        val practitioner = mockedService.getById("a")
        assertThat(practitioner!!.spiritBankLog.last().type).isEqualTo(SpiritBankLogItemType.JOINED_CIRCLE)
    }


    @Test
    fun joinCircle_circleCostUserCannotAffordToJoinCircleResponseStatus_400() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a",
                spiritBankLog = listOf(SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = SPIRIT_BANK_START_POINTS))))
        // Insert the creator of circle
        mockedService.insert(PractitionerDBO(
                _id = "b",
                circles = listOf(CircleDBO(
                        _id = "1",
                        name = "MyCircle",
                        description = "MyDescription",
                        disciplines = listOf(),
                        intentions = listOf(),
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 100))))
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun spiritBankHistory_userHas3Logs_size3() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a",
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = SPIRIT_BANK_START_POINTS),
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.ENDED_SESSION, points = 20),
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.JOINED_CIRCLE, points = -10)
                )))

        val request = testController.testServer.get("/practitioner/spiritbankhistory/a", false)
        val response = testController.testServer.execute(request)
        val responseBody = String(response.body())
        val history = ControllerUtil.stringToObjectList(responseBody, SpiritBankLogItemDBO::class.java)
        assertThat(history.size).isEqualTo(3)
    }
}