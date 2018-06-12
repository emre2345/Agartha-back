package agartha.site.controllers

import agartha.data.objects.ImageDBO
import agartha.site.controllers.mocks.MockedImageService
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

/**
 * Purpose of this file is to test the Image Controller
 *
 * Created by Jorgen Andersson on 2018-06-12.
 */
class ImageControllerTest {
    companion object {
        val mockedService = MockedImageService()
        val testController = ControllerServer()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            ImageController(mockedService)
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
    fun getImage_Existing_status200() {
        mockedService.insert(ImageDBO(
                _id = "aaa",
                fileName = "test.jpg",
                image = "test".toByteArray()))
        //
        val request = testController.testServer.get("/image/aaa", false)
        val response = testController.testServer.execute(request)
        Assertions.assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun getImage_NonExisting_status404() {
        val request = testController.testServer.get("/image/aaa", false)
        val response = testController.testServer.execute(request)
        Assertions.assertThat(response.code()).isEqualTo(404)
    }

    @Test
    fun getImage_NoPath_status404() {
        val request = testController.testServer.get("/image/", false)
        val response = testController.testServer.execute(request)
        Assertions.assertThat(response.code()).isEqualTo(404)
    }

    @Test
    fun insertImage_() {
        val request = testController.testServer.post("/image/aaa", "",false)

    }
}