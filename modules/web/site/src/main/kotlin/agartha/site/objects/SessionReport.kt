package agartha.site.objects

/**
 * Purpose of this file is representing response for a report/feedback for a user session
 * with statistical information
 *
 * Created by Jorgen Andersson on 2018-04-25.
 */
data class SessionReport(val userSession : PractitionerReport, val companionSession: Companion)