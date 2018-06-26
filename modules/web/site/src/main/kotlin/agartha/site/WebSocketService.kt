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
     * @return Practitioner's latest session
     */
    fun connectOriginal(webSocketSession: Session, webSocketMessage: WebSocketMessage): SessionDBO {
        // Get the practitioner
        val practitioner: PractitionerDBO = mService.getById(webSocketMessage.data)!!
        // Get practitioners last session
        val practitionersLatestSession: SessionDBO = practitioner.sessions.last()
        // Put practitioners session and webSocket-session to a map
        practitionersSessions[webSocketSession] = mutableListOf(practitionersLatestSession)
        return practitionersLatestSession
    }

    /**
     * Add a virtual practitioners session to a original practitioners webSocketSession in the map
     * @return Practitioner's latest session
     */
    fun connectVirtual(webSocketSession: Session, practitionerId: String, nrOfVirtualSessions: Int): SessionDBO {
        // Get the practitioner
        val practitioner: PractitionerDBO = mService.getById(practitionerId)!!
        // Get practitioners last session
        val practitionersLatestSession: SessionDBO = practitioner.sessions.last()
        // get circle of session
        val circle = practitionersLatestSession.circle
        // Find the practitionerSession from the webSocketSession
        val sessions = practitionersSessions[webSocketSession]
        // If there is a session for this practitioners webSocket
        // And if the session has a circle and if it is the practitioner's circle
        if (sessions != null &&
                circle != null &&
                practitioner.creatorOfCircle(circle) &&
                // Make sure that the practitioner can pay for the added sessions
                mService.payForAddingVirtualSessions(practitioner, nrOfVirtualSessions)) {
            // Then add the practitionersSession to the sessionList as many times that the practitioner requested
            for (i in 1..nrOfVirtualSessions) {
                sessions.add(practitionersLatestSession)
            }
            // Update the hasMap with the webSocketSession and the new sessionList
            practitionersSessions[webSocketSession] = sessions
        }
        return practitionersLatestSession
    }

    /**
     * Remove the webSocketSession from the Map
     * @return the WebSocketSession's value - the array with Sessions
     */
    fun disconnect(webSocketSession: Session): MutableList<SessionDBO>? {
        // Remove the practitioners session from the list
        return practitionersSessions.remove(webSocketSession)
    }

    /**
     * Return the Maps keys (webSocketSession)
     * @return List with the webSocketSessions in the WebSocket
     */
    fun getPractitionersWebSocketSessions(): List<Session> {
        return practitionersSessions.keys.toList()
    }

    /**
     * Gathers all the practitioners sessions into one list
     * @return List with all the Sessions in the WebSocket (both original and virtual)
     */
    fun getAllPractitionersSessions(): List<SessionDBO> {
        val sessions = mutableListOf<SessionDBO>()
        // Loop all the lists with sessions, add all the sessions in those lists into a mutableList
        practitionersSessions.values.forEach {
            sessions.addAll(it)
        }
        // Return all sessions in the lists
        return sessions
    }

    /**
     * @return the size of all the practitionersSessions in the Map
     */
    fun getPractitionersSessionsSize(): Int {
        return getAllPractitionersSessions().size
    }
}