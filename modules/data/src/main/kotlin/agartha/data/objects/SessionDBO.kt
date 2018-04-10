package agartha.data.objects

import java.util.Date

/**
 * Purpose of this file is data object for a practitioner's session
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
data class SessionDBO(
        // Session index for this user
        val index: Int,
        // Type of practitioning
        val practition: String,
        // Is this still active
        val active: Boolean = true,
        // Time when started
        val startTime: Date = Date(),
        // Time when ended
        val endTime: Date? = null)
