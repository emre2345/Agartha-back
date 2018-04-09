package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.objects.PracticeData
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class PractitionerControllerTest {

    private fun setupStuff(service: IPractitionerService): ControllerServer {
        val testService = ControllerServer()
        PractitionerController(service)
        spark.Spark.awaitInitialization()
        return testService
    }

    @Test
    fun practitionerController_testEmptyUserId_status200() {
        val testController = setupStuff(MockedPractitionerService())
        //
        val getRequest = testController.testServer.get("/session/", false)
        val httpResponse = testController.testServer.execute(getRequest)
        assertThat(httpResponse.code()).isEqualTo(200)
    }

    @Test
    fun practitionerController_testEmptyUserId_userCreated() {
        val testController = setupStuff(MockedPractitionerService())
        //
        val getRequest = testController.testServer.get("/session/", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PracticeData = jacksonObjectMapper().readValue(body, PracticeData::class.java)
        assertThat(data.userId.length).isEqualTo(40)
    }

    @Test
    fun practitionerController_testUserId_userExists() {
        val service = MockedPractitionerService()
        service.insert(PractitionerDBO(mutableListOf(), Date(), "abc"))
        val testController = setupStuff(service)
        //
        val getRequest = testController.testServer.get("/session/abc", false)
        val httpResponse = testController.testServer.execute(getRequest)
        val body = String(httpResponse.body())
        // Map to Data object
        val data: PracticeData = jacksonObjectMapper().readValue(body, PracticeData::class.java)
        assertThat(data.userId).isEqualTo("abc")
    }

    @Test
    fun practitionerController_insertSession_sessionIdIs1() {
        val service = MockedPractitionerService()
        service.practitionerList.add(PractitionerDBO(mutableListOf(), Date(), "abc"))
        val testController = setupStuff(service)
        //
        val postRequest = testController.testServer.post("/session/abc/MyPractice", "", false)
        val httpResponse = testController.testServer.execute(postRequest)
        val body = String(httpResponse.body())
        assertThat(body).isEqualTo("1")
    }
}