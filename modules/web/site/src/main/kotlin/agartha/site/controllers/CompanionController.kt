package agartha.site.controllers

import agartha.common.config.Settings.Companion.COMPANION_NUMBER_OF_MINUTES
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
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
class CompanionController(private val mService: IPractitionerService) : AbstractController() {

    /**
     * Class init
     */
    init {
        // API path for session
        Spark.path("/companion") {
            // Get companions for predefined timespan
            Spark.get("", ::companionReport)
            // Get companions for practitioners last session
            Spark.get("/:userid", ::companionSessionReport)
            // Get companions for ongoing
            Spark.get("/ongoing/:userid", ::companionOngoing)
        }
    }


    /**
     * Get all practitioners with sessions/practices during last 24 hours
     */
    @Suppress("UNUSED_PARAMETER")
    private fun companionReport(request: Request, response: Response): String {
        // Start date from when we should look for sessions
        val startDateTime: LocalDateTime = LocalDateTime.now()
                .minusMinutes(COMPANION_NUMBER_OF_MINUTES)
        // End date from when we should look for sessions (now)
        val endDateTime: LocalDateTime = LocalDateTime.now()
        //
        // Get list of practitioners with at least one session ongoing in this time span
        val practitioners = mService.getAll().filter {
            it.hasSessionBetween(startDateTime, endDateTime)
        }
        //
        // Filter out all sessions matching dates from these practitioners
        val sessions = SessionUtil
                .filterAllSessionsActiveBetween(
                        practitioners, startDateTime, endDateTime)
        // Generate report
        val companionReport = CompanionReport(practitioners.count(), sessions)
        // Return the report
        return ControllerUtil.objectToString(companionReport)
    }

    /**
     * Get report with all sessions active during current practitioner's latest session
     * Should include current practitioner
     */
    @Suppress("UNUSED_PARAMETER")
    private fun companionSessionReport(request: Request, response: Response): String {
        // Get practitioner ID from API path
        val practitionerId: String = request.params(":userid")
        // Make sure practitionerId exists in database
        val practitioner: PractitionerDBO = getPractitionerFromDatabase(practitionerId, mService)
        //
        val startDateTime: LocalDateTime = practitioner.sessions.last().startTime
        val endDateTime: LocalDateTime = practitioner.sessions.last().endTime ?: LocalDateTime.now()
        // Get practitioners with sessions between
        val practitioners = mService.getAll().filter {
            it.hasSessionBetween(startDateTime, endDateTime)
        }
        // Filter out overlapping sessions
        val sessions: List<SessionDBO> = SessionUtil.filterAllSessionsActiveBetween(
                practitioners, startDateTime, endDateTime)
        // Generate report
        val companionReport = CompanionReport(practitioners.count(), sessions)
        // Return the report
        return ControllerUtil.objectToString(companionReport)
    }

    /**
     * Get practitioners with ongoing session/practice
     */
    @Suppress("UNUSED_PARAMETER")
    private fun companionOngoing(request: Request, response: Response): String {
        // Get current practitionerid
        val practitionerId: String = request.params(":userid")
        // Make sure practitioner exists
        getPractitionerFromDatabase(practitionerId, mService)
        // Get all companions with ongoing session
        val practitioners = getOngoingCompanions()
        // Extract sessions from these companions
        val sessions = getOngoingCompanionsSessions(practitionerId, practitioners)
        // Generate report
        val companionReport = CompanionReport(practitioners.count(), sessions)
        // Return the report
        return ControllerUtil.objectToString(companionReport)
    }

    /**
     * Get all practitioners with ongoing session
     */
    private fun getOngoingCompanions(): List<PractitionerDBO> {
        // Get practitioners with sessions between
        return mService.getAll().filter {
            it.hasOngoingSession()
        }
    }

    /**
     * Get all ongoing sessions from a list of practitioners
     * The argument practitionerId is removed from sessions
     */
    private fun getOngoingCompanionsSessions(practitionerId: String, practitioners: List<PractitionerDBO>): List<SessionDBO> {
        // Filter out ongoing sessions
        return SessionUtil
                .filterSingleOngoingSession(practitioners, practitionerId)
    }
}