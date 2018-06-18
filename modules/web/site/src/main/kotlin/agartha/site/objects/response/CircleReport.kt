package agartha.site.objects.response

import agartha.data.objects.CircleDBO
import agartha.data.objects.SessionDBO

/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-06-18.
 */
data class CircleReport(
        // Circle name
        val name: String,
        // Circle description
        val description: String,
        //
        val numberOfPractitioners: Int) {

    constructor(circle: CircleDBO, sessions: List<SessionDBO>) : this(
            circle.name,
            circle.description,
            sessions.size)
}