package agartha.site.controllers

import agartha.data.objects.SettingsDBO
import agartha.site.controllers.mocks.MockedSettingsService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

/**
 * Purpose of this file is to test the Setting controller
 *
 * Created by Jorgen Andersson on 2018-04-12.
 */
class SettingsControllerTest {

    companion object {
        val mockedService = MockedSettingsService()
        val testController = ControllerServer()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            SettingsController(mockedService)
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

        val item = jacksonObjectMapper().readValue(body, SettingsDBO::class.java)
        assertThat(item.intentions.size).isEqualTo(10)
    }

    /**
     *
     */
    @Test
    fun settingController_settings_defaultIntentions() {
        val getRequest = testController.testServer.get("/settings", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        val settings = jacksonObjectMapper().readValue(body, SettingsDBO::class.java)
        //
        val keys = settings
                .intentions
                .map {
                    it.title to it.description
                }
                .toMap()
                .keys
        //
        assertThat(keys).contains("Wellbeing")
        assertThat(keys).contains("Harmony")
        assertThat(keys).contains("Freedom")
        assertThat(keys).contains("Empowerment")
        assertThat(keys).contains("Resolution")
        assertThat(keys).contains("Empathy")
        assertThat(keys).contains("Abundance")
        assertThat(keys).contains("Love")
        assertThat(keys).contains("Celebration")
        assertThat(keys).contains("Transformation")
    }

    /**
     *
     */
    @Test
    fun settingController_settings_defaultDisciplinesCount() {
        val getRequest = testController.testServer.get("/settings", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())

        val item = jacksonObjectMapper().readValue(body, SettingsDBO::class.java)
        assertThat(item.disciplines.size).isEqualTo(10)
    }

    /**
     *
     */
    @Test
    fun settingController_settings_defaultDisciplines() {
        val getRequest = testController.testServer.get("/settings", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        val settings = jacksonObjectMapper().readValue(body, SettingsDBO::class.java)
        //
        val keys = settings
                .disciplines
                .map {
                    it.title to it.description
                }
                .toMap()
                .keys

        // Validate first level practices
        assertThat(keys).contains("Readings")
        assertThat(keys).contains("Meditation")
        assertThat(keys).contains("Wellness")
        assertThat(keys).contains("Movement")
        assertThat(keys).contains("Martial arts")
        assertThat(keys).contains("Physical exercise")
        assertThat(keys).contains("Creative expression")
        assertThat(keys).contains("Outdoor activity")
        assertThat(keys).contains("Personal growth")
        assertThat(keys).contains("Meals")
    }

    @Test
    fun settingsController_readFromDataSourceStatus_200() {
        // Add to storage
        mockedService.insert(
                SettingsDBO(
                        _id = "",
                        disciplines = listOf(),
                        intentions = listOf()))
        // Read a second time when stored to datasource
        val request = testController.testServer.get("/settings", false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun settingsController_addIntention_tada() {
        // Add to storage
        mockedService.insert(
                SettingsDBO(
                        _id = "",
                        disciplines = listOf(),
                        intentions = listOf()))

        val request = testController.testServer.post(
                "/settings/intention",
                "{\"title\":\"MyTitle\",\"description\":\"MyDesc\"}",
                false)
        val response = testController.testServer.execute(request)
        assertThat(response.code()).isEqualTo(200)
    }
}