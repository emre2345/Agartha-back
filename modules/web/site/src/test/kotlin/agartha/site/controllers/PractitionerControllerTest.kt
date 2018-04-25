package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.objects.PractitionerReport
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import java.time.LocalDateTime

/**
 * Purpose of this file is to test the PractitionerReport controller
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class PractitionerControllerTest {

    companion object {
        val mockedService = MockedPractitionerService()
        val testController = ControllerServer()


        @BeforeClass
        @JvmStatic
        fun setupClass() {
            PractitionerController(mockedService)
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
    fun practitionerController_testEmptyUserId_status200() {
        val getRequest = testController.testServer.get("/session/", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    /**
     *
     */
    @Test
    fun practitionerController_testEmptyUserId_userCreated() {
        val getRequest = testController.testServer.get("/session/", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PractitionerReport = jacksonObjectMapper().readValue(body, PractitionerReport::class.java)
        assertThat(data.userId?.length).isEqualTo(36)
    }

    /**
     *
     */
    @Test
    fun practitionerController_testUserId_userExists() {
        // Setup
        mockedService.insert(PractitionerDBO(mutableListOf(), LocalDateTime.now(), "abc"))
        //
        val getRequest = testController.testServer.get("/session/abc", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PractitionerReport = jacksonObjectMapper().readValue(body, PractitionerReport::class.java)
        assertThat(data.userId).isEqualTo("abc")
    }

    /**
     *
     */
    @Test
    fun practitionerController_insertSession_sessionIdIs1() {
        // Setup
        mockedService.insert(PractitionerDBO(mutableListOf(), LocalDateTime.now(), "abc"))
        //
        val postRequest = testController.testServer.post("/session/abc/MyPractice", "", false)
        val httpResponse = testController.testServer.execute(postRequest)
        val body = String(httpResponse.body())
        assertThat(body).isEqualTo("1")
    }

}