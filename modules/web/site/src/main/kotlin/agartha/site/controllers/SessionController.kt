package agartha.site.controllers

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
            Spark.post("/:userid/:practice", ::insertSession)
            //
            // Session report, feedback after completed session
            Spark.get("/report/:userid", ::sessionReport)
        }
    }


    /**
     * Start a new user session
     * @return id/index for started session
     */
    private fun insertSession(request: Request, response: Response): Int {
        // Get current userid
        val userId : String = request.params(":userid")
        // Type of practice
        val practice : String = request.params(":practice")
        // Start a session
        return mService.startSession(userId, practice)
    }


    /**
     * Get session report after completed session
     */
    private fun sessionReport(request: Request, response: Response): String {
        // Get current userid
        val userId : String = request.params(":userid")
        // Get user from data source
        val user : PractitionerDBO = getPractitionerFromDataSource(userId)
        // Create Report for current user
        val practitionerReport : PractitionerReport = PractitionerReport(userId, user.sessions)
        // user.sessions ought not to be empty array here, since this is a report for completed session
        // However we might end up here in dev!
        // If you are in dev mode, use the DevelopmentController for setup
        val startTime: LocalDateTime = user.sessions.last().startTime
        val endTime: LocalDateTime = user.sessions.last().endTime ?: LocalDateTime.now()
        // Map to Contribution
        val companionSessions : List<SessionDBO> = SessionUtil.filterSessionsBetween(
                // Get practitioners with overlapping sessions
                mService.getPractitionersWithSessionBetween(startTime, endTime),
                userId,
                startTime,
                endTime)
        // Create Report for overlapping users
        val companionReport : CompanionReport = CompanionReport(companionSessions)
        // Return the report
        return mMapper.writeValueAsString(SessionReport(practitionerReport, companionReport))
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