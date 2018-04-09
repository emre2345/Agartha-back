package agartha.data.objects

import java.util.Date

/**
 * Purpose of this file is data object for a practitioner's session
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
data class SessionDBO(
        val practition: String,
        val active: Boolean = true,
        val startTime: Date = Date(),
        val endTime: Date? = null)
