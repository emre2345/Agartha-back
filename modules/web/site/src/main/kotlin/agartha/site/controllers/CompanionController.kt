package agartha.site.controllers

import agartha.common.config.Settings.Companion.COMPANION_NUMBER_OF_MINUTES
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.controllers.utils.PractitionerUtil
import agartha.site.controllers.utils.SessionUtil
import agartha.site.objects.response.CompanionReport
import agartha.site.objects.response.CompanionsSessionReport
import spark.Request
import spark.Response
import spark.Spark
import java.time.LocalDateTime

/**
 * Purpose of this class is handling companion Practitioners
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-05-03.
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
            Spark.get("/ongoing", ::companionOngoing)
            Spark.get("/ongoing/:userid", ::companionOngoing)
            // Get matched companions
            Spark.get("/matched/:userid", ::matchOngoingCompanionsSessions)
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
        // Get practitioners with sessions between
        val practitioners = PractitionerUtil
                .filterPractitionerWithSessionsBetween(
                        mService.getAll(), startDateTime, endDateTime)
        // Filter out all sessions matching dates from these practitioners
        val sessions = SessionUtil
                .filterAllSessionsActiveBetween(
                        practitioners, startDateTime, endDateTime)
        // Generate report
        val companionReport = CompanionReport(practitioners.count(), sessions)
        // Return the report
        return ControllerUtil.objectToString(companionReport)
    }

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
            val practitioners = PractitionerUtil
                    .filterPractitionerWithSessionsBetween(
                            mService.getAll(), startDateTime, endDateTime)
            // Filter out last session for these practitioners
            val sessions: List<SessionDBO> = SessionUtil
                    .filterSingleSessionActiveBetween(
                            practitioners, userId, startDateTime, endDateTime)
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
     * Counts all the ongoing sessions and matching them with an user
     */
    @Suppress("UNUSED_PARAMETER")
    private fun matchOngoingCompanionsSessions(request: Request, response: Response): String {
        val userId: String = request.params(":userid") ?: ""
        // Get sessions for the ongoing companions
        val sessionReportList = getOngoingCompanionsSessions(userId, getOngoingCompanions()).map {
            CompanionsSessionReport(it)
        }
        //
        return ControllerUtil.objectListToString(sessionReportList)
    }

    private fun getOngoingCompanions(): List<PractitionerDBO> {
        // Created times for getting ongoing sessions
        val startDateTime: LocalDateTime = LocalDateTime.now().minusMinutes(15)
        val endDateTime: LocalDateTime = LocalDateTime.now()
        // Get practitioners with sessions between
        return PractitionerUtil
                .filterPractitionerWithSessionsBetween(
                        mService.getAll(), startDateTime, endDateTime)
    }

    private fun getOngoingCompanionsSessions(userId: String, practitioners: List<PractitionerDBO>): List<SessionDBO> {
        // Created times for getting ongoing sessions
        val startDateTime: LocalDateTime = LocalDateTime.now().minusMinutes(15)
        val endDateTime: LocalDateTime = LocalDateTime.now()
        // Filter out last session for these practitioners
        return SessionUtil
                .filterSingleSessionActiveBetween(
                        practitioners, userId, startDateTime, endDateTime)
    }
}