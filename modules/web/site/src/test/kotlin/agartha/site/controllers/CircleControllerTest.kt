package agartha.site.controllers

import agartha.common.config.Settings.Companion.SPIRIT_BANK_START_POINTS
import agartha.common.utils.DateTimeFormat
import agartha.data.objects.*
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.response.CircleReport
import agartha.site.objects.response.RegisteredReport
import io.schinzel.basicutils.configvar.IConfigVar
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

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
                created = DateTimeFormat.localDateTimeUTC(),
                sessions = listOf(),
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = SPIRIT_BANK_START_POINTS)
                ),
                registeredCircles = listOf("1", "c1")))
        // User with 3 circles
        mockedService.insert(PractitionerDBO(
                _id = "b",
                created = DateTimeFormat.localDateTimeUTC(),
                sessions = listOf(),
                circles = listOf(
                        CircleDBO(
                                _id = "1",
                                name = "",
                                description = "",
                                startTime = DateTimeFormat.localDateTimeUTC().plusHours(1),
                                endTime = DateTimeFormat.localDateTimeUTC().plusHours(3),
                                intentions = listOf(),
                                disciplines = listOf(),
                                minimumSpiritContribution = 5,
                                language = "Swedish",
                                virtualRegistered = 3),
                        CircleDBO(
                                name = "",
                                description = "",
                                startTime = DateTimeFormat.localDateTimeUTC().minusHours(1),
                                endTime = DateTimeFormat.localDateTimeUTC().plusHours(1),
                                intentions = listOf(),
                                disciplines = listOf(),
                                minimumSpiritContribution = 5,
                                language = "Swedish"),
                        CircleDBO(
                                name = "",
                                description = "",
                                startTime = DateTimeFormat.localDateTimeUTC().plusHours(14),
                                endTime = DateTimeFormat.localDateTimeUTC().plusHours(15),
                                intentions = listOf(),
                                disciplines = listOf(),
                                minimumSpiritContribution = 5,
                                language = "Swedish")),
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = SPIRIT_BANK_START_POINTS)
                )))
        // User with 1 circles
        mockedService.insert(PractitionerDBO(
                _id = "c",
                created = DateTimeFormat.localDateTimeUTC(),
                sessions = listOf(),
                circles = listOf(
                        CircleDBO(
                                _id = "c1",
                                name = "CName",
                                description = "CDesc",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30),
                                endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(90),
                                intentions = listOf(),
                                disciplines = listOf(),
                                minimumSpiritContribution = 5,
                                language = "Swedish")),
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(created = DateTimeFormat.localDateTimeUTC().minusHours(4), type = SpiritBankLogItemType.START, points = SPIRIT_BANK_START_POINTS),
                        SpiritBankLogItemDBO(created = DateTimeFormat.localDateTimeUTC(), type = SpiritBankLogItemType.START, points = 3)
                )))

        // User without enough points in spiritBankLog
        mockedService.insert(PractitionerDBO(
                _id = "d",
                created = DateTimeFormat.localDateTimeUTC(),
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
                                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30),
                                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(30),
                                        minimumSpiritContribution = 12,
                                        language = "Swedish"))
                ),
                circles = listOf(),
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = SPIRIT_BANK_START_POINTS),
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = 49)
                )))

    }
    /**********
     * getAll *
     **********/
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

    /*************
     * getActive *
     *************/
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

    /**************
     * getCreated *
     **************/
    @Test
    fun getCreated_status_200() {
        setup()
        val request = testController.testServer.get("/circle/created/b", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun getCreated_size_3() {
        setup()
        val request = testController.testServer.get("/circle/created/b", false)
        val response = testController.testServer.execute(request)
        val circles = ControllerUtil.stringToObjectList(String(response.body()), CircleDBO::class.java)
        assertThat(circles.size).isEqualTo(3)
    }

    /*****************
     * getRegistered *
     *****************/
    @Test
    fun getRegistered_status_200() {
        setup()
        val request = testController.testServer.get("/circle/registered/a", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun getRegistered_size_2() {
        setup()
        val request = testController.testServer.get("/circle/registered/a", false)
        val response = testController.testServer.execute(request)
        val circles = ControllerUtil.stringToObjectList(String(response.body()), CircleDBO::class.java)
        assertThat(circles.size).isEqualTo(2)
    }

    /**********************
     * getTotalRegistered *
     **********************/
    @Test
    fun getTotalRegistered_status_200() {
        setup()
        val request = testController.testServer.get("/circle/registered/total/1", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun getTotalRegistered_virtualRegistered_2() {
        setup()
        val request = testController.testServer.get("/circle/registered/total/1", false)
        val response = testController.testServer.execute(request)
        val registered = ControllerUtil.stringToObject(String(response.body()), RegisteredReport::class.java)
        assertThat(registered.virtualRegistered).isEqualTo(3)
    }

    @Test
    fun getTotalRegistered_practitionerRegistered_1() {
        setup()
        val request = testController.testServer.get("/circle/registered/total/1", false)
        val response = testController.testServer.execute(request)
        val registered = ControllerUtil.stringToObject(String(response.body()), RegisteredReport::class.java)
        assertThat(registered.practitionersRegistered).isEqualTo(1)
    }

    /****************
     * giveFeedback *
     ****************/
    @Test
    fun giveFeedback_status_200() {
        setup()
        val request = testController.testServer.post("/circle/feedback/1/99", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }
    @Test
    fun giveFeedback_circleDoesNotExistStatus_400() {
        setup()
        val request = testController.testServer.post("/circle/feedback/99/99", "",false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }
    @Test
    fun giveFeedback_response_circle() {
        setup()
        val request = testController.testServer.post("/circle/feedback/1/99", "",false)
        val response = testController.testServer.execute(request)
        val circle = ControllerUtil.stringToObject(String(response.body()), CircleDBO::class.java)
        assertThat(circle._id).isEqualTo("1")
    }

    /*************
     * addCircle *
     *************/
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
                        "minimumSpiritContribution":14,
                        "language": "Swedish",
                        "virtualRegistered": 0
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
                        "minimumSpiritContribution":14,
                        "language": "Swedish",
                        "virtualRegistered": 0
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
                        "minimumSpiritContribution":14,
                        "language": "Swedish",
                        "virtualRegistered": 0
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
                        "minimumSpiritContribution":14,
                        "language": "Swedish",
                        "virtualRegistered": 0
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
                        "minimumSpiritContribution":14,
                        "language": "Swedish",
                        "virtualRegistered": 0
                        }""",
                false)
        val response = testController.testServer.execute(request)
        val practitioner = ControllerUtil.stringToObject(String(response.body()), PractitionerDBO::class.java)
        assertThat(practitioner.circles.size).isEqualTo(1)
    }

    /**************
     * editCircle *
     **************/
    @Test
    fun editCircle_circleChangedAttributes_NameMyCircle() {
        setup()
        val request = testController.testServer.post(
                "/circle/edit/b",
                """{
                        "_id": "1",
                        "name":"MyCircle Name",
                        "description":"MyCircle Desc",
                        "startTime":"2020-03-15T12:00:00.000Z",
                        "endTime":"2020-03-15T14:00:00.000Z",
                        "disciplines":[],
                        "intentions":[],
                        "minimumSpiritContribution":14,
                        "language": "Swedish",
                        "virtualRegistered": 0
                        }""",
                false)
        val response = testController.testServer.execute(request)
        val practitioner = ControllerUtil.stringToObject(String(response.body()), PractitionerDBO::class.java)
        val circle =  practitioner.circles.find { it._id == "1" }
        assertThat(circle!!.name).isEqualTo("MyCircle Name")
    }


    @Test
    fun editCircle_practitionerDoesNotExistResponseStatus_400() {
        setup()
        val request = testController.testServer.post(
                "/circle/edit/9",
                """{
                        "_id": "1",
                        "name":"MyCircle Name",
                        "description":"MyCircle Desc",
                        "startTime":"2020-03-15T12:00:00.000Z",
                        "endTime":"2020-03-15T14:00:00.000Z",
                        "disciplines":[],
                        "intentions":[],
                        "minimumSpiritContribution":14,
                        "language": "Swedish",
                        "virtualRegistered": 0
                        }""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }


    @Test
    fun editCircle_withVirtualRegisteredPractitionerCanAfford_200() {
        setup()
        val request = testController.testServer.post(
                "/circle/edit/b",
                """{
                        "_id": "1",
                        "name":"MyCircle Name",
                        "description":"MyCircle Desc",
                        "startTime":"2020-03-15T12:00:00.000Z",
                        "endTime":"2020-03-15T14:00:00.000Z",
                        "disciplines":[],
                        "intentions":[],
                        "minimumSpiritContribution":14,
                        "language": "Swedish",
                        "virtualRegistered": 2
                        }""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }


    @Test
    fun editCircle_withVirtualRegisteredPractitionerCannotAfford_400() {
        setup()
        val request = testController.testServer.post(
                "/circle/edit/b",
                """{
                        "_id": "1",
                        "name":"MyCircle Name",
                        "description":"MyCircle Desc",
                        "startTime":"2020-03-15T12:00:00.000Z",
                        "endTime":"2020-03-15T14:00:00.000Z",
                        "disciplines":[],
                        "intentions":[],
                        "minimumSpiritContribution":14,
                        "language": "Swedish",
                        "virtualRegistered": 200
                        }""",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    /***********
     * receipt *
     ***********/
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
    fun receipt_circleDescription_CDesc() {
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

    @Test
    fun removeCircleWhenPractitionerMissing_status_400() {
        setup()
        val request = testController.testServer.post("/circle/remove/q/c1", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun removeCircleWhenCircleMissing_status_400() {
        setup()
        val request = testController.testServer.post("/circle/remove/c/c9", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun removeCircleWhenPractitionerIsNotCreator_status_400() {
        setup()
        val request = testController.testServer.post("/circle/remove/b/c1", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(400)
    }

    @Test
    fun removeCircleSuccessfully_status_200() {
        setup()
        val request = testController.testServer.post("/circle/remove/c/c1", "", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun removeCircleSuccessfully_responseIs_true() {
        setup()
        val request = testController.testServer.post("/circle/remove/c/c1", "", false)
        val response = testController.testServer.execute(request)
        assertThat(String(response.body())).isEqualTo("""{"status":true}""")
    }
}