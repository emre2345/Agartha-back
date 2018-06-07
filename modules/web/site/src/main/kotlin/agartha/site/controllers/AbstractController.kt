package agartha.site.controllers

import org.bson.types.ObjectId
import spark.Request
import spark.Spark.halt

/**
 * Purpose of this file is handle mapping requests that can go wrong
 *
 * Created by Jorgen Andersson on 2018-06-07.
 */
abstract class AbstractController {

    /**
     * Read API request parameter userId and if missing, return http status 400
     */
    fun getUserIdFromRequest(request: Request, generateIfMissing: Boolean = false): String {
        // Get practitionerId from request param
        val userId =  request.params(":userid")
        // If param is empty
        if (userId.isNullOrEmpty()) {
            // Should we generate new if missing
            if (generateIfMissing) {
                return ObjectId().toHexString()
            }
            // Should not generate but still missing, send status 400
            halt(400, "Practitioner Id missing or incorrect")
        }
        // practitionerId is attached and should be returned
        return userId
    }
}