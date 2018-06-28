package agartha.site.controllers

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ErrorMessagesEnum
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
            halt(400, ErrorMessagesEnum.PRACTITIONER_ID_INCORRECT.message)
        }
    }

    /**
     * Validate that the argument "circleid" does exist in datastore
     */
    @Suppress("UNUSED_PARAMETER")
    fun validateCircle(request: Request, response: Response) {
        val circle = getCircle(request, false)
        if (circle._id.isEmpty()) {
            halt(400, ErrorMessagesEnum.CIRCLE_NOT_ACTIVE_OR_EXIST.message)
        }
    }

    /**
     * Validate that the argument "circleid" does exist in datastore as is active
     */
    @Suppress("UNUSED_PARAMETER")
    fun validateActiveCircle(request: Request, response: Response) {
        val circle = getCircle(request, true)
        if (circle._id.isEmpty()) {
            halt(400, ErrorMessagesEnum.CIRCLE_NOT_ACTIVE_OR_EXIST.message)
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
     * @param request api request
     * @param checkActive boolean decides if it should check for active circles
     * @return circle
     */
    fun getCircle(request: Request, checkActive: Boolean): CircleDBO {
        // Get circle id from request
        val circleId: String = request.params(ReqArgument.CIRCLE_ID.value)
        val emptyCircle = CircleDBO(_id = "", name = "", description = "",
                startTime = DateTimeFormat.localDateTimeUTC(), endTime = DateTimeFormat.localDateTimeUTC(),
                disciplines = listOf(), intentions = listOf(), minimumSpiritContribution = 0,
                language = "")
        // Find the circle
        val circle: CircleDBO? = mService
                // Get all practitioner
                .getAll()
                // Get all circles from practitioner
                .flatMap { it.circles }
                // Find the one with correct id
                .find { it._id == circleId }
        // Check if  the one we are looking for should be active
        if (checkActive && circle != null) {
            return if (circle.active()) {
                circle
            } else {
                emptyCircle
            }
        }
        // Return the circle even if its not active, or empty circle if its null
        return circle ?: emptyCircle
    }
}