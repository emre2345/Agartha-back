package agartha.site.controllers

import agartha.common.config.Settings
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.ISessionService
import agartha.site.controllers.utils.SessionUtil
import agartha.site.objects.response.CompanionReport
import agartha.site.objects.response.PractitionerReport
import agartha.site.objects.response.SessionReport
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Request
import spark.Response
import spark.Spark
import java.time.LocalDateTime

/**
 * Purpose of this file is handling API requests for practitioning sessions
 *
 * Created by Rebecca Fransson on 2018-04-25.
 */
class SessionController {
    // Practitioner data service
    private val mService: ISessionService
    // For mapping objects to string
    private val mMapper = jacksonObjectMapper()

    constructor(service: ISessionService) {
        mService = service
        // API path for session
        Spark.path("/session") {
            //
            // Start new session with practice
            Spark.post("/:userid/:discipline/:practice/:intention", ::insertSession)
            //
            // Session report, feedback after completed session
            Spark.get("/report/:userid", ::sessionReport)
            //
            // Companion report
            Spark.get("/companion", ::companionReport)
        }
    }


    /**
     * Start a new user session
     * @return id/index for started session
     */
    private fun insertSession(request: Request, response: Response): Int {
        // Get current userid
        val userId : String = request.params(":userid")
        // Type of discipline
        val discipline : String = request.params(":discipline")
        // Type of practice
        val practice : String = request.params(":practice")
        // Type of intention
        val intention : String = request.params(":intention")
        // Start a session
        return mService.startSession(userId, discipline, practice, intention)
    }


    /**
     * Generate and get session report after completed session
     */
    private fun sessionReport(request: Request, response: Response): String {
        // Get current userid
        val userId : String = request.params(":userid")
        // Get user from data source
        val user : PractitionerDBO = getPractitionerFromDataSource(userId)
        // Create Report for current user
        val practitionerReport : PractitionerReport = PractitionerReport(user)
        // user.sessions ought not to be empty array here, since this is a report for completed session
        // However we might end up here in dev!
        // If you are in dev mode, use the DevelopmentController for setup
        val startTime: LocalDateTime = user.sessions.last().startTime
        val endTime: LocalDateTime = user.sessions.last().endTime ?: LocalDateTime.now()
        // Filter and map to list of sessions
        val companionSessions : List<SessionDBO> = SessionUtil.filterSingleSessionActiveBetween(
                // Get practitioners sessions started during last 24 hours
                mService.getPractitionersWithSessionAfter(endTime.minusHours(Settings.SESSION_HOURS)),
                userId,
                startTime,
                endTime)
        //
        // Create Report for overlapping users
        val companionReport : CompanionReport = CompanionReport(companionSessions)
        // Return the report
        return mMapper.writeValueAsString(SessionReport(practitionerReport, companionReport))
    }

    /**
     * Generate and get a companion report. Report covers what has happened during the last/latest
     * x number of days
     */
    private fun companionReport(request: Request, response: Response): String {
        // Start date from when we should look for sessions
        val startDateTime : LocalDateTime = LocalDateTime.now().minusDays(Settings.COMPAINON_NUMBER_OF_DAYS)
        // End date from when we should look for sessions (now)
        val endDateTime : LocalDateTime = LocalDateTime.now()
        // Get practitioners
        val practitioners = mService.getAll()
        // Filter out all sessions matching dates from these practitioners
        val sessions = SessionUtil.filterAllSessionsActiveBetween(practitioners, startDateTime, endDateTime)
        // Generate report
        val companionReport = CompanionReport(practitioners.count(), sessions)
        // Return the report
        return mMapper.writeValueAsString(companionReport)
    }

    /**
     * Get a practitioner from its practitionerId from datasource or create if it does not exists
     *
     * @param userId database id for user
     * @return Database representation of practitioner
     */
    private fun getPractitionerFromDataSource(userId: String): PractitionerDBO {
        // If user exists in database, return it otherwise create, store and return
        return mService.getById(userId)
                ?: mService.insert(PractitionerDBO(userId, LocalDateTime.now(), mutableListOf()))
    }
}