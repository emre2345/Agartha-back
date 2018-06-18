package agartha.site

import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.controllers.mocks.MockedWebSocketSession
import agartha.site.objects.webSocket.WebSocketMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * Purpose of this class is to test the hashMap in the WebSocketService
 *
 * Created by Rebecca Fransson on 2018-06-11
 */
class WebSocketServiceTest {

    private val practitionerService = MockedPractitionerService()
    // create webSocketService with the mocked practitioner service
    private val webSocketService = WebSocketService(practitionerService)
    // Expected circle
    private val expectedCircle = CircleDBO(intentions = listOf(),
            disciplines = listOf(),
            description = "",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now().plusHours(2),
            minimumSpiritContribution = 5,
            name = "CircleName")
    // Expected Session for practitioner
    private val expectedSession = SessionDBO(null, "", "", circle = expectedCircle)
    // The mockedWebSocketSession only initialized once
    private val mockedWebSocketSession = MockedWebSocketSession()
    // WebSocket connect message
    private val connectMessage = WebSocketMessage("", "abc")

    private fun connectAUser(): SessionDBO{
        return webSocketService.connectOriginal(
                mockedWebSocketSession,
                connectMessage)
    }
    private fun connectAVirtualUser(): SessionDBO{
        return webSocketService.connectVirtual(
                mockedWebSocketSession,
                connectMessage)
    }


    @Before
    fun setupClass() {
        // Add a practitioner to the mocked db
        practitionerService.insert(PractitionerDBO(_id = "abc", sessions = listOf(expectedSession)))
        // Add a practitioner that owns a circle to the mocked db
        practitionerService.insert(PractitionerDBO(_id = "deg", sessions = listOf(expectedSession), circles = listOf(expectedCircle)))
    }

    /***********
     * Connect *
     ***********/
    @Test
    fun webSocketService_connectOriginal_newKeyAndValue() {
        connectAUser()
        assertThat(webSocketService.getPractitionersSessionsSize()).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun webSocketService_connectOriginalSessionReturned_sessionThatUserHas() {
        val session = connectAUser()
        assertThat(session).isEqualTo(expectedSession)
    }

    /****************
     * Connect fake *
     ****************/
    @Test
    fun webSocketService_connectFake_newValueArrayInKey() {
        connectAUser()
        connectAVirtualUser()
        assertThat(webSocketService.getPractitionersSessionsSize()).isEqualTo(2)
    }

    /**
     *
     */
    @Test
    fun webSocketService_connectFakeSessionReturned_sessionThatUserHas() {
        connectAUser()
        val session = connectAVirtualUser()
        assertThat(session).isEqualTo(expectedSession)
    }

    /**************
     * Disconnect *
     **************/
    @Test
    fun webSocketService_disconnect_nothingInMap() {
        // First connect user
        connectAUser()
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
        connectAUser()
        // Then test the disconnect
        val session = webSocketService.disconnect(
                mockedWebSocketSession)
        assertThat(session).isEqualTo(expectedSession)
    }

    /**************************
     * Disconnect with a fake *
     **************************/
    @Test
    fun webSocketService_disconnectWithAFake_nothingInMap() {
        // First connect user
        connectAUser()
        connectAVirtualUser()
        // Then test the disconnect
        webSocketService.disconnect(
                mockedWebSocketSession)
        assertThat(webSocketService.getPractitionersSessionsSize()).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun webSocketService_disconnectWithAFakeSessionReturned_sessionThatUserHas() {
        // First connect user
        connectAUser()
        connectAVirtualUser()
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
        val map = webSocketService.getPractitionersWebSocketSessions()
        assertThat(map.size).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun webSocketService_getPractitionersSessionMapOneUserConnected_size1() {
        // Connect user
        connectAUser()
        // Get the map
        val map = webSocketService.getPractitionersWebSocketSessions()
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
        connectAUser()
        // Get the map
        val size = webSocketService.getPractitionersSessionsSize()
        assertThat(size).isEqualTo(1)
    }
}