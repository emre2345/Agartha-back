package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.request.PractitionerInvolvedInformation
import agartha.site.objects.request.StartSessionInformation
import agartha.site.objects.response.PractitionerReport
import org.bson.types.ObjectId
import spark.Request
import spark.Response
import spark.Spark
import java.time.LocalDateTime

/**
 * Purpose of this file is handling API requests for practitioning sessions
 *
 * Created by Jorgen Andersson on 2018-04-09.
 *
 * @param mService object for reading data from data source
 */
class PractitionerController(private val mService: IPractitionerService) {

    init {
        // API path for session
        Spark.path("/practitioner") {
            //
            // Where we have no practitionerId set yet - return info for user
            Spark.get("", ::getInformation)
            //
            // Where practitionerId has been set - return info for user and about user
            Spark.get("/:userid", ::getInformation)
            //
            // Update practitioner data
            Spark.post("/:userid", ::updatePractitioner)
            //
            // Start a Session
            Spark.post("/session/start/:userid", ::startSession)
            // End a Session
            Spark.post("/session/end/:userid/:contributionpoints", ::endSession)

        }
    }

    /**
     * Get information about current practitioner
     * @return Object with general information
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getInformation(request: Request, response: Response): String {
        // Get current userid or generate new
        val userId = getUserIdFromRequest(request)
        // Get user from data source
        val user: PractitionerDBO = getPractitionerFromDataSource(userId)
        // Create Report for current user
        val report = PractitionerReport(user)
        // Return
        return ControllerUtil.objectToString(report)
    }


    /**
     * Update practitioner with 'Get involved'-information
     * @return The updated practitioner
     */
    @Suppress("UNUSED_PARAMETER")
    private fun updatePractitioner(request: Request, response: Response): String {
        val involvedInformation: PractitionerInvolvedInformation =
                ControllerUtil.stringToObject(request.body(), PractitionerInvolvedInformation::class.java)
        // Get params
        val userId: String = getUserIdFromRequest(request)
        // Get user
        val user: PractitionerDBO = getPractitionerFromDataSource(userId)
        // Update user
        val updatedUser: PractitionerDBO = mService.updatePractitionerWithInvolvedInformation(
                user,
                involvedInformation.fullName,
                involvedInformation.email,
                involvedInformation.description)
        // Return updated user
        return ControllerUtil.objectToString(updatedUser)
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

    /**
     * Start a new user session
     * @return id/index for started session
     */
    @Suppress("UNUSED_PARAMETER")
    private fun startSession(request: Request, response: Response): String {
        // Get current userid
        val userId: String = request.params(":userid")
        // Get selected geolocation, discipline and intention
        val startSessionInformation: StartSessionInformation =
                ControllerUtil.stringToObject(request.body(), StartSessionInformation::class.java)
        // Start a session
        val session = mService.startSession(
                userId,
                startSessionInformation.geolocation,
                startSessionInformation.discipline,
                startSessionInformation.intention)
        // Return the started session
        return ControllerUtil.objectToString(session)
    }

    /**
     * End an ongoing session
     * @return practitioner with the updated session
     */
    @Suppress("UNUSED_PARAMETER")
    private fun endSession(request: Request, response: Response): String {
        // Get current userid
        val userId: String = request.params(":userid")
        val contributionPoints: Long = request.params(":contributionpoints").toLong()
        // Stop the last session for user
        val practitioner = mService.endSession(userId, contributionPoints)
        // Return the updated practitioner
        return ControllerUtil.objectToString(practitioner)
    }
}