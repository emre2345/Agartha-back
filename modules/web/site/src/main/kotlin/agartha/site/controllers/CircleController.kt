package agartha.site.controllers

import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.controllers.utils.ReqArgument
import agartha.site.controllers.utils.SessionUtil
import agartha.site.controllers.utils.SpiritBankLogUtil
import agartha.site.objects.response.CircleReport
import io.schinzel.basicutils.configvar.IConfigVar
import spark.Request
import spark.Response
import spark.Spark

/**
 * Purpose of this file is handling API request for practitioners circles
 *
 * Created by Jorgen Andersson on 2018-06-07.
 * @param mService object from reading from and writing to data source
 * @param mConfig object to read config params from
 */
class CircleController(
        private val mService: IPractitionerService,
        private val mConfig: IConfigVar) : AbstractController(mService) {

    private val minConfigCircleCreate = mConfig.getValue("A_MIN_POINTS_CREATE_CIRCLE").toLong()

    init {
        // API path for circles
        Spark.path("/circle") {
            //
            // Get a list of all circles
            Spark.get("", ::getAll)
            //
            // Get a list of all currently active circles
            Spark.get("/active", ::getAllActive)
            //
            // Get a list of all circles for a user
            Spark.before("/created/${ReqArgument.PRACTITIONER_ID.value}", ::validatePractitioner)
            Spark.get("/created/${ReqArgument.PRACTITIONER_ID.value}", ::getAllForUser)
            //
            // Add a circle to a practitioner
            Spark.before("/add/${ReqArgument.PRACTITIONER_ID.value}", ::validatePractitioner)
            Spark.before("/add/${ReqArgument.PRACTITIONER_ID.value}", ::validateCreateCircle)
            Spark.post("/add/${ReqArgument.PRACTITIONER_ID.value}", ::addOrEditCircle)
            //
            // Edit a circle to a practitioner
            Spark.before("/edit/${ReqArgument.PRACTITIONER_ID.value}", ::validatePractitioner)
            Spark.post("/edit/${ReqArgument.PRACTITIONER_ID.value}", ::addOrEditCircle)
            //
            // Get a receipt of my circle
            Spark.before("/receipt/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::validatePractitioner)
            Spark.before("/receipt/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::validateCircle)
            Spark.before("/receipt/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::validateCircleCreator)
            Spark.get("/receipt/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::circleReceipt)
        }
    }

    /**
     * Valdate that the argument "userid" is allowed to create a circle
     */
    @Suppress("UNUSED_PARAMETER")
    private fun validateCreateCircle(request: Request, response: Response) {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        // Practitioner cannot create a circle if less then 50 points in spiritBank
        if (practitioner.calculateSpiritBankPointsFromLog() < minConfigCircleCreate) {
            Spark.halt(400, "Practitioner cannot create circle with less than $minConfigCircleCreate contribution points")
        }
    }

    /**
     * Validate that the argument "circleid" is created by the argument "userid"
     */
    @Suppress("UNUSED_PARAMETER")
    private fun validateCircleCreator(request: Request, response: Response) {
        val practitioner = getPractitioner(request)
        val circleId: String = request.params(ReqArgument.CIRCLE_ID.value)
        val circle = practitioner
                .circles
                .filter { it._id == circleId }
                .firstOrNull()
        // Make sure we exit if practitioner is not creator of circle
        if (circle == null) {
            spark.kotlin.halt(400, "Practitioner is not the creator of this circle")
        }
    }

    /**
     * Get all circles
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getAll(request: Request, response: Response): String {
        return ControllerUtil.objectListToString(getAllCircles())
    }

    /**
     * Get all active circles
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getAllActive(request: Request, response: Response): String {
        return ControllerUtil.objectListToString(getAllCircles().filter { it.active() })
    }

    /**
     * Get all circles that a user created
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getAllForUser(request: Request, response: Response): String {
        val practitioner = getPractitioner(request)
        return ControllerUtil.objectListToString(practitioner.circles)
    }


    /**
     * Add or edit a circle to argument practitioner
     * @return practitioner object
     */
    @Suppress("UNUSED_PARAMETER")
    private fun addOrEditCircle(request: Request, response: Response): String {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        // Get circle data from body
        val circle: CircleDBO = ControllerUtil.stringToObject(request.body(), CircleDBO::class.java)
        // Check if this circle already exists to this practitioner
        val circleToEdit = practitioner.circles.find { it._id == circle._id }
        if (circleToEdit != null) {
            // Exists - Edit circle and return the complete practitioner object
            return ControllerUtil.objectToString(mService.editCircle(practitioner._id ?: "", circle))
        } else {
            // Does not exist - Store circle and return the complete practitioner object
            return ControllerUtil.objectToString(mService.addCircle(practitioner._id ?: "", circle))
        }
    }

    /**
     * Get a receipt of a circle
     * reuqires paramters practitioner id and circle id where both must exist in database
     *
     * @return receipt
     */
    @Suppress("UNUSED_PARAMETER")
    private fun circleReceipt(request: Request, response: Response): String {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        // Validate that practitioner exists and is the circle creator
        val circle: CircleDBO = getCircle(request)
        // Count points generated for this circle
        val logPoints = SpiritBankLogUtil.countLogPointsForCircle(practitioner.spiritBankLog, circle)
        // get all sessions in this circle
        val sessions = SessionUtil.getAllSessionsInCircle(mService.getAll(), circle._id)
        // Generate and return report/receipt
        return ControllerUtil.objectToString(CircleReport(circle, sessions, logPoints))
    }

    /**
     * Get all circles from practitioners
     */
    private fun getAllCircles(): List<CircleDBO> {
        return mService
                .getAll()
                .flatMap {
                    it.circles
                }
    }

}