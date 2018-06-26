package agartha.site.controllers

import agartha.common.config.Settings.Companion.COMPANION_NUMBER_OF_MINUTES
import agartha.common.utils.DateTimeFormat
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.controllers.utils.ReqArgument
import agartha.site.controllers.utils.SessionUtil
import agartha.site.objects.response.CompanionReport
import spark.Request
import spark.Response
import spark.Spark
import java.time.LocalDateTime

/**
 * Purpose of this class is handling companion Practitioners
 *
 * Created by Jorgen Andersson on 2018-05-03.
 *
 * @param mService object for reading data from data source
 */
class CompanionController(private val mService: IPractitionerService) : AbstractController(mService) {

    /**
     * Class init
     */
    init {
        // API path for session
        Spark.path("/companion") {
            // Get companions for predefined time span
            Spark.get("", ::companionReport)
            // Get companions for practitioners last session
            Spark.before("/${ReqArgument.PRACTITIONER_ID.value}", ::validatePractitioner)
            Spark.get("/${ReqArgument.PRACTITIONER_ID.value}", ::companionSessionReport)
            // Get companions for ongoing
            Spark.before("/ongoing/${ReqArgument.PRACTITIONER_ID.value}", ::validatePractitioner)
            Spark.get("/ongoing/${ReqArgument.PRACTITIONER_ID.value}", ::companionOngoing)
        }
    }

    /**
     * Get all practitioners with sessions/practices during last 24 hours
     */
    @Suppress("UNUSED_PARAMETER")
    private fun companionReport(request: Request, response: Response): String {
        // Start date from when we should look for sessions
        val startDateTime: LocalDateTime = DateTimeFormat.localDateTimeUTC()
                .minusMinutes(COMPANION_NUMBER_OF_MINUTES)
        // End date from when we should look for sessions (now)
        val endDateTime: LocalDateTime = DateTimeFormat.localDateTimeUTC()
        // Generate report
        val companionReport = generateCompanionReport(startDateTime, endDateTime)
        // Return the report
        return ControllerUtil.objectToString(companionReport)
    }

    /**
     * Get report with all sessions active during current practitioner's latest session
     * Should include current practitioner
     */
    @Suppress("UNUSED_PARAMETER")
    private fun companionSessionReport(request: Request, response: Response): String {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        //
        val startDateTime: LocalDateTime = practitioner.sessions.last().startTime
        val endDateTime: LocalDateTime = practitioner.sessions.last().endTime ?: DateTimeFormat.localDateTimeUTC()
        // Generate report
        val companionReport = generateCompanionReport(startDateTime, endDateTime)
        // Return the report
        return ControllerUtil.objectToString(companionReport)
    }

    /**
     * Common code for companionReport and companionSessionReport
     * Filter out active sessions between these two dateTimes and generate a report from them
     * @param startDateTime
     * @param endDateTime
     * @return Report of active sessions between these dates
     */
    private fun generateCompanionReport(
            startDateTime: LocalDateTime, endDateTime: LocalDateTime): CompanionReport {
        // Get practitioners with sessions between
        val practitioners = mService
                .getAll()
                .filter { it.hasSessionBetween(startDateTime, endDateTime) }
        // Filter out overlapping sessions
        val sessions: List<SessionDBO> = SessionUtil
                .filterAllSessionsActiveBetween(practitioners, startDateTime, endDateTime)
        // Generate report
        return CompanionReport(practitioners.count(), sessions)
    }

    /**
     * Get practitioners with ongoing session/practice
     */
    @Suppress("UNUSED_PARAMETER")
    private fun companionOngoing(request: Request, response: Response): String {
        // Get current practitioner id
        val practitionerId: String = request.params(ReqArgument.PRACTITIONER_ID.value)
        // Get all companions with ongoing session
        val practitioners = mService
                .getAll()
                .filter { it.hasOngoingSession() }
        // Extract sessions from these companions
        val sessions = SessionUtil
                .filterSingleOngoingSession(practitioners, practitionerId)
        // Generate report
        val companionReport = CompanionReport(practitioners.count(), sessions)
        // Return the report
        return ControllerUtil.objectToString(companionReport)
    }
}