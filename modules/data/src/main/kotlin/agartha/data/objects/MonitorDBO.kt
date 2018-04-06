package agartha.data.objects

import java.util.Date

/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-04-06.
 */
data class MonitorDBO(
        val value : String,
        val created: Date = Date(),
        val _id: String? = null)