package agartha.data.objects

import java.time.LocalDateTime

/**
 * Purpose of this file is representing data object for a practicing person
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
data class PractitionerDBO(
        val _id: String? = null,
        val created: LocalDateTime = LocalDateTime.now(),
        val sessions: List<SessionDBO> = listOf(),
        val fullName: String? = null,
        val email: String? = null,
        val description: String? = null
        )