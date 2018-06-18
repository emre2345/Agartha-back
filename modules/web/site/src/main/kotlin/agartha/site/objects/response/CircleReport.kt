package agartha.site.objects.response

import agartha.data.objects.CircleDBO
import agartha.data.objects.SessionDBO

/**
 * Purpose of this file is is response object for a circle report/receipt
 *
 * Created by Jorgen Andersson on 2018-06-18.
 */
data class CircleReport(
        // Circle name
        val name: String,
        // Circle description
        val description: String,
        // Number of practitioners/sessions in circle
        val numberOfPractitioners: Int,
        // Number of points generated during circle was active
        val generatedPoints: Long) {

    constructor(circle: CircleDBO, sessions: List<SessionDBO>, points: Long) : this(
            circle.name,
            circle.description,
            sessions.size,
            points)
}