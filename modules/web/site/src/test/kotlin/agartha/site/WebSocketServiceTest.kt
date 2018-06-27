package agartha.site

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.*
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.controllers.mocks.MockedWebSocketSession
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

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
            startTime = DateTimeFormat.localDateTimeUTC(),
            endTime = DateTimeFormat.localDateTimeUTC().plusHours(2),
            minimumSpiritContribution = 5,
            name = "CircleName",
            language = "Swedish",
            virtualRegistered = 5)
    // Expected Session for practitioner
    private val expectedSession = SessionDBO(null, "", "", circle = expectedCircle)
    // The mockedWebSocketSession only initialized once
    private val mockedWebSocketSession = MockedWebSocketSession()

    /**
     * Connect a user that does not own a circle that is in its latest session
     */
    private fun connectAUser(): SessionDBO {
        return webSocketService.connect(
                mockedWebSocketSession,
                "abc")
    }

    /**
     * Connect a user that own the circle that is in its latest session
     */
    private fun connectAUserWithCircle(): SessionDBO {
        return webSocketService.connect(
                mockedWebSocketSession,
                "dfg")
    }

    /**
     * Create a Virtual session with the practitioner that does not own the circle
     */
    private fun connectAVirtualUserWithoutCircle(): SessionDBO {
        return webSocketService.connect(
                mockedWebSocketSession,
                "abc")
    }

    /**
     * Create a Virtual session with the practitioner that is the creator of the circle
     */
    private fun connectAVirtualUserWithCircle(): SessionDBO {
        return webSocketService.connect(
                mockedWebSocketSession,
                "dfg")
    }

    /**
     * Create a Virtual session with the practitioner that is the creator of the circle
     */
    private fun connect5VirtualUserWithCircle(): SessionDBO {
        return webSocketService.connect(
                mockedWebSocketSession,
                "dfg")
    }


    @Before
    fun setupClass() {
        // Add a practitioner to the mocked db
        practitionerService.insert(PractitionerDBO(_id = "abc",
                sessions = listOf(expectedSession)))
        // Add a practitioner that owns a circle to the mocked db
        practitionerService.insert(PractitionerDBO(_id = "dfg",
                sessions = listOf(expectedSession),
                spiritBankLog = listOf(SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = 50)),
                circles = listOf(expectedCircle)))
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

    /***************************
     * Connect virtual session *
     ***************************/
    @Test
    fun webSocketService_connectVirtual_6ValuesArrayInKey() {
        connectAUserWithCircle()
        connectAVirtualUserWithCircle()
        assertThat(webSocketService.getPractitionersSessionsSize()).isEqualTo(6)
    }

    /**
     *
     */
    @Test
    fun webSocketService_connectVirtual5NewSessions_activeSessionsSize6() {
        connectAUserWithCircle()
        connect5VirtualUserWithCircle()
        assertThat(webSocketService.getPractitionersSessionsSize()).isEqualTo(6)
    }

    /**
     *
     */
    @Test
    fun webSocketService_connectVirtual_pointsLeft0() {
        connectAUserWithCircle()
        connectAVirtualUserWithCircle()
        val practitioner = practitionerService.getById("dfg")
        val pointsLeft = practitioner!!.calculateSpiritBankPointsFromLog()
        assertThat(pointsLeft).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun webSocketService_connectVirtual_newLogInSpiritBank() {
        connectAUserWithCircle()
        connectAVirtualUserWithCircle()
        val practitioner = practitionerService.getById("dfg")
        val log = practitioner!!.spiritBankLog.last()
        assertThat(log.type).isEqualTo(SpiritBankLogItemType.ADD_VIRTUAL_TO_CIRCLE)
    }

    /**
     *
     */
    @Test
    fun webSocketService_connectVirtualSessionReturned_sessionThatUserHas() {
        connectAUserWithCircle()
        val session = connectAVirtualUserWithCircle()
        assertThat(session).isEqualTo(expectedSession)
    }

    /**
     *
     */
    @Test
    fun webSocketService_connectVirtualWithoutOwningCircle_noNewSessionInValueArrayInKey() {
        connectAUser()
        connectAVirtualUserWithoutCircle()
        assertThat(webSocketService.getPractitionersSessionsSize()).isEqualTo(1)
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
        assertThat(session).isEqualTo(listOf(expectedSession))
    }

    /*****************************
     * Disconnect with a virtual *
     *****************************/
    @Test
    fun webSocketService_disconnectWithAFake_nothingInMap() {
        // First connect user
        connectAUser()
        connectAVirtualUserWithCircle()
        // Then test the disconnect
        webSocketService.disconnect(
                mockedWebSocketSession)
        assertThat(webSocketService.getPractitionersSessionsSize()).isEqualTo(0)
    }

    /**
     *
     */
    @Test
    fun webSocketService_disconnectWithAVirtualSessionReturned_6() {
        // First connect user
        connectAUser()
        connectAVirtualUserWithCircle()
        // Then test the disconnect
        val session = webSocketService.disconnect(
                mockedWebSocketSession)
        assertThat(session!!.size).isEqualTo(6)
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