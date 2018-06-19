package agartha.site

import agartha.data.objects.SessionDBO
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
    fun connected(webSocketSession: Session) = println("session connected")

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
                // Connect a original practitioner to the webSocket
                connectOriginal(webSocketSession, webSocketMessage)
            }
            // Reconnect web socket session, should be when Heroku re-starts and client connection is lost
            WebSocketEvents.RECONNECT_SESSION.eventName -> {
                // Connect a original practitioner to the webSocket
                connectOriginal(webSocketSession, webSocketMessage)
            }
            // Reconnect web socket session, should be when Heroku re-starts and client connection is lost
            WebSocketEvents.START_VIRTUAL_SESSION.eventName -> {
                // Connect a virtual practitioner to a original practitioners webSocketSession
                connectVirtual(webSocketSession, webSocketMessage)
            }
        }
    }

    /**
     * Opening the webSocket results in:
     * - connect WebSocketSession
     * - Broadcast to everybody else that a new companion joined
     * - emit to self all the sessions in the WebSocket
     */
    private fun connect(webSocketSession: Session, practitionersLatestSession: SessionDBO) {
        debugPrintout(
                "starting '${practitionersLatestSession.discipline}' for '${practitionersLatestSession.intention}'")
        // The sessions remaining in the socket
        val returnSessions = ControllerUtil.objectToString(service.getAllPractitionersSessions())
        // The disconnected practitioners session
        val returnPractitionersSession = ControllerUtil.objectToString(practitionersLatestSession)
        // Broadcast to all practitioners connected except this session
        broadcastToOthers(webSocketSession,
                WebSocketMessage(
                        event = WebSocketEvents.NEW_COMPANION.eventName,
                        data = returnSessions,
                        practitionersSession = returnPractitionersSession))
        // Send to self
        emit(webSocketSession,
                WebSocketMessage(
                event = WebSocketEvents.COMPANIONS_SESSIONS.eventName,
                data = returnSessions))
    }

    /**
     * Connects a single practitioner to the webSocket
     */
    private fun connectOriginal(webSocketSession: Session, webSocketMessage: WebSocketMessage) {
        val practitionersLatestSession = service.connectOriginal(webSocketSession, webSocketMessage)
        connect(webSocketSession, practitionersLatestSession)
    }

    /**
     * Connects fake practitioner to a practitioner that is an original and already in the webSocket
     */
    private fun connectVirtual(webSocketSession: Session, webSocketMessage: WebSocketMessage) {
        val practitionersLatestSession = service.connectVirtual(webSocketSession, webSocketMessage.data, webSocketMessage.nrOfVirtualSessions)
        connect(webSocketSession, practitionersLatestSession)
    }

    /**
     * Closing the webSocket results in:
     * - disconnect WebSocketSession
     * - Broadcast to everybody else that a companion left
     *   OR
     * - Broadcast to everybody else that a companion with its virutal sessions left
     */
    @OnWebSocketClose
    fun disconnect(webSocketSession: Session, code: Int, reason: String?) {
        // Remove the practitioner from the hashMap
        val practitionersSessions = service.disconnect(webSocketSession)
        // If the disconnect was successful
        if(practitionersSessions != null){
            debugPrintout(
                    "closing '${practitionersSessions[0].discipline}' for '${practitionersSessions[0].intention}' lasted for '${practitionersSession?.sessionDurationMinutes()}' minutes")
            // The sessions remaining in the socket
            val returnSessions = ControllerUtil.objectToString(service.getAllPractitionersSessions())
            // The disconnected practitioners session, the original will be the first in the list
            val returnPractitionersSession = ControllerUtil.objectToString(practitionersSessions[0])
            // If there is more then one session for this practitioners webSocketSession
            // Then the practitioner has virtual sessions
            if (practitionersSessions.size > 1) {
                // Notify all other practitionersSessions this practitioner and some virtualSessions has left the webSocketSession
                broadcastToOthers(webSocketSession,
                        WebSocketMessage(
                                event = WebSocketEvents.COMPANION_LEFT_WITH_VIRTUAL_SESSIONS.eventName,
                                data = returnSessions,
                                practitionersSession = returnPractitionersSession,
                                nrOfVirtualSessions = practitionersSessions.size))
            }else {
                // Notify all other practitionersSessions this practitioner has left the webSocketSession
                broadcastToOthers(webSocketSession,
                        WebSocketMessage(
                                event =WebSocketEvents.COMPANION_LEFT.eventName,
                                data = returnSessions,
                                practitionersSession = returnPractitionersSession))
            }
        }
        // If the disconnect was'nt successful
        // TODO: Return a webSocket message with event Error
    }


    /**
     * Send a message to a specific webSocket-session
     * @param webSocketSession - the practitionersSessions webSocket-session
     * @param message - the message for the client
     */
    private fun emit(webSocketSession: Session, message: WebSocketMessage){
        webSocketSession.remote.sendString(jacksonObjectMapper().writeValueAsString(message))
    }

    /**
     * Broadcast a message to everybody except a specific session
     * @param webSocketSession - the practitionersSessions webSocket-session
     * @param message - the message for the client
     */
    private fun broadcastToOthers(webSocketSession: Session, message: WebSocketMessage){
            service.getPractitionersWebSocketSessions()
                    .filter { it != webSocketSession }
                    .forEach { emit(it, message) }
    }

    private fun debugPrintout(eventText: String) {
        println(eventText)
        println("Practitioners size: ${service.getPractitionersSessionsSize()}")
    }


}