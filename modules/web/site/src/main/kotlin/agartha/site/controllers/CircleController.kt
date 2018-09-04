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
            // Get owner/creator of a circle
            Spark.before("/creator/${ReqArgument.CIRCLE_ID.value}", ::validateOwner)
            Spark.get("/creator/${ReqArgument.CIRCLE_ID.value}", ::getOwnerForCircle)
            //
            // Get a list of all circles for a user
            Spark.before("/created/${ReqArgument.PRACTITIONER_ID.value}", ::validatePractitioner)
            Spark.get("/created/${ReqArgument.PRACTITIONER_ID.value}", ::getAllForUser)
            //
            // Get a list of all circles that a user is registered to
            Spark.before("/registered/${ReqArgument.PRACTITIONER_ID.value}", ::validatePractitioner)
            Spark.get("/registered/${ReqArgument.PRACTITIONER_ID.value}", ::getAllRegisteredForUser)
            //
            // Get a report for how many is registered to a specific circle
            Spark.before("/registered/total/${ReqArgument.CIRCLE_ID.value}", ::validateCircle)
            Spark.get("/registered/total/${ReqArgument.CIRCLE_ID.value}", ::getTotalRegistered)
            //
            // Give feedback to a circle
            Spark.post("/feedback/${ReqArgument.CIRCLE_ID.value}/${ReqArgument.POINTS.value}", ::giveFeedback)
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

            // Remove a circle
            Spark.before("/remove/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::validatePractitioner)
            Spark.before("/remove/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::validateCircle)
            Spark.before("/remove/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::validateCircleCreator)
            Spark.post("/remove/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::removeCircle)
        }
    }

    /**
     * Validate that the argument "userid" is allowed to create a circle
     */
    @Suppress("UNUSED_PARAMETER")
    private fun validateCreateCircle(request: Request, response: Response) {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        // Practitioner cannot create a circle if less then 50 points in spiritBank
        if (practitioner.calculateSpiritBankPointsFromLog() < minConfigCircleCreate) {
            Spark.halt(400, """{"error":"Practitioner cannot create circle with less than $minConfigCircleCreate contribution points"}""")
        }
    }

    /**
     * Validate that a circle with this id exists
     */
    @Suppress("UNUSED_PARAMETER")
    private fun validateOwner(request: Request, response: Response) {
        val circleId: String = request.params(ReqArgument.CIRCLE_ID.value)
        val circle: CircleDBO = getCircle(request, false)
        if (circle._id.isEmpty()) {
            Spark.halt(400, """{"error":"There is no circle with id $circleId"}""")
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
                .circles.firstOrNull { it._id == circleId }
        // Make sure we exit if practitioner is not creator of circle
        if (circle == null) {
            spark.kotlin.halt(400, ErrorMessagesEnum.PRACTITIONER_NOT_CREATOR_CIRCLE.getAsJson())
        }
    }

    /**
     * Get all circles
     * @return list of circles as a string
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getAll(request: Request, response: Response): String {
        return ControllerUtil.objectListToString(getAllCircles())
    }

    /**
     * Get all active circles
     * @return list of circles as a string
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getAllActive(request: Request, response: Response): String {
        return ControllerUtil.objectListToString(getAllCircles().filter { it.active() })
    }

    /**
     * Get all circles that a user created
     * @return list of circles as a string
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getAllForUser(request: Request, response: Response): String {
        val practitioner = getPractitioner(request)
        return ControllerUtil.objectListToString(practitioner.circles)
    }

    /**
     * Get creator/owner of a circle by its Id
     * @return list of circles as a string
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getOwnerForCircle(request: Request, response: Response): String {
        val circle = getCircle(request, false)
        val practitioner = mService.getCreatorOfCircle(circle)
        return ControllerUtil.objectToString(practitioner)
    }

    /**
     * Get all circles that a user is registered to
     * @return list of circles as a string
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getAllRegisteredForUser(request: Request, response: Response): String {
        val practitioner = getPractitioner(request)
        val idOfRegisteredCircles = practitioner.registeredCircles
        // Filter out the circles that the practitioner is registered to
        val registeredTo = getAllCircles().filter { idOfRegisteredCircles.contains(it._id) }
        return ControllerUtil.objectListToString(registeredTo)
    }


    /**
     * Add or edit a circle to argument practitioner
     * @return practitioner object as a string
     */
    @Suppress("UNUSED_PARAMETER")
    private fun addOrEditCircle(request: Request, response: Response): String {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        // Get circle data from body
        val circle: CircleDBO = ControllerUtil.stringToObject(request.body(), CircleDBO::class.java)
        // Check if this circle already exists to this practitioner
        val circleToEdit = practitioner.circles.find { it._id == circle._id }
        return if (circleToEdit != null) {
            // Exists - Check if circle to edit has a new value for virtualRegistered
            //          and if user cannot afford this many virtualRegistered
            if (circle.virtualRegistered != circleToEdit.virtualRegistered &&
                    !mService.checkPractitionerCanAffordVirtualRegistered(practitioner, circle.virtualRegistered)) {
                //
                // Cannot afford to edit to this amount of virtualRegistered
                spark.kotlin.halt(400, ErrorMessagesEnum.PRACTITIONER_NOT_AFFORD_ADD_VIRTUAL.getAsJson())
            }
            // Edit circle and return the complete practitioner object
            ControllerUtil.objectToString(mService.editCircle(practitioner._id ?: "", circle))

        } else {
            // Does not exist - Store circle and return the complete practitioner object
            ControllerUtil.objectToString(mService.addCircle(practitioner._id ?: "", circle))
        }
    }

    /**
     * Get a receipt of a circle
     * requires parameters practitioner id and circle id where both must exist in database
     *
     * @return receipt as a string
     */
    @Suppress("UNUSED_PARAMETER")
    private fun circleReceipt(request: Request, response: Response): String {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        // Validate that practitioner exists and is the circle creator
        val circle: CircleDBO = getCircle(request, false)
        // Count points generated for this circle
        val logPoints = SpiritBankLogUtil.countLogPointsForCircle(practitioner.spiritBankLog, circle)
        // get all sessions in this circle
        val sessions = SessionUtil.getAllSessionsInCircle(mService.getAll(), circle._id)
        // Generate and return report/receipt
        return ControllerUtil.objectToString(CircleReport(circle, sessions, logPoints))
    }

    /**
     * Get the total nr of registered adn return them in a registeredReport
     *
     * @return registeredReport as a string
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getTotalRegistered(request: Request, response: Response): String {
        val circle: CircleDBO = getCircle(request, false)
        // Get all practitioners
        val practitioners = mService.getAll()
        // Search for those practitioners that has this circle id in their registeredCircles
        val practitionersRegistered = practitioners.filter { it.registeredCircles.contains(circle._id) }.size
        // Create a report object and return it
        return ControllerUtil.objectToString(RegisteredReport(circle.virtualRegistered, practitionersRegistered.toLong()))
    }

    /**
     * Gives feedback to a circle
     *
     * @return registeredReport as a string
     */
    @Suppress("UNUSED_PARAMETER")
    private fun giveFeedback(request: Request, response: Response): String {
        val circle: CircleDBO = getCircle(request, false)
        val feedback = request.params(ReqArgument.POINTS.value).toLong()
        val wentFine = mService.giveFeedback(circle, feedback)
        if(!wentFine){
            spark.kotlin.halt(400, ErrorMessagesEnum.GIVE_FEEDBACK.getAsJson())
        }
        return ControllerUtil.objectToString(getCircle(request, false))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun removeCircle(request: Request, response: Response): String {
        val practitioner: PractitionerDBO = getPractitioner(request)
        val circle: CircleDBO = getCircle(request, false)
        // Remove the circle
        val status = mService.removeCircleById(
                practitionerId = practitioner._id ?: "", circleId = circle._id)
        return """{"status":$status}"""

    }

    /**
     * Get all circles from practitioners
     * @return List of all circles
     */
    private fun getAllCircles(): List<CircleDBO> {
        return mService
                .getAll()
                .flatMap {
                    it.circles
                }
    }

}