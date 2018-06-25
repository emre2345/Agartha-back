package agartha.site.controllers

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ReqArgument
import spark.Request
import spark.Response
import spark.Spark.halt

/**
 * Purpose of this file is handle mapping requests that can go wrong
 *
 * Created by Jorgen Andersson on 2018-06-07.
 */
abstract class AbstractController(private val mService: IPractitionerService) {

    /**
     * Validate that the argument "userid" does exist in datastore
     */
    @Suppress("UNUSED_PARAMETER")
    fun validatePractitioner(request: Request, response: Response) {
        val practitioner = getPractitioner(request)
        if (practitioner._id.isNullOrEmpty()) {
            halt(400, "Practitioner Id missing or incorrect")
        }
    }

    /**
     * Validate that the argument "circleid" does exist in datastore
     */
    @Suppress("UNUSED_PARAMETER")
    fun validateCircle(request: Request, response: Response) {
        val circle = getCircle(request)
        if (circle._id.isEmpty()) {
            halt(400, "Circle not active")
        }
    }

    /**
     * Get practitioner from datasource
     * The null should not happen since Spark.before should catch these
     */
    fun getPractitioner(request: Request): PractitionerDBO {
        val practitionerId = request.params(ReqArgument.PRACTITIONER_ID.value)
        return mService.getById(practitionerId) ?: PractitionerDBO(_id = "")
    }

    /**
     * Get the circle from id (if Exists)
     */
    fun getCircle(request: Request): CircleDBO {
        // Get circle id from request
        val circleId: String = request.params(ReqArgument.CIRCLE_ID.value)
        // Find the circle
        val circle: CircleDBO? = mService
                // Get all practitioner
                .getAll()
                // Get all circles from practitioner
                .flatMap { it.circles }
                // Filter out active
                .filter { it.active() }
                // Find the one with correct id
                .find { it._id == circleId }

        return circle ?: CircleDBO(_id = "", name = "", description = "",
                startTime = DateTimeFormat.localDateTimeUTC(), endTime = DateTimeFormat.localDateTimeUTC(),
                disciplines = listOf(), intentions = listOf(), minimumSpiritContribution = 0,
                language = "")
    }
}