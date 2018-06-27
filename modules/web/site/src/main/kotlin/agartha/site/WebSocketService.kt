package agartha.site

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
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
     * Checks if the practitioners latest session is in a circle and if the practitioner is the creator of the circle
     * Adds a list of sessions to the practitionerSessions hashMap
     *
     * @param webSocketSession WebSocket session for a practitioner
     * @param practitionerId practitioners id
     * @return list of sessions
     */
    fun connect(webSocketSession: Session, practitionerId: String): SessionDBO {
        // Get the practitioner
        val practitioner: PractitionerDBO = mService.getById(practitionerId)!!
        // Get practitioners last session
        val practitionersLatestSession: SessionDBO = practitioner.sessions.last()
        val circle = practitionersLatestSession.circle

        // If practitioner is creator of circle
        // and the circle has virtualRegistered
        // and the creator can afford to pay...
        if (circle != null &&
                practitioner.creatorOfCircle(circle) &&
                circle.virtualRegistered > 0 &&
                mService.payForAddingVirtualSessions(practitioner, circle.virtualRegistered )) {
            // ...then update the hashMap with the webSocketSession and the new sessionList with both virtual registered and the practitioners session
            practitionersSessions[webSocketSession] = createListOfSessionsFromVirtual(circle.virtualRegistered, practitionersLatestSession)
        } else {
            // Put practitioners session and webSocket-session to a map
            practitionersSessions[webSocketSession] = mutableListOf(practitionersLatestSession)
        }
        return practitionersLatestSession
    }

    /**
     * Adds the practitioner latest session to a list as many times as there is virtual registered to this circle
     * so that all the virtual registered and practitioner's session will be in the list of sessions
     *
     * @param virtualRegistered number of virtual registered for this circle
     * @param practitionersLatestSession practitioner's latest session
     * @return list of sessions
     */
    private fun createListOfSessionsFromVirtual(virtualRegistered: Long, practitionersLatestSession: SessionDBO): MutableList<SessionDBO> {
        // Add the virtualRegistered and the practitioner to the webSocket
        val sessions = mutableListOf<SessionDBO>()
        for (i in 0..virtualRegistered) {
            sessions.add(practitionersLatestSession)
        }
        // Update the hashMap with the webSocketSession and the new sessionList
        return sessions
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