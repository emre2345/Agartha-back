package agartha.site.controllers

import agartha.common.config.Settings
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.ISessionService
import agartha.site.controllers.utils.PractitionerUtil
import agartha.site.controllers.utils.SessionUtil
import agartha.site.objects.request.StartSessionInformation
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
            Spark.post("/:userid", ::insertSession)
        }
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
                startSessionInformation.practice,
                startSessionInformation.intention)
        // Return the started session
        return mMapper.writeValueAsString(session)
    }
}