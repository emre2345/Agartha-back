package agartha.site

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.site.controllers.mocks.MockedPractitionerService
import agartha.site.objects.webSocket.WebSocketMessage
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jetty.websocket.api.*
import org.junit.Test
import java.net.InetSocketAddress

class tome : Session {
    override fun getRemote(): RemoteEndpoint {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLocalAddress(): InetSocketAddress {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disconnect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProtocolVersion(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUpgradeResponse(): UpgradeResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setIdleTimeout(p0: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPolicy(): WebSocketPolicy {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUpgradeRequest(): UpgradeRequest {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun suspend(): SuspendToken {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isOpen(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getIdleTimeout(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close(p0: CloseStatus?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close(p0: Int, p1: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isSecure(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRemoteAddress(): InetSocketAddress {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class WebSocketServiceTest {
    @Test
    fun webSocketService_connect_newKeyAndValue() {
        val practitionerService = MockedPractitionerService()
        // Add a practitioner to the mocked db
        practitionerService.insert(PractitionerDBO(_id = "abc", sessions = listOf(SessionDBO(null, "", ""))))
        // create websocket service with the mocked practitioner service
        val webSocketService = WebSocketService(practitionerService)
        val session = webSocketService.connect(
                tome(),
                WebSocketMessage("", "abc"))
        assertThat(webSocketService.getPractitionersSessionsSize()).isEqualTo(1)
    }
}