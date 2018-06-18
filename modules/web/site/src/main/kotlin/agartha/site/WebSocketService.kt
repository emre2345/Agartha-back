package agartha.site

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.objects.webSocket.WebSocketMessage
import org.eclipse.jetty.websocket.api.Session

/**
 * Purpose of this class is to handle a hashMap that is used to
 * keep track of which practitioners that is connected to the WebSocketServer
 *
 * Created by Rebecca Fransson on 2018-06-11
 */
class WebSocketService(private val mService: IPractitionerService) {
    /**
     * PractitionersSessions map contains
     * Session                  - a webSocket session, unique session for a user
     * MutableList<SessionDBO>  - a list with sessions that is connected to the webSocketSession for a user
     * The mutable list exist because a practitioner can have one original session and many virtual session connected to a webSocketSession
     */
    private val practitionersSessions = HashMap<Session, MutableList<SessionDBO>>()

    /**
     * Add a original practitioners latest session to the webSocket map
     */
    fun connectOriginal(webSocketSession: Session, webSocketMessage: WebSocketMessage): SessionDBO {
        // Get the practitioner
        val practitioner: PractitionerDBO = mService.getById(webSocketMessage.data)!!
        // Get practitioners last session
        val practitionersLatestSession: SessionDBO = practitioner.sessions.last()
        // Put practitioners session and webSocket-session to a map
        practitionersSessions.put(webSocketSession, mutableListOf(practitionersLatestSession))
        return practitionersLatestSession
    }

    /**
     * Add a virtual practitioners session to a original practitioners webSocketSession in the map
     */
    fun connectVirtual(webSocketSession: Session, webSocketMessage: WebSocketMessage): SessionDBO {
        // Get the practitioner
        val practitioner: PractitionerDBO = mService.getById(webSocketMessage.data)!!
        // Get practitioners last session
        val practitionersLatestSession: SessionDBO = practitioner.sessions.last()
        // Find the practitionerSession from the webSocketSession
        val sessions = practitionersSessions.get(webSocketSession)
        // If there is a session for this practitioners webSocket
        if (sessions != null) {
            // Then make a mutable sessionList and add the practitionersSession to the sessionList
            sessions.add(practitionersLatestSession)
            // Update the hasMap with the webSocketSession and the new sessionList
            practitionersSessions.put(webSocketSession, sessions)
        }
        return practitionersLatestSession
    }

    /**
     * Remove the webSocketSession from the Map
     */
    fun disconnect(webSocketSession: Session): SessionDBO? {
        // Remove the practitioners session from the list
        val removedSession = practitionersSessions.remove(webSocketSession)
        if (removedSession != null) {
            // return the first element in the list(The session that is the original)
            return removedSession[0]
        }
        return null
    }

    /**
     * Return the Maps keys (webSocketSession)
     */
    fun getPractitionersWebSocketSessions(): List<Session> {
        return practitionersSessions.keys.toList()
    }

    /**
     * Return a list with all the sessions in the map (even the ones that is generated by other practitioners)
     */
    fun getAllPractitionersSessions(): List<SessionDBO> {
        val sessions = mutableListOf<SessionDBO>()
        // Loop all the lists with sessions, add all the sessions in those lists into a mutableList
        practitionersSessions.values.forEach {
            sessions.addAll(it)
        }
        // Return all the lists
        return sessions
    }

    /**
     * Return the size of all the practitionersSessions in the Map
     */
    fun getPractitionersSessionsSize(): Number {
        return getAllPractitionersSessions().size
    }
}