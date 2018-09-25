package agartha.site.controllers

import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.*
import agartha.site.objects.response.CircleReport
import agartha.site.objects.response.RegisteredReport
import io.schinzel.basicutils.configvar.IConfigVar
import spark.Request
import spark.Response
import spark.Spark

class CirclesController(
        private val mService: IPractitionerService,
        private val mConfig: IConfigVar) : AbstractController(mService) {

    private val minConfigCircleCreate = mConfig.getValue("A_MIN_POINTS_CREATE_CIRCLE").toLong()

    init {
        // API path for circles
        Spark.path("/circles") {
            // Add a circle to a practitioner
            Spark.before("/${ReqArgument.PRACTITIONER_ID.value}", ::validatePractitioner)
            Spark.before("/${ReqArgument.PRACTITIONER_ID.value}", ::validateCreateCircle)
            Spark.post("/${ReqArgument.PRACTITIONER_ID.value}", ::createCircle)
        }
    }

    /**
     * Validate that the argument "userid" is allowed to create a circle
     */
    @Suppress("UNUSED_PARAMETER")
    private fun validateCreateCircle(request: Request, response: Response) {
        val practitioner: PractitionerDBO = getPractitioner(request)
        if (practitioner.calculateSpiritBankPointsFromLog() < minConfigCircleCreate) {
            Spark.halt(400, """{"error":"Practitioner cannot create circle with less than $minConfigCircleCreate contribution points", "errorCode":10001}""")
        }
    }

    /**
     * Add or edit a circle to argument practitioner
     * @return practitioner object as a string
     */
    @Suppress("UNUSED_PARAMETER")
    private fun createCircle(request: Request, response: Response): String {
        val practitioner: PractitionerDBO = getPractitioner(request)
        try {
            val circle: CircleDBO = ControllerUtil.stringToObject(request.body(), CircleDBO::class.java)
            ControllerUtil.objectToString(mService.addCircle(practitioner._id!!, circle))
            return """{"_id":"${circle._id}"}"""
        } catch (e: Exception) {
            Spark.halt(400, """{"error":"Insufficient data to create circle.", "errorCode":10003}""")
        }
        return ""
    }
}
