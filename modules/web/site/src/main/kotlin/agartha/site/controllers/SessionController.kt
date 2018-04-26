package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.ISessionService
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
     * Get session report from a users latest session
     */
    private fun sessionReport(request: Request, response: Response): String {
        // Get current userid
        val userId : String = request.params(":userid")
        // Get user from data source
        val user : PractitionerDBO = getPractitionerFromDataSource(userId)
        // Create Report for current user
        val practitionerReport : PractitionerReport = PractitionerReport(userId, user.sessions, user)
        // Map to Contribution
        val companionSessions : List<SessionDBO> = getSessionCompanions(userId, user.sessions.last())
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


    /**
     * Get sessions from other user with overlapping start/end time as argument session
     *
     * @param pracitionerSession session for a practitioner from which we use start and end time
     * @return List of overlapping sessions
     */
    private fun getSessionCompanions(userId: String, pracitionerSession: SessionDBO): List<SessionDBO> {
        val startTime : LocalDateTime = pracitionerSession.startTime
        val endTime : LocalDateTime = pracitionerSession.endTime ?: LocalDateTime.now()

        return mService
                // Get practitioners with overlapping sessions
                .getPractitionersWithSessionBetween(startTime, endTime)
                // Filter out the current user
                .filter {
                    it._id != userId
                }
                // Get the overlapping sessions from these practitioners
                .map {
                    // Filter out overlapping sessions
                    it.sessions.filter {
                        // Start time should be between
                        it.sessionOverlap(startTime, endTime)
                    }
                            // Return first overlapping session for each practitioner
                            .first()
                }
                .toList()
    }
}