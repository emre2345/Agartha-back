package agartha.site.controllers

import agartha.site.controllers.mocks.MockedSettingService
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

        assertThat(body).contains("WELLBEING")
        assertThat(body).contains("HARMONY")
        assertThat(body).contains("FREEDOM")
        assertThat(body).contains("EMPOWERMENT")
        assertThat(body).contains("RESOLUTION")
        assertThat(body).contains("EMPATHY")
        assertThat(body).contains("ABUNDANCE")
        assertThat(body).contains("LOVE")
        assertThat(body).contains("CELEBRATION")
        assertThat(body).contains("TRANSFORMATION")
    }
}