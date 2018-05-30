package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.controllers.utils.ControllerUtil
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

    companion object {
        val mockedService = MockedPractitionerService()
        val testController = ControllerServer()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            AdminController(mockedService, null)
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
        mockedService.insert(PractitionerDBO())
        mockedService.insert(PractitionerDBO())
        mockedService.insert(PractitionerDBO())
    }

    @Test
    fun getPractitioners_responseStatus_200() {
        prepopulate()
        val request = testController.testServer.get("/admin/practitioners", false)
        val httpResponse = testController.testServer.execute(request)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    @Test
    fun getPractitioners_responseBody_size3() {
        prepopulate()
        val request = testController.testServer.get("/admin/practitioners", false)
        val httpResponse = testController.testServer.execute(request)
        val body = String(httpResponse.body())
        val dataList = ControllerUtil.stringToObjectList(body, PractitionerDBO::class.java)
        assertThat(dataList.size).isEqualTo(3)
    }

    @Test
    fun generatePractitionersGenerate10_responseStatus_200() {
        val request = testController.testServer.post(
                "/admin/generate/10", "", false)
        val httpResponse = testController.testServer.execute(request)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    @Test
    fun generatePractitionersGenerate10_responseBody_size10() {
        val request = testController.testServer.post(
                "/admin/generate/10", "", false)
        val httpResponse = testController.testServer.execute(request)
        val body = String(httpResponse.body())
        val dataList = ControllerUtil.stringToObjectList(body, PractitionerDBO::class.java)
        assertThat(dataList.size).isEqualTo(10)
    }

    @Test
    fun generatePractitionersGenerate10_storedCount_10() {
        val request = testController.testServer.post(
                "/admin/generate/10", "", false)
        testController.testServer.execute(request)
        assertThat(mockedService.getAll().size).isEqualTo(10)
    }
}