package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.services.IPractitionerService
import agartha.site.objects.HashUtils
import agartha.site.objects.PracticeData
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Request
import spark.Response
import spark.Spark
import java.util.*

/**
 * Purpose of this file is handling API requests for practitioning sessions
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class PractitionerController {
    // Practitioner data service
    val mService : IPractitionerService
    // For mapping objects to string
    val mMapper = jacksonObjectMapper()

    constructor(service : IPractitionerService) {
        mService = service
        // API path for session
        Spark.path("/session") {
            // Where we have no userId set yet
            Spark.get("/", ::getInformation)
            // Where userId has been set
            Spark.get("/:userid", ::getInformation)
            // Start new session with practice
            Spark.post("/:userid/:practice", ::insertSession)
        }

    }


    /**
     * Get initial information about current user and other active users
     * @return Object with general information
     */
    private fun getInformation(request: Request, response: Response) : String {
        // Get current userid or generate new
        val userId = request.params(":userid") ?: HashUtils.sha1(Date().hashCode().toString())
        // Get user from data source if exists or create
        val user = mService.getById(userId) ?:  mService.insert(PractitionerDBO(mutableListOf(), Date(), userId))
        // Read data to be returned
        val activeCount = mService.getActiveCount()
        // Return data
        return mMapper.writeValueAsString(PracticeData(user._id as String, activeCount))
    }


    /**
     * Start a new user session
     * @return id/index for started session
     */
    private fun insertSession(request: Request, response: Response) : Int {
        // Get current userid
        val userId = request.params(":userid")
        // Type of practice
        val practice = request.params(":practice")
        // Start a session
        return mService.startSession(userId, practice)
    }
}