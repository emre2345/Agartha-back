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
class CompanionController(private val mService: IPractitionerService) {

    /**
     * Class init
     */
    init {
        // API path for session
        Spark.path("/companion") {
            // Get companions for predefined timespan
            Spark.get("", ::companionReport)
            // Get companions for last user session
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
     * Get report with all sessions active during current user's latest session
     * Should include current user
     */
    @Suppress("UNUSED_PARAMETER")
    private fun companionSessionReport(request: Request, response: Response): String {
        // Get current userid
        val userId: String = request.params(":userid")
        // Get user from data source
        val user: PractitionerDBO? = mService.getById(userId)
        //
        if (user != null) {
            val startDateTime: LocalDateTime = user.sessions.last().startTime
            val endDateTime: LocalDateTime = user.sessions.last().endTime ?: LocalDateTime.now()
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
        // We should not end up here coz user should exist when this request is called
        // If not we are in test/dev mode
        return ""
    }

    /**
     * Get practitioners with ongoing session/practice
     */
    @Suppress("UNUSED_PARAMETER")
    private fun companionOngoing(request: Request, response: Response): String {
        // Get current userid
        val userId: String = request.params(":userid") ?: ""
        val practitioners = getOngoingCompanions()
        val sessions = getOngoingCompanionsSessions(userId, practitioners)
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
     * The argument userId is removed from sessions
     */
    private fun getOngoingCompanionsSessions(userId: String, practitioners: List<PractitionerDBO>): List<SessionDBO> {
        // Filter out ongoing sessions
        return SessionUtil
                .filterSingleOngoingSession(practitioners, userId)
    }
}