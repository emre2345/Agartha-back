package agartha.site.controllers

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.response.CompanionReport
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

/**
 * Created by Jorgen Andersson on 2018-05-03.
 */
class CompanionControllerTest {
    companion object {
        val mockedService = MockedPractitionerService()
        val testController = ControllerServer()

        /**
         *
         */
        @BeforeClass
        @JvmStatic
        fun setupClass() {
            CompanionController(mockedService)
            spark.Spark.awaitInitialization()
        }
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
                SessionDBO(null, "Meditation", "Harmony",
                        DateTimeFormat.localDateTimeUTC().minusMinutes(35),
                        DateTimeFormat.localDateTimeUTC().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("f", DateTimeFormat.localDateTimeUTC(), mutableListOf(
                SessionDBO(null, "Meditation", "Celebration",
                        DateTimeFormat.localDateTimeUTC().minusDays(5),
                        DateTimeFormat.localDateTimeUTC().minusDays(5)))))
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
    fun companionReport_statusForNoUserId_200() {
        setupReport()
        val getRequest = testController.testServer.get("/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    /**
     *
     */
    @Test
    fun companionSessionReport_statusForExistingUser_200() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/a", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    /**
     *
     */
    @Test
    fun companionSessionReport_statusForEmptyUserId_404() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(404)
    }

    /**
     *
     */
    @Test
    fun companionSessionReport_statusForNonExistingUser_400() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/s", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(400)
    }

    /**
     *
     */
    @Test
    fun companionOngoingReport_statusForExistingUserId_200() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/ongoing/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    /**
     *
     */
    @Test
    fun companionOngoingReport_statusForEmptyUserId_404() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/ongoing/", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(404)
    }

    /**
     *
     */
    @Test
    fun companionOngoingReport_statusForNonExistingUserId_400() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/ongoing/s", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(400)
    }

    /**
     *
     */
    @Test
    fun companionReport_practitionerCount_5() {
        setupReport()
        val getRequest = testController.testServer.get("/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil.stringToObject(body, CompanionReport::class.java)
        assertThat(data.companionCount).isEqualTo(5)
    }

    /**
     *
     */
    @Test
    fun companionReport_sessionCount_5() {
        setupReport()
        val getRequest = testController.testServer.get("/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil.stringToObject(body, CompanionReport::class.java)
        assertThat(data.sessionCount).isEqualTo(5)
    }

    /**
     *
     */
    @Test
    fun companionReport_sessionMinutes_135() {
        setupReport()
        val getRequest = testController.testServer.get("/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil.stringToObject(body, CompanionReport::class.java)
        assertThat(data.sessionSumMinutes).isEqualTo(135)
    }

    /**
     *
     */
    @Test
    fun companionReport_intentionsHas_Harmony() {
        setupReport()
        val getRequest = testController.testServer.get("/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil.stringToObject(body, CompanionReport::class.java)
        assertThat(data.intentions.containsKey("Harmony")).isTrue()
    }

    /**
     *
     */
    @Test
    fun companionSessionReport_practitionerCount_4() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil.stringToObject(body, CompanionReport::class.java)
        assertThat(data.companionCount).isEqualTo(4)
    }

    /**
     *
     */
    @Test
    fun companionSessionReport_sessionCount_4() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil.stringToObject(body, CompanionReport::class.java)
        assertThat(data.sessionCount).isEqualTo(4)
    }

    /**
     *
     */
    @Test
    fun companionSessionReport_sessionMinutes_115() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil.stringToObject(body, CompanionReport::class.java)
        assertThat(data.sessionSumMinutes).isEqualTo(115)
    }

    /**
     *
     */
    @Test
    fun companionSessionReport_intentionsHas_Love() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil.stringToObject(body, CompanionReport::class.java)
        assertThat(data.intentions.containsKey("Love")).isTrue()
    }

    /**
     *
     */
    @Test
    fun companionSessionReport_intentionsHas_Freedom() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil.stringToObject(body, CompanionReport::class.java)
        assertThat(data.intentions.containsKey("Freedom")).isTrue()
    }

    /**
     * Current users practice should not be included
     */
    @Test
    fun companionSessionReport_intentionsHasNot_Transformation() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil.stringToObject(body, CompanionReport::class.java)
        assertThat(data.intentions.containsKey("Transformation")).isFalse()
    }

    /**
     * Older than current session should not be counted
     */
    @Test
    fun companionSessionReport_intentionsHasNot_Empowerment() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil.stringToObject(body, CompanionReport::class.java)
        assertThat(data.intentions.containsKey("Empowerment")).isFalse()
    }

}