package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.services.IPractitionerService
import agartha.site.objects.PractitionerReport
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
        Spark.path("/practitioner") {
            //
            // Where we have no userId set yet - return info for user
            Spark.get("", ::getInformation)
            //
            // Where userId has been set - return info for user and about user
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
        val userId = request.params(":userid") ?: UUID.randomUUID().toString()
        // Get user from data source
        val user: PractitionerDBO = getPractitionerFromDataSource(userId)
        // Return info about current user
        return mMapper.writeValueAsString(PractitionerReport(userId, user.sessions))
    }


    /**
     * Update a practitioner with new information
     * @return id/index for started session
     */
    private fun updatePractitioner(request: Request, response: Response): String {
        val userId = request.params(":userid")
        var user: PractitionerDBO? = null
        println("updating practitioner: $userId")
        try {
            user = mService.getById(userId)
        } catch (e: Exception) {
            println(e)
        }
        return mMapper.writeValueAsString(user)

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

}