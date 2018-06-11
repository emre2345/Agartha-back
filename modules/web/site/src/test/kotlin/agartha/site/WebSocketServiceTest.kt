package agartha.site

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.controllers.mocks.MockedWebSocketSession
import agartha.site.objects.webSocket.WebSocketMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Purpose of this class is to test the hashMap in the WebSocketService
 *
 * Created by Rebecca Fransson on 2018-06-11
 */
class WebSocketServiceTest {

    val practitionerService = MockedPractitionerService()
    // create webSocketService with the mocked practitioner service
    val webSocketService = WebSocketService(practitionerService)
    // Expected Session for user
    val expectedSession = SessionDBO(null, "", "")
    // The mockedWebSocketSession only initilized once
    val mockedWebSocketSession = MockedWebSocketSession()
    // WebSocket connect message
    val connectMessage = WebSocketMessage("", "abc")

    @Before
    fun setupClass() {
        // Add a practitioner to the mocked db
        practitionerService.insert(PractitionerDBO(_id = "abc", sessions = listOf(expectedSession)))
    }

    /***********
     * Connect *
     ***********/
    @Test
    fun webSocketService_connect_newKeyAndValue() {
        webSocketService.connect(
                mockedWebSocketSession,
                connectMessage)
        assertThat(webSocketService.getPractitionersSessionsSize()).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun webSocketService_connectSessionReturned_sessionThatUserHas() {
        val session = webSocketService.connect(
                mockedWebSocketSession,
                connectMessage)
        assertThat(session).isEqualTo(expectedSession)
    }

    /**************
     * Disconnect *
     **************/
    @Test
    fun webSocketService_disconnect_nothingInMap() {
        // First connect user
        webSocketService.connect(
                mockedWebSocketSession,
                connectMessage)
        // Then test the disconnect
        webSocketService.disconnect(
                mockedWebSocketSession)
        assertThat(webSocketService.getPractitionersSessionsSize()).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun webSocketService_disconnectSessionReturned_sessionThatUserHas() {
        // First connect user
        webSocketService.connect(
                mockedWebSocketSession,
                connectMessage)
        // Then test the disconnect
        val session = webSocketService.disconnect(
                mockedWebSocketSession)
        assertThat(session).isEqualTo(expectedSession)
    }

    /***************************
     * PractitionersSessionMap *
     ***************************/
    @Test
    fun webSocketService_getPractitionersSessionMapNoUserConnected_nothingInMap() {
        // Get the map
        val map = webSocketService.getPractitionersSessionMap()
        assertThat(map.size).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun webSocketService_getPractitionersSessionMapOneUserConnected_size1() {
        // Connect user
        webSocketService.connect(
                mockedWebSocketSession,
                connectMessage)
        // Get the map
        val map = webSocketService.getPractitionersSessionMap()
        assertThat(map.size).isEqualTo(1)
    }

    /********************************
     * getPractitionersSessionsSize *
     ********************************/
    @Test
    fun webSocketService_getPractitionersSessionsSizeNoUserConnected_nothingInMap() {
        // Get the map
        val size = webSocketService.getPractitionersSessionsSize()
        assertThat(size).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun webSocketService_getPractitionersSessionsSizeOneUserConnected_size1() {
        // Connect user
        webSocketService.connect(
                mockedWebSocketSession,
                connectMessage)
        // Get the map
        val size = webSocketService.getPractitionersSessionsSize()
        assertThat(size).isEqualTo(1)
    }
}