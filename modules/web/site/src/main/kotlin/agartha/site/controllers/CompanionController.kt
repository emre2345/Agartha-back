package agartha.site.controllers

import agartha.common.config.Settings.Companion.COMPANION_NUMBER_OF_HOURS
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.PractitionerUtil
import agartha.site.controllers.utils.SessionUtil
import agartha.site.objects.response.CompanionReport
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Request
import spark.Response
import spark.Spark
import java.time.LocalDateTime

/**
 * Purpose of this class is handling companion Practictioners
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-05-03.
 */
class CompanionController {
    // Practitioner data service
    private val mService: IPractitionerService
    // For mapping objects to string
    private val mMapper = jacksonObjectMapper()

    constructor(service: IPractitionerService) {
        mService = service
        // API path for session
        Spark.path("/companion") {
            // Get companions for predefined timespan
            Spark.get("", ::companionReport)
            // Get companions for last user session
            Spark.get("/:userid", ::companionSessionReport)
            // Get companions for ongoing
            Spark.get("/ongoing", ::companionOngoing)
            Spark.get("/ongoing/:userid", ::companionOngoing)

        }
    }

    /**
     * Get all practitioners with sessions/practices during last 24 hours
     */
    private fun companionReport(request: Request, response: Response): String {
        // Start date from when we should look for sessions
        val startDateTime: LocalDateTime = LocalDateTime.now()
                .minusHours(COMPANION_NUMBER_OF_HOURS)
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
        return mMapper.writeValueAsString(companionReport)
    }

    private fun companionSessionReport(request: Request, response: Response) : String {
        // Get current userid
        val userId : String = request.params(":userid")
        // Get user from data source
        val user : PractitionerDBO? = mService.getById(userId)
        //
        if (user != null) {
            val startDateTime: LocalDateTime = user.sessions.last().startTime
            val endDateTime: LocalDateTime = user.sessions.last().endTime ?: LocalDateTime.now()
            // Get practitioners with sessions between
            val practitioners = PractitionerUtil
                    .filterPractitionerWithSessionsBetween(
                            mService.getAll(), startDateTime, endDateTime)
            // Filter out last session for these practitioners
            val sessions : List<SessionDBO> = SessionUtil
                    .filterSingleSessionActiveBetween(
                            practitioners, userId, startDateTime, endDateTime)
            // Generate report
            val companionReport = CompanionReport(practitioners.count(), sessions)
            // Return the report
            return mMapper.writeValueAsString(companionReport)
        }
        // We should not end up here coz user should exist when this request is called
        // If not we are in test/dev mode
        return ""
    }

    /**
     * Get practitioners with ongoing session/practice
     */
    private fun companionOngoing(request: Request, response: Response): String {
        // Get current userid
        val userId : String = request.params(":userid") ?: ""

        // Created times for getting ongoing sessions
        val startDateTime: LocalDateTime = LocalDateTime.now().minusMinutes(15)
        val endDateTime: LocalDateTime = LocalDateTime.now()
        // Get practitioners with sessions between
        val practitioners = PractitionerUtil
                .filterPractitionerWithSessionsBetween(
                        mService.getAll(), startDateTime, endDateTime)
        // Filter out last session for these practitioners
        val sessions : List<SessionDBO> = SessionUtil
                .filterSingleSessionActiveBetween(
                        practitioners, userId, startDateTime, endDateTime)
        // Generate report
        val companionReport = CompanionReport(practitioners.count(), sessions)
        // Return the report
        return mMapper.writeValueAsString(companionReport)
    }
}