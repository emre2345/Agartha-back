package agartha.site

import agartha.data.objects.PractitionerDBO
import agartha.data.services.PractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.request.PractitionerInvolvedInformation
import agartha.site.objects.webSocket.WebSocketEvents
import agartha.site.objects.webSocket.WebSocketMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket

/**
 * The purpose of this class is to handle all events for the webSocket.
 */
@WebSocket
class WebSocketHandler {
    private val practitioners = HashMap<Session, PractitionerDBO>()

    /**
     * When a practitioner has connected to the WebSocket
     */
    @OnWebSocketConnect
    fun connected(session: Session) = println("session connected")

    /**
     * When the webSocket receives a message this is where is it attended
     * Message-types:
     * 'startSession' - adds the practitioner to the map
     *                  and broadcasts the new practitioner to the others in the session
     *                  Emit the companions in the map to the new practitioner
     */
    @OnWebSocketMessage
    fun message(session: Session, message: String) {
        println("json msg ${message}")
        val webSocketMessage: WebSocketMessage =
                ControllerUtil.stringToObject(message, WebSocketMessage::class.java)

        // Do different things depending on the WebSocketMessage event
        when (webSocketMessage.event) {
            WebSocketEvents.START_SESSION.eventName -> {
                // Get the practitioner
                val practitioner: PractitionerDBO = PractitionerService().getById(webSocketMessage.data.toString())!!
                // Put practitioner and webSocket-session to a map
                practitioners.put(session, practitioner)
                // Broadcast to all users connected except this session
                broadcastToOthers(session, WebSocketMessage(WebSocketEvents.NEW_COMPANION.eventName, practitioner))
                // Send to self
                emit(session, WebSocketMessage(WebSocketEvents.COMPANIONS.eventName, practitioners.values))
            }
        }
    }


    /**
     * Closing the webSocket results in:
     * - removing practitioner from map
     * - Broadcast to everybody else that a companion left
     */
    @OnWebSocketClose
    fun disconnect(session: Session, code: Int, reason: String?) {
        println("closing")
        // Remove the practitioner from the list
        val practitioner: PractitionerDBO? = practitioners.remove(session)
        // Notify all other practitioners this practitioner has left the session
        if (practitioner != null) broadcastToOthers(session, WebSocketMessage(WebSocketEvents.COMPANION_LEFT.eventName, practitioners))
    }


    /**
     * Send a message to a specific webSocket-session
     * @param session - the practitioners webSocket-session
     * @param message - the message for the client
     */
    private fun emit(session: Session, message: WebSocketMessage) = session.remote.sendString(jacksonObjectMapper().writeValueAsString(message))

    /**
     * Broadcast a message to everybody except a specific session
     * @param session - the practitioners webSocket-session
     * @param message - the message for the client
     */
    private fun broadcastToOthers(session: Session, message: WebSocketMessage) = practitioners.filter { it.key != session }.forEach() { emit(it.key, message)}

}