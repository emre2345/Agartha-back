package agartha.site.controllers

import agartha.common.config.Settings.Companion.SPIRIT_BANK_START_POINTS
import agartha.data.objects.*
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.response.CircleReport
import io.schinzel.basicutils.configvar.IConfigVar
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import java.time.LocalDateTime

/**
 * Purpose of this class is tests for circle controller
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-06-08.
 */
class CircleControllerTest {

    class ConfigDummy : IConfigVar {
        override fun getValue(p0: String?): String {
            return "50"
        }

    }

    companion object {
        val mockedService = MockedPractitionerService()
        val testController = ControllerServer()

        /**
         *
         */
        @BeforeClass
        @JvmStatic
        fun setupClass() {
            CircleController(mockedService, ConfigDummy())
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

    private fun setup() {
        // User with no circles
        mockedService.insert(PractitionerDBO(
                _id = "a",
                created = LocalDateTime.now(),
                sessions = listOf(),
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = SPIRIT_BANK_START_POINTS)
                )))
        // User with 3 circles
        mockedService.insert(PractitionerDBO(
                _id = "b",
                created = LocalDateTime.now(),
                sessions = listOf(),
                circles = listOf(
                        CircleDBO(
                                name = "",
                                description = "",
                                startTime = LocalDateTime.now().plusHours(1),
                                endTime = LocalDateTime.now().plusHours(3),
                                intentions = listOf(),
                                disciplines = listOf(),
                                minimumSpiritContribution = 5),
                        CircleDBO(
                                name = "",
                                description = "",
                                startTime = LocalDateTime.now().minusHours(1),
                                endTime = LocalDateTime.now().plusHours(1),
                                intentions = listOf(),
                                disciplines = listOf(),
                                minimumSpiritContribution = 5),
                        CircleDBO(
                                name = "",
                                description = "",
                                startTime = LocalDateTime.now().plusHours(14),
                                endTime = LocalDateTime.now().plusHours(15),
                                intentions = listOf(),
                                disciplines = listOf(),
                                minimumSpiritContribution = 5)),
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = SPIRIT_BANK_START_POINTS)
                )))
        // User with 1 circles
        mockedService.insert(PractitionerDBO(
                _id = "c",
                created = LocalDateTime.now(),
                sessions = listOf(),
                circles = listOf(
                        CircleDBO(
                                _id = "c1",
                                name = "CName",
                                description = "CDesc",
                                startTime = LocalDateTime.now().minusMinutes(30),
                                endTime = LocalDateTime.now().plusMinutes(90),
                                intentions = listOf(),
                                disciplines = listOf(),
                                minimumSpiritContribution = 5)),
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(created = LocalDateTime.now().minusHours(4), type = SpiritBankLogItemType.START, points = SPIRIT_BANK_START_POINTS),
                        SpiritBankLogItemDBO(created = LocalDateTime.now(), type = SpiritBankLogItemType.START, points = 3)
                )))

        // User without enough points in spiritBankLog
        mockedService.insert(PractitionerDBO(
                _id = "d",
                created = LocalDateTime.now(),
                sessions = listOf(
                        SessionDBO(
                                discipline = "D",
                                intention = "I",
                                circle = CircleDBO(
                                        _id = "c1",
                                        name = "CName",
                                        description = "CDesc",
                                        intentions = listOf(),
                                        disciplines = listOf(),
                                        startTime = LocalDateTime.now().minusMinutes(30),
                                        endTime = LocalDateTime.now().plusMinutes(30),
                                        minimumSpiritContribution = 12))
                ),
                circles = listOf(),
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = SPIRIT_BANK_START_POINTS),
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = 49)
                )))

    }

    @Test
    fun getAll_status_200() {
        val request = testController.testServer.get("/circle", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun getAll_size_4() {
        setup()
        val request = testController.testServer.get("/circle", false)
        val response = testController.testServer.execute(request)
        val circles = ControllerUtil.stringToObjectList(String(response.body()), CircleDBO::class.java)
        assertThat(circles.size).isEqualTo(4)
    }

    @Test
    fun getActive_status_200() {
        val request = testController.testServer.get("/circle/active", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun getActive_size_2() {
        setup()
        val request = testController.testServer.get("/circle/active", false)
        val response = testController.testServer.execute(request)
        val circles = ControllerUtil.stringToObjectList(String(response.body()), CircleDBO::class.java)
        assertThat(circles.size).isEqualTo(2)
    }

    @Test
    fun addCircle_statusNoGeolocation_200() {
        setup()
        val request = testController.testServer.post(
                "/circle/add/a",
                """{
                        "name":"MyCircle Name",
                        "description":"MyCircle Desc",
                        "startTime":"2020-03-15T12:00:00.000Z",
                        "endTime":"2020-03-15T14:00:00.000Z",
                        "disciplines":[],
                        "intentions":[],
                        "minimumSpiritContribution":14
                        }""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun addCircle_statusGeolocation_200() {
        setup()
        val request = testController.testServer.post(
                "/circle/add/a",
                """{
                        "name":"MyCircle Name",
                        "description":"MyCircle Desc",
                        "geolocation":{"latitude":1.0,"longitude":1.0},
                        "startTime":"2020-03-15T12:00:00.000Z",
                        "endTime":"2020-03-15T14:00:00.000Z",
                        "disciplines":[],
                        "intentions":[],
                        "minimumSpiritContribution":14
                        }""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun addCircle_statusDisciplineAndIntention_200() {
        setup()
        val request = testController.testServer.post(
                "/circle/add/a",
                """{
                        "name":"MyCircle Name",
                        "description":"MyCircle Desc",
                        "startTime":"2020-03-15T12:00:00.000Z",
                        "endTime":"2020-03-15T14:00:00.000Z",
                        "disciplines":[{"title":"D","description":"D"}],
                        "intentions":[{"title":"I","description":"I"}],
                        "minimumSpiritContribution":14
                        }""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun addCircle_responseObjectCircles_1() {
        setup()
        val request = testController.testServer.post(
                "/circle/add/a",
                """{
                        "name":"MyCircle Name",
                        "description":"MyCircle Desc",
                        "startTime":"2020-03-15T12:00:00.000Z",
                        "endTime":"2020-03-15T14:00:00.000Z",
                        "disciplines":[],
                        "intentions":[],
                        "minimumSpiritContribution":14
                        }""",
                false)
        val response = testController.testServer.execute(request)
        val practitioner = ControllerUtil.stringToObject(String(response.body()), PractitionerDBO::class.java)
        assertThat(practitioner.circles.size).isEqualTo(1)
    }

    @Test
    fun addCircle_withoutEnoughPointsResponseStatus_400() {
        setup()
        val request = testController.testServer.post(
                "/circle/add/d",
                """{
                        "name":"MyCircle Name",
                        "description":"MyCircle Desc",
                        "startTime":"2020-03-15T12:00:00.000Z",
                        "endTime":"2020-03-15T14:00:00.000Z",
                        "disciplines":[],
                        "intentions":[],
                        "minimumSpiritContribution":14
                        }""",
                false)
        val response = testController.testServer.execute(request)
        val practitioner = ControllerUtil.stringToObject(String(response.body()), PractitionerDBO::class.java)
        assertThat(practitioner.circles.size).isEqualTo(1)
    }

    @Test
    fun receipt_status_200() {
        setup()
        val request = testController.testServer.get("/circle/receipt/c/c1", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun receipt_practitionerMissing_400(){
        setup()
        val request = testController.testServer.get("/circle/receipt/s/c1", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun receipt_practitionerNotCreator_400(){
        setup()
        val request = testController.testServer.get("/circle/receipt/a/c1", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun receipt_circleName_CName() {
        setup()
        val request = testController.testServer.get("/circle/receipt/c/c1", false)
        val response = testController.testServer.execute(request)
        val report = ControllerUtil.stringToObject(String(response.body()), CircleReport::class.java)
        assertThat(report.name).isEqualTo("CName")
    }

    @Test
    fun receipt_circleDescripition_CDesc() {
        setup()
        val request = testController.testServer.get("/circle/receipt/c/c1", false)
        val response = testController.testServer.execute(request)
        val report = ControllerUtil.stringToObject(String(response.body()), CircleReport::class.java)
        assertThat(report.description).isEqualTo("CDesc")
    }

    @Test
    fun receipt_circleSessions_3() {
        setup()
        val request = testController.testServer.get("/circle/receipt/c/c1", false)
        val response = testController.testServer.execute(request)
        val report = ControllerUtil.stringToObject(String(response.body()), CircleReport::class.java)
        assertThat(report.numberOfPractitioners).isEqualTo(1)
    }

    @Test
    fun receipt_circlePoints_1() {
        setup()
        val request = testController.testServer.get("/circle/receipt/c/c1", false)
        val response = testController.testServer.execute(request)
        val report = ControllerUtil.stringToObject(String(response.body()), CircleReport::class.java)
        assertThat(report.generatedPoints).isEqualTo(3)
    }
}