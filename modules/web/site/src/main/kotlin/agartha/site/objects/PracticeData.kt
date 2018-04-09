package agartha.site.objects

import agartha.data.services.PractitionerService
import java.util.*

/**
 * Purpose of this file is represent data to be returned to user with statistical information
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
data class PracticeData(val userId: String, val currentUserCount: Int)