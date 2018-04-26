package agartha.site.objects.response

/**
 * Purpose of this file is representing response for a report/feedback for a user session
 * with statistical information about
 * a. User/Practitioner's current/latest session
 * b. Other Users/Practitioners practicing at the same time
 *
 * Created by Jorgen Andersson on 2018-04-25.
 */
data class SessionReport(val practitionerReport : PractitionerReport, val companionReport: CompanionReport)