package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ObjectToStringFormatter
import agartha.site.objects.request.PractitionerInvolvedInformation
import agartha.site.objects.request.StartSessionInformation
import agartha.site.objects.response.Hovno
import agartha.site.objects.response.Jojje
import agartha.site.objects.response.PractitionerReport
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bson.types.ObjectId
import spark.Request
import spark.Response
import spark.Spark
import java.time.LocalDateTime

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

        Spark.path("/hovno") {
            // test
            Spark.get("/test", ::getHovno)
        }

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
            // Start a new Session
            Spark.post("/session/:userid", ::insertSession)
        }
    }

    private fun getHovno(request: Request, response: Response): String {
        val jojje = Jojje()
        return ObjectToStringFormatter().getFormatter().writeValueAsString(jojje)
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
        return mMapper.writeValueAsString(PractitionerReport(user))
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
    private fun insertSession(request: Request, response: Response): String {
        // Get current userid
        val userId : String = request.params(":userid")
        // Type of discipline, practice and intention
        val startSessionInformation: StartSessionInformation = mMapper.readValue(request.body(), StartSessionInformation::class.java)
        // Start a session
        val session =  mService.startSession(
                userId,
                startSessionInformation.geolocation,
                startSessionInformation.discipline,
                startSessionInformation.intention)
        // Return the started session
        return mMapper.writeValueAsString(session)
    }
}