package agartha.site

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.data.services.PractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.webSocket.WebSocketEvents
import agartha.site.objects.webSocket.WebSocketMessage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket

/**
 * The purpose of this class is to handle all events for the webSocket.
 * Per default, a close connection is closed after 5 minutes per default
 * MaxIdleTime is in ms (3 hour * 60 minutes * 60 seconds * 1000 ms = 10 800 000)
 */
@WebSocket(maxIdleTime = 10800000)
class WebSocketHandler {
    private val service: WebSocketService = WebSocketService(PractitionerService())

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
    fun message(webSocketSession: Session, message: String) {
        val webSocketMessage: WebSocketMessage =
                ControllerUtil.stringToObject(message, WebSocketMessage::class.java)
        println("Received event: '${webSocketMessage.event}'")

        // Do different things depending on the WebSocketMessage event
        when (webSocketMessage.event) {
            // Start web socket session
            WebSocketEvents.START_SESSION.eventName -> {
                connect(webSocketSession, webSocketMessage)
            }
            // Reconnect web socket session, should be when Heroku re-starts and client connection is lost
            WebSocketEvents.RECONNECT_SESSION.eventName -> {
                connect(webSocketSession, webSocketMessage)
            }
        }
    }

    /**
     * Opening the webSocket results in:
     * - connect WebSocketSession
     * - Broadcast to everybody else that a new companion joined
     * - emit to self all the sessions in the WebSocket
     */
    fun connect(webSocketSession: Session, webSocketMessage: WebSocketMessage) {
        val practitionersLatestSession = service.connect(webSocketSession, webSocketMessage)
        debugPrintout(
                "starting '${practitionersLatestSession.discipline}' for '${practitionersLatestSession.intention}'")
        // The sessions remaining in the socket
        val returnSessions = ControllerUtil.objectToString(service.getPractitionersSessionMap().values.toList())
        // The disconnected practitioners session
        val returnPractitionersSession = ControllerUtil.objectToString(practitionersLatestSession)
        // Broadcast to all users connected except this session
        broadcastToOthers(webSocketSession, WebSocketMessage(WebSocketEvents.NEW_COMPANION.eventName, returnSessions, returnPractitionersSession))
        // Send to self
        emit(webSocketSession, WebSocketMessage(WebSocketEvents.COMPANIONS_SESSIONS.eventName, returnSessions))

    }

    /**
     * Closing the webSocket results in:
     * - disconnect WebSocketSession
     * - Broadcast to everybody else that a companion left
     */
    @OnWebSocketClose
    fun disconnect(webSocketSession: Session, code: Int, reason: String?) {
        // Remove the practitioner from the hashmap
        val practitionersSession = service.disconnect(webSocketSession)
        debugPrintout(
                "closing '${practitionersSession?.discipline}' for '${practitionersSession?.intention}' lasted for '${practitionersSession?.sessionDurationMinutes()}' minutes")
        // The sessions remaining in the socket
        val returnSessions = ControllerUtil.objectToString(service.getPractitionersSessionMap().values.toList())
        // The disconnected practitioners session
        val returnPractitionersSession = ControllerUtil.objectToString(practitionersSession)
        // Notify all other practitionersSessions this practitioner has left the webSocketSession
        if (practitionersSession != null) broadcastToOthers(webSocketSession, WebSocketMessage(WebSocketEvents.COMPANION_LEFT.eventName, returnSessions, returnPractitionersSession))
    }


    /**
     * Send a message to a specific webSocket-session
     * @param webSocketSession - the practitionersSessions webSocket-session
     * @param message - the message for the client
     */
    private fun emit(webSocketSession: Session, message: WebSocketMessage) = webSocketSession.remote.sendString(jacksonObjectMapper().writeValueAsString(message))

    /**
     * Broadcast a message to everybody except a specific session
     * @param webSocketSession - the practitionersSessions webSocket-session
     * @param message - the message for the client
     */
    private fun broadcastToOthers(webSocketSession: Session, message: WebSocketMessage) = service.getPractitionersSessionMap().filter { it.key != webSocketSession }.forEach { emit(it.key, message) }

    private fun debugPrintout(eventText: String) {
        println(eventText)
        println("Practitioners size: ${service.getPractitionersSessionsSize()}")
    }


}