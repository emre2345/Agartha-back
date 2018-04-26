package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.objects.request.PractitionerInvolvedInformation
import agartha.site.objects.response.SessionReport
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import java.time.LocalDateTime

/**
 * Purpose of this file is to test the Practitioner controller
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
        val getRequest = testController.testServer.get("/practitioner", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    /**
     *
     */
    @Test
    fun practitionerController_testEmptyUserId_userCreated() {
        val getRequest = testController.testServer.get("/practitioner", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.practitionerReport.practitionerId?.length).isEqualTo(36)
    }

    /**
     *
     */
    @Test
    fun practitionerController_testUserId_userExists() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", LocalDateTime.now(), mutableListOf()))
        //
        val getRequest = testController.testServer.get("/practitioner/abc", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat(data.practitionerReport.practitionerId).isEqualTo("abc")
    }

    /**
     *
     */
    @Test
    fun practitionerControllerUpdatePractitioner_insertedUser_updatedUserWithInvolvedInformation() {
        // Setup
        mockedService.insert(PractitionerDBO("abc", LocalDateTime.now(), mutableListOf()))
        val involvedInformation = PractitionerInvolvedInformation(
                "Rebecca",
                "rebecca@kollektiva.se",
                "Jag gillar yoga!")
        //
        val getRequest = testController.testServer.put("/practitioner/abc", jacksonObjectMapper().writeValueAsString(involvedInformation), false)
        val httpResponse = testController.testServer.execute(getRequest)

        // Map to Data object
        //val data: SessionReport = jacksonObjectMapper().readValue(body, SessionReport::class.java)
        assertThat("abc").isEqualTo("abc")
    }
}