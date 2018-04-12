package agartha.site.controllers

import agartha.data.objects.SettingsDBO
import agartha.site.controllers.mocks.MockedSettingService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

/**
 * Purpose of this file is to test the Setting controller
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
class SettingControllerTest {

    companion object {
        val mockedService = MockedSettingService()
        val testController = ControllerServer()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            SettingController(mockedService)
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

    /**
     *
     */
    @Test
    fun settingController_settings_status200() {
        val getRequest = testController.testServer.get("/settings", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    /**
     *
     */
    @Test
    fun settingController_settings_defaultIntentionCount() {
        val getRequest = testController.testServer.get("/settings", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data : SettingsDBO = jacksonObjectMapper().readValue(body, SettingsDBO::class.java)
        assertThat(data.intentions.size).isEqualTo(9)
    }
}