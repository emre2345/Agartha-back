package agartha.data.objects

import java.time.LocalDateTime

/**
 * Purpose of this file is representing a data object for monitoring database up for montioring tool
 *
 * Created by Jorgen Andersson on 2018-04-06.
 */
data class MonitorDBO(
        val value: String,
        val created: LocalDateTime = LocalDateTime.now(),
        val _id: String? = null)