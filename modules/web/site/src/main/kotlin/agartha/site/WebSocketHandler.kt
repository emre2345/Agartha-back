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
import java.util.concurrent.atomic.AtomicLong

class Message(val msgType: String, val data: Any)

@WebSocket
class WebSocketHandler {

    val practitioners = HashMap<Session, PractitionerDBO>()
    var uids = AtomicLong(0)

    @OnWebSocketConnect
    fun connected(session: Session) = println("session connected")

    @OnWebSocketMessage
    fun message(session: Session, message: String) {
        val json = ObjectMapper().readTree(message)
        // {type: "join/say", data: "name/msg"}
        when (json.get("type").asText()) {
            "join" -> {
                println("*** join ***")
                /*val user = User(uids.getAndIncrement(), json.get("data").asText())
                practitioners.put(session, user)
                // tell this user about all other users
                emit(session, Message("users", practitioners.values))
                // tell all other users, about this user
                broadcastToOthers(session, Message("join", user))*/
            }
            "startSession" -> {
                println("*** startSession ***")
                // Get the practitioner
                val practitioner: PractitionerDBO = PractitionerService().getById(json.get("practitionerId").asText())!!
                // Put practitioner and webSocket-session to a map
                practitioners.put(session, practitioner)
                // Broadcast to all users connected
                broadcast(Message("companionsCount", practitioners.size))
                //emit(session, Message("companionsCount", practitioners.size))
                // tell all other users, about this user
                //broadcastToOthers(session, Message("join", practitioner))
            }
            "say" -> {
                broadcast(Message("say", json.get("data").asText()))
            }
        }
        println("json msg ${message}")
    }


    @OnWebSocketClose
    fun disconnect(session: Session, code: Int, reason: String?) {
        // remove the user from our list
        val user = practitioners.remove(session)
        // notify all other users this user has disconnected
        if (user != null) broadcastToOthers(session, Message("companionsCount", practitioners.size))
    }


    fun emit(session: Session, message: Message) = session.remote.sendString(jacksonObjectMapper().writeValueAsString(message))
    fun broadcast(message: Message) = practitioners.forEach() { emit(it.key, message) }
    fun broadcastToOthers(session: Session, message: Message) = practitioners.filter { it.key != session }.forEach() { emit(it.key, message)}

}