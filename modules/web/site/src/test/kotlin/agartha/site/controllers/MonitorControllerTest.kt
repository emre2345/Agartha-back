package agartha.site.controllers

import agartha.data.objects.MonitorDBO
import agartha.data.services.IBaseService
import agartha.site.controllers.mocks.MockedMonitorService
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * Test for MonitorController, API for monitoring
 */
class MonitorControllerTest {

    private fun setupStuff(service: IBaseService<MonitorDBO>): ControllerServer {
        val testController = ControllerServer()
        MonitorController(service)
        spark.Spark.awaitInitialization()
        return testController
    }


    @Test
    fun monitorController_monitorStatus_StillAlive() {
        val testController = setupStuff(MockedMonitorService())
        //
        val getRequest = testController.testServer.get("/monitoring/status", false)
        val httpResponse = testController.testServer.execute(getRequest)
        Assertions.assertThat(httpResponse.code()).isEqualTo(200)
        val body = String(httpResponse.body())
        Assertions.assertThat(body).isEqualTo("Still alive")
    }


    @Test
    fun monitorController_monitorDbWrite_true() {
        val testController = setupStuff(MockedMonitorService())
        //
        val getRequest = testController.testServer.get("/monitoring/db/write", false)
        val httpResponse = testController.testServer.execute(getRequest)
        Assertions.assertThat(httpResponse.code()).isEqualTo(200)
        val body = String(httpResponse.body())
        Assertions.assertThat(body).isEqualTo("true")
    }

    @Test
    fun monitorController_monitorDbRead_true() {
        val testController = setupStuff(MockedMonitorService())
        //
        testController.testServer.execute(testController.testServer.get("/monitoring/db/write", false))
        //
        val getRequest = testController.testServer.get("/monitoring/db/read", false)
        val httpResponse = testController.testServer.execute(getRequest)
        Assertions.assertThat(httpResponse.code()).isEqualTo(200)
        val body = String(httpResponse.body())
        Assertions.assertThat(body).isEqualTo("true")
    }
}