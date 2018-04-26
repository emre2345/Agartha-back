package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.objects.response.CompanionReport
import agartha.site.objects.response.PractitionerReport
import agartha.site.objects.response.SessionReport
import agartha.site.objects.request.PractitionerInvolvedInformation
import agartha.site.controllers.utils.SessionUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bson.types.ObjectId
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
        Spark.path("/practitioner") {
            //
            // Where we have no practitionerId set yet - return info for user
            Spark.get("", ::getInformation)
            //
            // Where practitionerId has been set - return info for user and about user
            Spark.get("/:userid", ::getInformation)
            //
            // Start new session with practice
            Spark.put("/:userid", ::updatePractitioner)
        }
    }


    /**
     * Get information about current practitioner
     * @return Object with general information
     */
    private fun getInformation(request: Request, response: Response): String {
        // Get current userid or generate new
        val userId = getUserIdFromRequest(request)
        // Get user from data source
        val user: PractitionerDBO = getPractitionerFromDataSource(userId)
        // Create Report for current user
        val practitionerReport: PractitionerReport = PractitionerReport(userId, user.sessions)
        val startTime: LocalDateTime = LocalDateTime.now().minusMinutes(30)
        val endTime: LocalDateTime = LocalDateTime.now()
        // Map to Companions
        val companionSessions: List<SessionDBO> = getSessionCompanions(userId, startTime, endTime)
        // Create Report for session ongoing during last x minutes
        val companionReport: CompanionReport = CompanionReport(companionSessions)
        // Return the report
        return mMapper.writeValueAsString(SessionReport(practitionerReport, companionReport))
    }


    /**
     * Update practitioner with 'Get involved'-information
     * @return The updated practitioner
     */
    private fun updatePractitioner(request: Request, response: Response): String {
        val involvedInformation: PractitionerInvolvedInformation = mMapper.readValue(request.body(), PractitionerInvolvedInformation::class.java)
        // Get params
        val userId: String = getUserIdFromRequest(request)
        // Get user
        val user: PractitionerDBO = getPractitionerFromDataSource(userId)
        println(user)
        // Update user
        val updatedUser: PractitionerDBO = mService.updatePractitionerWithInvolvedInformation(
                user,
                involvedInformation.fullName,
                involvedInformation.email,
                involvedInformation.description)
        // Return updated user
        return mMapper.writeValueAsString(updatedUser)
    }


    /**
     * Get sessions from other user with overlapping start/end time as argument session
     *
     * @param startTime start time for sessions we are looking for
     * @param endTime end time for sessions we are looking for
     * @return List of overlapping sessions
     */
    private fun getSessionCompanions(userId: String, startTime: LocalDateTime, endTime: LocalDateTime): List<SessionDBO> {

        return SessionUtil.filterSessionsBetween(
                // Get practitioners with overlapping sessions
                mService.getPractitionersWithSessionBetween(startTime, endTime),
                startTime,
                endTime)

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
     * Get or create User Id
     *
     * @param request API request object
     * @return user id from request or generated if missing
     */
    private fun getUserIdFromRequest(request: Request): String {
        return request.params(":userid") ?: ObjectId().toHexString()
    }

}