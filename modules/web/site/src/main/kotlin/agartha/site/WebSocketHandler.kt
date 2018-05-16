package agartha.site

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.data.services.PractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.controllers.utils.PractitionerUtil
import agartha.site.controllers.utils.SessionUtil
import agartha.site.objects.webSocket.WebSocketEvents
import agartha.site.objects.webSocket.WebSocketMessage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import java.time.LocalDateTime

/**
 * The purpose of this class is to handle all events for the webSocket.
 */
@WebSocket
class WebSocketHandler {
    private val practitionersSessions = HashMap<Session, SessionDBO>()
    private val mService: IPractitionerService = PractitionerService();

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
        println("json msg ${message}")
        val webSocketMessage: WebSocketMessage =
                ControllerUtil.stringToObject(message, WebSocketMessage::class.java)

        // Do different things depending on the WebSocketMessage event
        when (webSocketMessage.event) {
            WebSocketEvents.START_SESSION.eventName -> {

                val userId = webSocketMessage.data.toString()
                // Get sessions for the ongoing companions
                val sessions = getOngoingCompanionsSessions(userId, getOngoingCompanions())

                // Get the practitioner
                val practitioner: PractitionerDBO = mService.getById(webSocketMessage.data.toString())!!
                val practitionersLatestSession: SessionDBO = practitioner.sessions.last()
                // Put practitioner and webSocket-session to a map
                practitionersSessions.put(webSocketSession, practitionersLatestSession)
                println(practitionersSessions.values.size)

                // Broadcast to all users connected except this session
                broadcastToOthers(webSocketSession, WebSocketMessage(WebSocketEvents.NEW_COMPANION.eventName, practitionersSessions.values))
                // Send to self
                emit(webSocketSession, WebSocketMessage(WebSocketEvents.COMPANIONS_SESSIONS.eventName, practitionersSessions.values))
            }
        }
    }


    /**
     * Closing the webSocket results in:
     * - removing practitioner from map
     * - Broadcast to everybody else that a companion left
     */
    @OnWebSocketClose
    fun disconnect(webSocketSession: Session, code: Int, reason: String?) {
        println("closing")
        // Remove the practitioners session from the list
        val practitionersSession: SessionDBO? = practitionersSessions.remove(webSocketSession)
        println(practitionersSessions.values.size)
        // Notify all other practitionersSessions this practitioner has left the webSocketSession
        if (practitionersSession != null) broadcastToOthers(webSocketSession, WebSocketMessage(WebSocketEvents.COMPANION_LEFT.eventName, practitionersSessions.values))
    }


    /**
     * Send a message to a specific webSocket-session
     * @param session - the practitionersSessions webSocket-session
     * @param message - the message for the client
     */
    private fun emit(webSocketSession: Session, message: WebSocketMessage) = webSocketSession.remote.sendString(jacksonObjectMapper().writeValueAsString(message))

    /**
     * Broadcast a message to everybody except a specific session
     * @param session - the practitionersSessions webSocket-session
     * @param message - the message for the client
     */
    private fun broadcastToOthers(webSocketSession: Session, message: WebSocketMessage) = practitionersSessions.filter { it.key != webSocketSession }.forEach { emit(it.key, message)}


    /**
     * Get the ongoing session for the practitioner
     * TODO: Same function as in PractitionerController and needs to be moved to a better place
     */
    private fun getOngoingCompanionsSessions(userId: String, practitioners: List<PractitionerDBO>): List<SessionDBO> {
        // Created times for getting ongoing sessions
        val startDateTime: LocalDateTime = LocalDateTime.now().minusMinutes(15)
        val endDateTime: LocalDateTime = LocalDateTime.now()
        // Filter out last session for these practitionersSessions
        return SessionUtil
                .filterSingleSessionActiveBetween(
                        practitioners, userId, startDateTime, endDateTime)
    }
    /**
     * Get the ongoing companion for the practitioner
     * TODO: Same function as in PractitionerController and needs to be moved to a better place
     */
    private fun getOngoingCompanions(): List<PractitionerDBO> {
        // Created times for getting ongoing sessions
        val startDateTime: LocalDateTime = LocalDateTime.now().minusMinutes(15)
        val endDateTime: LocalDateTime = LocalDateTime.now()
        // Get practitionersSessions with sessions between
        return PractitionerUtil
                .filterPractitionerWithSessionsBetween(
                        mService.getAll(), startDateTime, endDateTime)
    }
}