package agartha.data.objects

import java.util.Date

/**
 * Purpose of this file is representing data object for a practicing person
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
data class PractitionerDBO(
        val sessions: List<SessionDBO>,
        val created: Date = Date(),
        val _id: String? = null)