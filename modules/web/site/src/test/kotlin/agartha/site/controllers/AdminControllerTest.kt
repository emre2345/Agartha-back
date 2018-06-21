package agartha.site.controllers

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import io.schinzel.basicutils.configvar.IConfigVar
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

/**
 * Purpose of this file is to test AdminController
 *
 * Created by Jorgen Andersson on 2018-05-30.
 */
class AdminControllerTest {

    class ConfigDummy : IConfigVar {
        override fun getValue(p0: String?): String {
            return "Santa"
        }

    }

    private val passPhrase = "Santa"

    companion object {
        val mockedService = MockedPractitionerService()
        val testController = ControllerServer()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            AdminController(mockedService, ConfigDummy(), null)
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

    private fun prepopulate() {
        mockedService.insert(PractitionerDBO(_id = "aaa"))
        mockedService.insert(PractitionerDBO(_id = "bbb"))
        mockedService.insert(PractitionerDBO(_id = "ccc"))
        mockedService.insert(PractitionerDBO(_id = "ccc", description = "Generated Practitioner"))
    }

    @Test
    fun getPractitioners_responseStatus_200() {
        prepopulate()
        val request = testController.testServer.post("/admin/practitioners", passPhrase, false)
        val httpResponse = testController.testServer.execute(request)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    @Test
    fun getPractitioners_responseBody_size4() {
        prepopulate()
        val request = testController.testServer.post("/admin/practitioners", passPhrase,false)
        val httpResponse = testController.testServer.execute(request)
        val body = String(httpResponse.body())
        val dataList = ControllerUtil.stringToObjectList(body, PractitionerDBO::class.java)
        assertThat(dataList.size).isEqualTo(4)
    }

    @Test
    fun authenticateValidPassPhrase_responseCode_200() {
        val request = testController.testServer.post(
                "/admin/auth", passPhrase, false)
        val httpResponse = testController.testServer.execute(request)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    @Test
    fun authenticateInvalidPassPhrase_responseCode_401() {
        val request = testController.testServer.post(
                "/admin/auth", "INVALID PASS PHRASE", false)
        val httpResponse = testController.testServer.execute(request)
        assertThat(httpResponse.code()).isEqualTo(401)
    }

    /**
     * Add
     */
    @Test
    fun addSessionExistingUser_responseStatus_200() {
        prepopulate()
        val request = testController.testServer.post(
                "/admin/session/add/bbb/Meditation/Love", passPhrase, false)
        val httpResponse = testController.testServer.execute(request)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    @Test
    fun addSessionExistingUser_responseBodyDiscipline_Meditation() {
        prepopulate()
        val request = testController.testServer.post(
                "/admin/session/add/bbb/Meditation/Love", passPhrase, false)
        val httpResponse = testController.testServer.execute(request)
        val body = String(httpResponse.body())
        val data = ControllerUtil.stringToObject(body, SessionDBO::class.java)
        assertThat(data.discipline).isEqualTo("Meditation")
    }

    @Test
    fun addSessionExistingUser_responseBodyIntention_Love() {
        prepopulate()
        val request = testController.testServer.post(
                "/admin/session/add/bbb/Meditation/Love", passPhrase, false)
        val httpResponse = testController.testServer.execute(request)
        val body = String(httpResponse.body())
        val data = ControllerUtil.stringToObject(body, SessionDBO::class.java)
        assertThat(data.intention).isEqualTo("Love")
    }

    @Test
    fun addSessionExistingUser_storedSessionCount_1() {
        prepopulate()
        val request = testController.testServer.post(
                "/admin/session/add/bbb/Meditation/Love", passPhrase, false)
         testController.testServer.execute(request)
        val p = mockedService.getById("bbb")
        assertThat(mockedService.getById("bbb")!!.sessions.size).isEqualTo(1)
    }

    @Test
    fun addSessionNonExistingUser_responseStatus_400() {
        prepopulate()
        val request = testController.testServer.post(
                "/admin/session/add/sss/Meditation/Love", passPhrase, false)
        val httpResponse = testController.testServer.execute(request)
        assertThat(httpResponse.code()).isEqualTo(400)
    }

    @Test
    fun addSessionNonExistingUser_body_missing() {
        prepopulate()
        val request = testController.testServer.post(
                "/admin/session/add/sss/Meditation/Love", passPhrase, false)
        val httpResponse = testController.testServer.execute(request)
        val body = String(httpResponse.body())
        assertThat(body).isEqualTo("Practitioner id sss does not exist in database")
    }

    /**
     * Generate
     */
    @Test
    fun generatePractitionersGenerate10_responseStatus_200() {
        val request = testController.testServer.post(
                "/admin/generate/10", passPhrase, false)
        val httpResponse = testController.testServer.execute(request)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    @Test
    fun generatePractitionersGenerate10_responseBody_size10() {
        val request = testController.testServer.post(
                "/admin/generate/10", passPhrase, false)
        val httpResponse = testController.testServer.execute(request)
        val body = String(httpResponse.body())
        val dataList = ControllerUtil.stringToObjectList(body, PractitionerDBO::class.java)
        assertThat(dataList.size).isEqualTo(10)
    }

    @Test
    fun generatePractitionersGenerate10_storedCount_10() {
        val request = testController.testServer.post(
                "/admin/generate/10", passPhrase,false)
        testController.testServer.execute(request)
        assertThat(mockedService.getAll().size).isEqualTo(10)
    }

    /**
     * Remove all
     */
    @Test
    fun removeAllPractitioners_responseStatus_200() {
        val request = testController.testServer.post(
                "/admin/remove/all", passPhrase,false)
        val httpResponse = testController.testServer.execute(request)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    @Test
    fun removeAllPractitioners_responseBody_true() {
        prepopulate()
        val request = testController.testServer.post(
                "/admin/remove/all", passPhrase,false)
        val httpResponse = testController.testServer.execute(request)
        val body = String(httpResponse.body())
        assertThat(body).isEqualTo("true")
    }

    @Test
    fun removeAllPractitioners_storedCount_0() {
        prepopulate()
        val request = testController.testServer.post(
                "/admin/remove/all", passPhrase,false)
        testController.testServer.execute(request)
        assertThat(mockedService.getAll().size).isEqualTo(0)
    }

    /**
     * Remove generated
     */
    @Test
    fun removeGeneratedPractitioners_responseStatus_200() {
        val request = testController.testServer.post(
                "/admin/remove/generated", passPhrase,false)
        val httpResponse = testController.testServer.execute(request)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    @Test
    fun removeGeneratedPractitioners_responseBody_listWithSize3() {
        prepopulate()
        val request = testController.testServer.post(
                "/admin/remove/generated", passPhrase,false)
        val httpResponse = testController.testServer.execute(request)
        val body = String(httpResponse.body())
        val list = ControllerUtil.stringToObjectList(body, PractitionerDBO::class.java)
        assertThat(list.size).isEqualTo(3)
    }

    @Test
    fun removeGeneratedPractitioners_storedCount_3() {
        prepopulate()
        val request = testController.testServer.post(
                "/admin/remove/generated", passPhrase, false)
        testController.testServer.execute(request)
        assertThat(mockedService.getAll().size).isEqualTo(3)
    }

    /**
     *
     */
    @Test
    fun removeById_response_true() {
        // Setup
        mockedService.insert(
                PractitionerDBO("abc", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                        SessionDBO(null, "D", "I", DateTimeFormat.localDateTimeUTC()))))

        val postRequest = testController.testServer.post("/admin/remove/practitioner/abc", passPhrase,false)
        val httpResponse = testController.testServer.execute(postRequest)
        val responseBody = String(httpResponse.body())
        assertThat(responseBody).isEqualTo("true")
    }

    @Test
    fun removeCircle_responseStatus_200() {
        mockedService.insert(
                PractitionerDBO(
                        _id = "p1",
                        created = DateTimeFormat.localDateTimeUTC(),
                        circles = listOf(CircleDBO(_id="c1", name = "", description = "",
                                        startTime = DateTimeFormat.localDateTimeUTC(), endTime = DateTimeFormat.localDateTimeUTC(),
                                        intentions = listOf(), disciplines = listOf(), minimumSpiritContribution = 2))))

        val request = testController.testServer.post("/admin/remove/circle/c1", passPhrase, false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun removeCircle_responseStatusEmpty_404() {
        mockedService.insert(
                PractitionerDBO(
                        _id = "p1",
                        created = DateTimeFormat.localDateTimeUTC(),
                        circles = listOf(CircleDBO(_id="c1", name = "", description = "",
                                startTime = DateTimeFormat.localDateTimeUTC(), endTime = DateTimeFormat.localDateTimeUTC(),
                                intentions = listOf(), disciplines = listOf(), minimumSpiritContribution = 2))))

        val request = testController.testServer.post("/admin/remove/circle/", passPhrase, false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(404)
    }

    @Test
    fun removeCircle_removeExistingBody_true() {
        mockedService.insert(
                PractitionerDBO(
                        _id = "p1",
                        created = DateTimeFormat.localDateTimeUTC(),
                        circles = listOf(CircleDBO(_id="c1", name = "", description = "",
                                startTime = DateTimeFormat.localDateTimeUTC(), endTime = DateTimeFormat.localDateTimeUTC(),
                                intentions = listOf(), disciplines = listOf(), minimumSpiritContribution = 2))))

        val request = testController.testServer.post("/admin/remove/circle/c1", passPhrase, false)
        val response = testController.testServer.execute(request)
        assertThat(String(response.body())).isEqualTo("true")
    }

    @Test
    fun removeCircle_removeNonExistingBody_false() {
        mockedService.insert(
                PractitionerDBO(
                        _id = "p1",
                        created = DateTimeFormat.localDateTimeUTC(),
                        circles = listOf(CircleDBO(_id="c1", name = "", description = "",
                                startTime = DateTimeFormat.localDateTimeUTC(), endTime = DateTimeFormat.localDateTimeUTC(),
                                intentions = listOf(), disciplines = listOf(), minimumSpiritContribution = 2))))

        val request = testController.testServer.post("/admin/remove/circle/c2", passPhrase, false)
        val response = testController.testServer.execute(request)
        assertThat(String(response.body())).isEqualTo("false")
    }
}