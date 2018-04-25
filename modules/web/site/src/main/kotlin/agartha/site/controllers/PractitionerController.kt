package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.objects.CompanionReport
import agartha.site.objects.PractitionerReport
import agartha.site.objects.SessionReport
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Request
import spark.Response
import spark.Spark
import java.time.LocalDateTime
import java.util.*

/**
 * Purpose of this file is handling API requests for practitioning sessions
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class PractitionerController {
    // Practitioner data service
    private val mService: IPractitionerService
    // For mapping objects to string
    private val mMapper = jacksonObjectMapper()

    constructor(service: IPractitionerService) {
        mService = service
        // API path for session
        Spark.path("/session") {
            //
            // Where we have no userId set yet
            Spark.get("/", ::getInformation)
            //
            // Where userId has been set
            Spark.get("/:userid", ::getInformation)
            //
            // Start new session with practice
            Spark.post("/:userid/:practice", ::insertSession)
            //
            // Session report, feedback after completed session
            Spark.get("/report/:userid", ::sessionReport)
        }
    }


    /**
     * Get information about current practitioner
     *
     * @return Object with general information
     */
    private fun getInformation(request: Request, response: Response): String {
        // Get current userid or generate new
        val userId = getUserIdFromRequest(request)
        // Get user from data source
        val user: PractitionerDBO = getPractitionerFromDataSource(userId)
        // Create Report for current user
        val practitionerReport : PractitionerReport = PractitionerReport(userId, user.sessions)
        val startTime : LocalDateTime = LocalDateTime.now().minusMinutes(30)
        val endTime : LocalDateTime = LocalDateTime.now()
        // Map to Companions
        val companionSessions : List<SessionDBO> = getSessionCompanions(userId, startTime, endTime)
        // Create Report for session ongoing during last x minutes
        val companionReport : CompanionReport = CompanionReport(companionSessions)
        // Return the report
        return mMapper.writeValueAsString(SessionReport(practitionerReport, companionReport))
    }


    /**
     * Start a new user session
     * @return id/index for started session
     */
    private fun insertSession(request: Request, response: Response): Int {
        // Get current userid
        val userId : String = getUserIdFromRequest(request)
        // Type of practice
        val practice : String = request.params(":practice")
        // Start a session
        return mService.startSession(userId, practice)
    }

    /**
     *
     */
    private fun sessionReport(request: Request, response: Response): String {
        // Get current userid
        val userId : String = getUserIdFromRequest(request)
        // Get user from data source
        val user : PractitionerDBO = getPractitionerFromDataSource(userId)
        // Create Report for current user
        val practitionerReport : PractitionerReport = PractitionerReport(userId, user.sessions)
        val startTime : LocalDateTime = user.sessions.last().startTime
        val endTime : LocalDateTime = user.sessions.last().endTime ?: LocalDateTime.now()
        // Map to Companions
        val companionSessions : List<SessionDBO> = getSessionCompanions(userId, startTime, endTime)
        // Create Report for overlapping user(s) sessions
        val companionReport : CompanionReport = CompanionReport(companionSessions)
        // Return the report
        return mMapper.writeValueAsString(SessionReport(practitionerReport, companionReport))
    }


    /**
     * Get or create User Id
     *
     * @param request API request object
     * @return user id from request or generated if missing
     */
    private fun getUserIdFromRequest(request: Request) : String {
        return request.params(":userid") ?: UUID.randomUUID().toString()
    }

    /**
     * Get a practitioner from its userId from datasource or create if it does not exists
     *
     * @param userId database id for user
     * @return Database representation of practitioner
     */
    private fun getPractitionerFromDataSource(userId: String): PractitionerDBO {
        // If user exists in database, return it otherwise create, store and return
        return mService.getById(userId)
                ?: mService.insert(PractitionerDBO(mutableListOf(), LocalDateTime.now(), userId))
    }


    /**
     * Get sessions from other user with overlapping start/end time as argument session
     *
     * @param startTime start time for sessions we are looking for
     * @param endTime end time for sessions we are looking for
     * @return List of overlapping sessions
     */
    private fun getSessionCompanions(userId: String, startTime : LocalDateTime, endTime : LocalDateTime): List<SessionDBO> {
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