package agartha.site.controllers.mocks

import org.eclipse.jetty.websocket.api.*
import java.net.InetSocketAddress

/**
 * Purpose of this class is to mock a WebSocketService
 * that is used in the tests for the WebSocketService
 * to test the map of the WebSocketServer
 *
 * Created by Rebecca Fransson on 2018-06-11
 */
class MockedWebSocketSession : Session {
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