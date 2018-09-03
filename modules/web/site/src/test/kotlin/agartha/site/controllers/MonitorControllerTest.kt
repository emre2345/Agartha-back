package agartha.site.controllers

import agartha.site.controllers.mocks.MockedMonitorService
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

/**
 * Test for MonitorController, API for monitoring
 */
class MonitorControllerTest {


    companion object {
        val mockedService = MockedMonitorService()
        val testController = ControllerServer()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            MonitorController(mockedService)
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


    @Test
    fun monitorController_monitorStatus_StillAlive() {
        //
        val getRequest = testController.testServer.get("/monitoring/status", false)
        val httpResponse = testController.testServer.execute(getRequest)
        Assertions.assertThat(httpResponse.code()).isEqualTo(200)
        val body = String(httpResponse.body())
        Assertions.assertThat(body).isEqualTo("""{"status":"Still alive"}""")
    }


    @Test
    fun monitorController_monitorDbWrite_true() {
        //
        val request = testController.testServer.post("/monitoring/db/write", "", false)
        val httpResponse = testController.testServer.execute(request)
        Assertions.assertThat(httpResponse.code()).isEqualTo(200)
        val body = String(httpResponse.body())
        Assertions.assertThat(body).isEqualTo("""{"status":"true"}""")
    }

    @Test
    fun monitorController_monitorDbRead_true() {
        //
        testController.testServer.execute(testController.testServer.post("/monitoring/db/write", "", false))
        //
        val getRequest = testController.testServer.get("/monitoring/db/read", false)
        val httpResponse = testController.testServer.execute(getRequest)
        Assertions.assertThat(httpResponse.code()).isEqualTo(200)
        val body = String(httpResponse.body())
        Assertions.assertThat(body).isEqualTo("""{"status":"true"}""")
    }
}