package agartha.site

import agartha.data.objects.PractitionerDBO
import agartha.data.services.PractitionerService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket

class Message(val msgType: String, val data: Any)

@WebSocket
class WebSocketHandler {

    val practitioners = HashMap<Session, PractitionerDBO>()

    @OnWebSocketConnect
    fun connected(session: Session) = println("session connected")

    @OnWebSocketMessage
    fun message(session: Session, message: String) {
        val json = ObjectMapper().readTree(message)
        // {type: "join/say", data: "name/msg"}
        when (json.get("type").asText()) {
            "startSession" -> {
                // Get the practitioner
                val practitioner: PractitionerDBO = PractitionerService().getById(json.get("practitionerId").asText())!!
                // Put practitioner and webSocket-session to a map
                practitioners.put(session, practitioner)
                // Broadcast to all users connected except this session
                broadcastToOthers(session, Message("newCompanion", practitioner))
                // Send to self
                emit(session, Message("companions", practitioners.values))
            }
        }
        println("json msg ${message}")
    }


    @OnWebSocketClose
    fun disconnect(session: Session, code: Int, reason: String?) {
        // remove the user from our list
        val user = practitioners.remove(session)
        // notify all other users this user has disconnected
        if (user != null) broadcastToOthers(session, Message("companions", practitioners))
    }


    fun emit(session: Session, message: Message) = session.remote.sendString(jacksonObjectMapper().writeValueAsString(message))
    fun broadcast(message: Message) = practitioners.forEach() { emit(it.key, message) }
    fun broadcastToOthers(session: Session, message: Message) = practitioners.filter { it.key != session }.forEach() { emit(it.key, message)}

}