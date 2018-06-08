package agartha.site

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.objects.webSocket.WebSocketMessage
import org.eclipse.jetty.websocket.api.Session

class WebSocketService(private val mService: IPractitionerService) {
    protected val practitionersSessions = HashMap<Session, SessionDBO>()

    /**
     * Add The webSocketSession and users latest session to the Map
     */
    fun connect(webSocketSession: Session, webSocketMessage: WebSocketMessage): SessionDBO {
        // Get the practitioner
        val practitioner: PractitionerDBO = mService.getById(webSocketMessage.data)!!
        // Get practitioners last session
        val practitionersLatestSession: SessionDBO = practitioner.sessions.last()
        // Put practitioners session and webSocket-session to a map
        practitionersSessions.put(webSocketSession, practitionersLatestSession)
        return practitionersLatestSession
    }

    /**
     * Remove the webSocketSession from the Map
     */
    fun disconnect(webSocketSession: Session): SessionDBO? {
        // Remove the practitioners session from the list
        return practitionersSessions.remove(webSocketSession)
    }

    /**
     * Return the Map
     */
    fun getPractitionersSessionMap(): HashMap<Session, SessionDBO> {
        return practitionersSessions
    }

    /**
     * Return the Maps size
     */
    fun getPractitionersSessionsSize(): Number {
        return practitionersSessions.values.size
    }
}