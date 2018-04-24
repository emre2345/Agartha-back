package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.objects.Practitioner
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
        val userId = request.params(":userid") ?: UUID.randomUUID().toString()
        // Get user from data source
        val user: PractitionerDBO = getPractitionerFromDataSource(userId)
        // Return info about current user
        return mMapper.writeValueAsString(Practitioner(userId, user.sessions))
    }


    /**
     * Start a new user session
     * @return id/index for started session
     */
    private fun insertSession(request: Request, response: Response): Int {
        // Get current userid
        val userId = request.params(":userid")
        // Type of practice
        val practice = request.params(":practice")
        // Start a session
        return mService.startSession(userId, practice)
    }

    /**
     *
     */
    private fun sessionReport(request: Request, response: Response): String {
        // Get current userid
        val userId = request.params(":userid")
        // Get user from data source
        val user: PractitionerDBO = getPractitionerFromDataSource(userId)
        // Map to Practitoner
        val practitioner = Practitioner(userId, user.sessions)
        // Map to Contribution
        val companion = getSessionCompanions(user.sessions.last())

        return "HOVNO"
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
     *
     * @param pracitionerSession
     * @return
     */
    private fun getSessionCompanions(pracitionerSession: SessionDBO): List<SessionDBO> {
        val startTime = pracitionerSession.startTime
        val endTime: LocalDateTime = pracitionerSession.endTime ?: LocalDateTime.now()

        return mService
                // Get practitioners with overlapping sessions
                .getPractitionersWithSessionBetween(startTime, endTime)
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