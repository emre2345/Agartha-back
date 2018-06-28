package agartha.site.controllers.utils

import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import kotlin.math.roundToLong

/**
 * Purpose of this class ...
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-06-28.
 */
class EndSession(
        practitioner: PractitionerDBO,
        allPractitioners: List<PractitionerDBO>,
        val contributionPoints: Long,
        circleContributionPercent: Long) {

    val practitionerId = practitioner._id ?: ""
    val circlePoints: Long
    val creator: Boolean
    val circle: CircleDBO?

    init {
        val ongoingSession = practitioner.sessions.lastOrNull()
        circle = ongoingSession?.circle
        creator = practitioner.creatorOfCircle(circle)

        if (circle != null && creator) {
            val sessionsInCircle = allPractitioners
                    .filter { it.hasSessionInCircleAfterStartTime(circle.startTime, circle) }
                    .size

            circlePoints = ((sessionsInCircle * circle.minimumSpiritContribution * circleContributionPercent) / 100.0).roundToLong()
        } else {
            circlePoints = 0
        }

    }

}