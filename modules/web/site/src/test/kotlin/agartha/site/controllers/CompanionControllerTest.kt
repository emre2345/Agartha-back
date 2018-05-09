package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.response.CompanionReport
import agartha.site.objects.response.CompanionsSessionReport
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import java.time.LocalDateTime

/**
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-05-03.
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
        mockedService.insert(PractitionerDBO("a", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, null, "Yoga", "Transformation",
                        LocalDateTime.now().minusDays(13),
                        LocalDateTime.now().minusDays(13)),
                SessionDBO(1, null, "Yoga", "Empowerment",
                        LocalDateTime.now().minusDays(11),
                        LocalDateTime.now().minusDays(11)),
                SessionDBO(2, null, "Meditation", "Harmony",
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().minusDays(5)),
                SessionDBO(3, null, "Yoga", "Freedom",
                        LocalDateTime.now().minusMinutes(41),
                        LocalDateTime.now().minusMinutes(1)))))
        //
        mockedService.insert(PractitionerDBO("b", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, null, "Meditation", "Love",
                        LocalDateTime.now().minusMinutes(20),
                        LocalDateTime.now().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("c", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, null, "Yoga", "Love",
                        LocalDateTime.now().minusDays(13),
                        LocalDateTime.now().minusDays(13)),
                SessionDBO(1, null, "Yoga", "Freedom",
                        LocalDateTime.now().minusDays(11),
                        LocalDateTime.now().minusDays(11)),
                SessionDBO(2, null, "Yoga", "Love",
                        LocalDateTime.now().minusDays(3).minusMinutes(45),
                        LocalDateTime.now().minusDays(3)),
                SessionDBO(3, null, "Meditation", "Harmony",
                        LocalDateTime.now().minusMinutes(20).minusSeconds(10),
                        LocalDateTime.now()))))
        //
        mockedService.insert(PractitionerDBO("d", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, null, "Meditation", "Empathy",
                        LocalDateTime.now().minusMinutes(35),
                        LocalDateTime.now().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("e", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, null, "Meditation", "Harmony",
                        LocalDateTime.now().minusMinutes(35),
                        LocalDateTime.now().minusMinutes(5)))))
        //
        mockedService.insert(PractitionerDBO("f", LocalDateTime.now(), mutableListOf(
                SessionDBO(0, null, "Meditation", "Celebration",
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().minusDays(5)))))
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
    fun companionReport_status_200() {
        val getRequest = testController.testServer.get("/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    /**
     *
     */
    @Test
    fun companionSessionReport_status_200() {
        val getRequest = testController.testServer.get("/companion/abc", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    /**
     *
     */
    @Test
    fun companionOngoingReport_status_200() {
        val getRequest = testController.testServer.get("/companion/ongoing/abc", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(200)
    }


    /**
     *
     */
    @Test
    fun companionReport_practitionerCount_6() {
        setupReport()
        val getRequest = testController.testServer.get("/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil<CompanionReport>().stringToObject(body, CompanionReport::class.java)
        assertThat(data.practitionerCount).isEqualTo(6)
    }

    /**
     *
     */
    @Test
    fun companionReport_sessionCount_8() {
        setupReport()
        val getRequest = testController.testServer.get("/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil<CompanionReport>().stringToObject(body, CompanionReport::class.java)
        assertThat(data.sessionCount).isEqualTo(8)
    }

    /**
     *
     */
    @Test
    fun companionReport_sessionMinutes_180() {
        setupReport()
        val getRequest = testController.testServer.get("/companion", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: CompanionReport = ControllerUtil<CompanionReport>().stringToObject(body, CompanionReport::class.java)
        assertThat(data.sessionMinutes).isEqualTo(180)
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
        val data: CompanionReport = ControllerUtil<CompanionReport>().stringToObject(body, CompanionReport::class.java)
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
        val data: CompanionReport = ControllerUtil<CompanionReport>().stringToObject(body, CompanionReport::class.java)
        assertThat(data.practitionerCount).isEqualTo(4)
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
        val data: CompanionReport = ControllerUtil<CompanionReport>().stringToObject(body, CompanionReport::class.java)
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
        val data: CompanionReport = ControllerUtil<CompanionReport>().stringToObject(body, CompanionReport::class.java)
        assertThat(data.sessionMinutes).isEqualTo(115)
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
        val data: CompanionReport = ControllerUtil<CompanionReport>().stringToObject(body, CompanionReport::class.java)
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
        val data: CompanionReport = ControllerUtil<CompanionReport>().stringToObject(body, CompanionReport::class.java)
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
        val data: CompanionReport = ControllerUtil<CompanionReport>().stringToObject(body, CompanionReport::class.java)
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
        val data: CompanionReport = ControllerUtil<CompanionReport>().stringToObject(body, CompanionReport::class.java)
        assertThat(data.intentions.containsKey("Empowerment")).isFalse()
    }

    /**
     *
     */
    @Test
    fun matchSessions_firstSessionPoints_2() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/matched/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map data to object
        val list: List<CompanionsSessionReport> = ControllerUtil<CompanionsSessionReport>()
                .stringToObjectList(body, CompanionsSessionReport::class.java)
        Assertions.assertThat(list[0].matchPoints).isEqualTo(2)
    }

    /**
     *
     */
    @Test
    fun matchSessions_SecondSessionPoints_1() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/matched/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map data to object
        val list: List<CompanionsSessionReport> = ControllerUtil<CompanionsSessionReport>()
                .stringToObjectList(body, CompanionsSessionReport::class.java)
        Assertions.assertThat(list[1].matchPoints).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun matchSessions_LastSessionPoints_0() {
        setupReport()
        val getRequest = testController.testServer.get("/companion/matched/c", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map data to object
        val list: List<CompanionsSessionReport> = ControllerUtil<CompanionsSessionReport>()
                .stringToObjectList(body, CompanionsSessionReport::class.java)
        Assertions.assertThat(list.last().matchPoints).isEqualTo(0)
    }
}