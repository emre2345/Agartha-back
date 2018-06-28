package agartha.site.controllers.utils

import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import kotlin.math.roundToLong

/**
 * Purpose of this class calculate points to be given to the current practitioner
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-06-28.
 */
class EndSession(
        // current practitioner
        practitioner: PractitionerDBO,
        // list of all practitioners
        allPractitioners: List<PractitionerDBO>,
        // points warded by frontend (api)
        val contributionPoints: Long,
        // percentage of circle point to be awarded to circle owner
        circleContributionPercent: Long) {

    val practitionerId = practitioner._id ?: ""
    // Points for circle owner
    val circlePoints: Long
    // is practitioner creator of circle
    val creator: Boolean
    // circle in question
    val circle: CircleDBO?

    init {
        // get the last session for practitioner
        val ongoingSession = practitioner.sessions.lastOrNull()
        // circle for ongoing session
        circle = ongoingSession?.circle
        // is practitioner creator of circle
        creator = practitioner.creatorOfCircle(circle)
        // calculate points for practition creator on session end
        if (circle != null && creator) {
            // count sessions for this circle
            val sessionsInCircle = allPractitioners
                    .filter { it.hasSessionInCircleAfterStartTime(circle.startTime, circle) }
                    .size
            circlePoints = ((sessionsInCircle * circle.minimumSpiritContribution * circleContributionPercent) / 100.0).roundToLong()
        } else {
            circlePoints = 0
        }

    }

}