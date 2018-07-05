package agartha.site.controllers

import agartha.common.config.Settings.Companion.SPIRIT_BANK_START_POINTS
import agartha.common.utils.DateTimeFormat
import agartha.data.objects.*
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.request.PractitionerInvolvedInformation
import agartha.site.objects.response.PractitionerReport
import io.schinzel.basicutils.configvar.IConfigVar
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

    class MockedConfig: IConfigVar {
        override fun getValue(p0: String?): String {
            return "100"
        }

    }

    companion object {
        val mockedService = MockedPractitionerService()
        val testController = ControllerServer()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            PractitionerController(mockedService, MockedConfig())
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
     * Create Practitioner
     */

    @Test
    fun getInformation_emptyUserId_status200() {
        val request = testController.testServer.get("/practitioner", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun getInformation_emptyUserId_userCreated() {
        val request = testController.testServer.get("/practitioner", false)
        val response = testController.testServer.execute(request)
        val body = String(response.body())
        // Map to Data object
        val data: PractitionerReport = ControllerUtil.stringToObject(body, PractitionerReport::class.java)
        assertThat(data.practitionerId?.length).isEqualTo(24)
    }

    /**
     * Get By Id
     */

    @Test
    fun getInformationUserExists_status_200() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf()))
        //
        val request = testController.testServer.get("/practitioner/abc", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun getInformationUserMissing_status_400() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf()))
        //
        val request = testController.testServer.get("/practitioner/def", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun getInformationUserExists_userId_C() {
        setupReport()
        val getRequest = testController.testServer.get("/practitioner/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PractitionerReport = ControllerUtil.stringToObject(body, PractitionerReport::class.java)
        assertThat(data.practitionerId).isEqualTo("c")
    }

    @Test
    fun getInformationUserExists_sessionTime_20() {
        setupReport()
        val getRequest = testController.testServer.get("/practitioner/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PractitionerReport = ControllerUtil.stringToObject(body, PractitionerReport::class.java)
        assertThat(data.lastSessionMinutes).isEqualTo(20)
    }

    @Test
    fun getInformationUserExists_totalSessionTime_65() {
        setupReport()
        val getRequest = testController.testServer.get("/practitioner/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PractitionerReport = ControllerUtil.stringToObject(body, PractitionerReport::class.java)
        assertThat(data.totalSessionMinutes).isEqualTo(65)
    }

    /**
     * Update Practitioner
     */


    @Test
    fun updatePractitionerUserExists_status_200() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf()))

        val request = testController.testServer.post(
                "/practitioner/abc",
                """{"fullName":"Santa Claus","email":"santa@agartha.com","description":"Jag gillar yoga!" }""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun updatePractitionerUserDoesNotExists_status_400() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf()))

        val request = testController.testServer.post(
                "/practitioner/def",
                """{"fullName":"Santa Claus","email":"santa@agartha.com","description":"Jag gillar yoga!" }""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun updatePractitioner_insertedUser_updatedUserWithInvolvedInformation() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf()))
        //
        val request = testController.testServer.post(
                "/practitioner/abc",
                """{"fullName":"Santa Claus","email":"santa@agartha.com","description":"Jag gillar yoga!" }""",
                false)
        val response = testController.testServer.execute(request)
        val body = String(response.body())
        val data: PractitionerDBO = ControllerUtil.stringToObject(body, PractitionerDBO::class.java)
        assertThat(data._id).isEqualTo("abc")
    }

    /**
     * Start Session
     */

    @Test
    fun startSessionValid_status_200() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf()))
        //
        val request = testController.testServer.post(
                "/practitioner/session/start/abc",
                "{\"discipline\":\"Yoga\",\"intention\":\"Salary raise\"}",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

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
    fun startSessionEmptyDiscipline_status_400() {
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

    @Test
    fun startSessionEmptyIntention_status_400() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf()))
        //
        val request = testController.testServer.post(
                "/practitioner/session/start/abc",
                """{"discipline":"Meditation","intention":""}""",
                false)
        val response = testController.testServer.execute(request)
        //
        assertThat(response.code()).isEqualTo(400)
    }

    /**
     * End Session
     */

    @Test
    fun endSessionValid_status_200() {
        // Setup
        mockedService.insert(
                PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                        SessionDBO(null, "D", "I", DateTimeFormat.localDateTimeUTC()))))

        val request = testController.testServer.post("/practitioner/session/end/abc/8", "", false)
        val response = testController.testServer.execute(request)
       assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun endSessionUserMissing_status_400() {
        // Setup
        mockedService.insert(
                PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                        SessionDBO(null, "D", "I", DateTimeFormat.localDateTimeUTC()))))

        val request = testController.testServer.post("/practitioner/session/end/def/8", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    /**
     * Join Circle
     */

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
                        minimumSpiritContribution = 2,
                        language = "Swedish"))))
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
                        minimumSpiritContribution = 2,
                        language = "Swedish"))))
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
                        minimumSpiritContribution = 2,
                        language = "Swedish"))))
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
                        minimumSpiritContribution = 2,
                        language = "Swedish"))))
        // let user id a join session 1 from user b
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
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
                        minimumSpiritContribution = 2,
                        language = "Swedish"))))
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
                        minimumSpiritContribution = 2,
                        language = "Swedish"))))
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
                        minimumSpiritContribution = 2,
                        language = "Swedish"))))
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
                        minimumSpiritContribution = 2,
                        language = "Swedish"))))
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
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
                        minimumSpiritContribution = 100,
                        language = "Swedish"))))
        val request = testController.testServer.post(
                "/practitioner/circle/join/a/1",
                """{"geolocation":null,"discipline":"D","intention":"I"}""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    /**********************
     * register To Circle *
     **********************/

    @Test
    fun registerToCircleUserMissing_status_400() {
        // Insert the user with the circle
        mockedService.insert(PractitionerDBO(_id = "b",
                circles = listOf(CircleDBO(
                        _id = "3",
                        name = "TheCircle",
                        description = "TheDescription",
                        disciplines = listOf(),
                        intentions = listOf(),
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 100,
                        language = "Swedish"))))
        val request = testController.testServer.post("/practitioner/circle/register/a/3", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun registerToCircleCircleMissing_status_400() {
        // Insert current cuser
        mockedService.insert(PractitionerDBO(_id = "a",
                registeredCircles = listOf()))
        // Insert the user with the circle
        mockedService.insert(PractitionerDBO(_id = "b",
                circles = listOf(CircleDBO(
                        _id = "3",
                        name = "TheCircle",
                        description = "TheDescription",
                        disciplines = listOf(),
                        intentions = listOf(),
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 100,
                        language = "Swedish"))))
        val request = testController.testServer.post("/practitioner/circle/register/a/4", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun registerToCircleValid_status_200() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a",
                registeredCircles = listOf("1", "2")))
        // Insert the user with the circle
        mockedService.insert(PractitionerDBO(_id = "b",
                circles = listOf(CircleDBO(
                        _id = "3",
                        name = "TheCircle",
                        description = "TheDescription",
                        disciplines = listOf(),
                        intentions = listOf(),
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 100,
                        language = "Swedish"))))
        val request = testController.testServer.post("/practitioner/circle/register/a/3", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun registerToCircleValid_practitionerReturnedWithId_a() {
        // Insert the current user
        mockedService.insert(PractitionerDBO(_id = "a",
                registeredCircles = listOf("1", "2")))
        // Insert the user with the circle
        mockedService.insert(PractitionerDBO(_id = "b",
                circles = listOf(CircleDBO(
                        _id = "3",
                        name = "TheCircle",
                        description = "TheDescription",
                        disciplines = listOf(),
                        intentions = listOf(),
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(10),
                        minimumSpiritContribution = 100,
                        language = "Swedish"))))

        val request = testController.testServer.post("/practitioner/circle/register/a/3", "", false)
        val response = testController.testServer.execute(request)
        val responseBody = String(response.body())
        val practitioner = ControllerUtil.stringToObject(responseBody, PractitionerDBO::class.java)
        assertThat(practitioner._id).isEqualTo("a")
    }


    /***********************
     * spirit Bank History *
     ***********************/

    @Test
    fun spiritBankHistoryValid_status_200() {
        // Setup
        mockedService.insert(
                PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                        SessionDBO(null, "D", "I", DateTimeFormat.localDateTimeUTC()))))

        val request = testController.testServer.get("/practitioner/spiritbankhistory/abc", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun spiritBankHistoryUserMissing_status_400() {
        // Setup
        mockedService.insert(
                PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                        SessionDBO(null, "D", "I", DateTimeFormat.localDateTimeUTC()))))

        val request = testController.testServer.get("/practitioner/spiritbankhistory/a", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun spiritBankHistory_responseIsListOfSpritLogItemsWithSize_3() {
        // Setup
        mockedService.insert(
                PractitionerDBO(
                        _id ="abc",
                        spiritBankLog = listOf(
                                SpiritBankLogItemDBO(type = SpiritBankLogItemType.ADD_VIRTUAL_TO_CIRCLE, points = 50),
                                SpiritBankLogItemDBO(type = SpiritBankLogItemType.ENDED_SESSION, points = 40),
                                SpiritBankLogItemDBO(type = SpiritBankLogItemType.ENDED_CREATED_CIRCLE, points = 30))))

        val request = testController.testServer.get("/practitioner/spiritbankhistory/abc", false)
        val response = testController.testServer.execute(request)
        val list = ControllerUtil.stringToObjectList(String(response.body()), SpiritBankLogItemDBO::class.java)
        assertThat(list.size).isEqualTo(3)
    }

    /*************************
     * find by Email address *
     *************************/

    @Test
    fun findByEmailAddressExists_status_200() {
        mockedService.insert(PractitionerDBO(_id = "a", email = "someone@agartha.com"))
        val request = testController.testServer.get("/practitioner/find/email/someone@agartha.com", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun findByEmailAddressExists_returnedUserId_a() {
        mockedService.insert(PractitionerDBO(_id = "a", email = "someone@agartha.com"))
        val request = testController.testServer.get("/practitioner/find/email/someone@agartha.com", false)
        val response = testController.testServer.execute(request)
        val responseBody = String(response.body())
        val practitioner = ControllerUtil.stringToObject(responseBody, PractitionerDBO::class.java)
        assertThat(practitioner._id).isEqualTo("a")
    }

    @Test
    fun findByEmailAddressMissing_status_404() {
        mockedService.insert(PractitionerDBO(_id = "a", email = "someone@agartha.com"))
        val request = testController.testServer.get("/practitioner/find/email/someoneelse@agartha.com", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(404)
    }

    @Test
    fun findByEmailNoAddress_status_404() {
        mockedService.insert(PractitionerDBO(_id = "a", email = "someone@agartha.com"))
        val request = testController.testServer.get("/practitioner/find/email/", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(404)
    }

    /*************************
     * donate                *
     *************************/

    @Test
    fun donateGiverMissing_status_400() {
        mockedService.insert(PractitionerDBO(_id = "b"))

        val request = testController.testServer.post("/practitioner/donate/a/b/7", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun donateRecieverMissing_status_400() {
        mockedService.insert(PractitionerDBO(_id = "a"))

        val request = testController.testServer.post("/practitioner/donate/a/b/7", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun donateNegativeValue_status_400() {
        mockedService.insert(PractitionerDBO(_id = "a"))
        mockedService.insert(PractitionerDBO(_id = "b"))

        val request = testController.testServer.post("/practitioner/donate/a/b/-7", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun donateValueNotAnNumber_status_500() {
        mockedService.insert(PractitionerDBO(_id = "a"))
        mockedService.insert(PractitionerDBO(_id = "b"))

        val request = testController.testServer.post("/practitioner/donate/a/b/seven", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(500)
    }

    @Test
    fun donateGiverOutOfFunds_status_400() {
        mockedService.insert(PractitionerDBO(_id = "a", spiritBankLog = listOf(SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = 5))))
        mockedService.insert(PractitionerDBO(_id = "b"))

        val request = testController.testServer.post("/practitioner/donate/a/b/7", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }


    @Test
    fun donateSuccessful_status_200() {
        mockedService.insert(PractitionerDBO(_id = "a", spiritBankLog = listOf(SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = 10))))
        mockedService.insert(PractitionerDBO(_id = "b"))

        val request = testController.testServer.post("/practitioner/donate/a/b/7", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }
}