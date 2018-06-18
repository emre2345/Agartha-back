package agartha.site.controllers

import agartha.common.config.Settings.Companion.SPIRIT_BANK_START_POINTS
import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.response.CircleReport
import spark.Request
import spark.Response
import spark.Spark
import spark.kotlin.halt
import java.time.LocalDateTime

/**
 * Purpose of this file is handling API request for practitioners circles
 *
 * Created by Jorgen Andersson on 2018-06-07.
 * @param mService object from reading from and writing to data source
 */
class CircleController(private val mService: IPractitionerService) : AbstractController() {

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
            // Add a circle to a practitioner
            Spark.post("/:userId", ::addCircle)
            //
            // Get a receipt of my circle
            Spark.get("/receipt/:userId/:circleId", ::circleReceipt)
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
     * Add a circle to argument practitioner
     * @return practitioner object
     */
    private fun addCircle(request: Request, response: Response): String {
        // Get practitioner ID from API path
        val userId: String = request.params(":userId")
        // Make sure practitionerId exists in database
        val practitioner = getPractitionerFromDatabase(userId, mService)
        // Practitioner cannot create a circle if less then 50 points in spiritBank
        if(practitioner.calculateSpiritBankPointsFromLog() < SPIRIT_BANK_START_POINTS){
            Spark.halt(400, "Practitioner cannot create circle with less than 50 contribution points")
        }
        // Get circle data from body
        val circle: CircleDBO = ControllerUtil.stringToObject(request.body(), CircleDBO::class.java)
        // Store it and return the complete practitioner object
        return ControllerUtil.objectToString(mService.addCircle(userId, circle))
    }

    /**
     * Get a receipt of a circle
     *
     * @return receipt
     */
    private fun circleReceipt(request: Request, response: Response): String {
        // Get practitioner Id from API path
        val userId: String = request.params(":userId")
        // Get circle Id from API path
        val circleId: String = request.params(":circleId")
        // Validate that practitioner exists and is the circle creator
        val circle = validateCircleCreator(userId, circleId)
        // get all sessions in this circle
        val sessions = getAllSessionsWithCircle(circleId)
        // Generate and return report/receipt
        return ControllerUtil.objectToString(CircleReport(circle, sessions))
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

    /**
     * Get all sessions for a specific circle
     *
     * @param circleId Id of circle
     * @return list of sessions within this circle
     */
    private fun getAllSessionsWithCircle(circleId: String): List<SessionDBO> {
       return mService
                // Get all practitioners
                .getAll()
                // Extract all sessions
                .flatMap { it.sessions }
                // Filter out all sessions for circles
                .filter{ it.circle != null }
                // Filter out those with this circle Id
                .filter { it.circle?._id == circleId }
    }

    private fun validateCircleCreator(practitionerId: String, circleId: String): CircleDBO {
        // Make sure practitioner is creator of circle
        val circle = getPractitionerFromDatabase(practitionerId, mService)
                .circles
                .filter { it._id == circleId }
                .firstOrNull()
        // Make sure we exit if practitioner is not creator of circle
        if (circle == null) {
            halt(400, "Practitioner is not the createor of this circle")
            // Create a dummy to avoid null response
            return CircleDBO(
                    name = "",
                    description = "",
                    startTime = LocalDateTime.now(),
                    endTime = LocalDateTime.now(),
                    disciplines = listOf(),
                    intentions = listOf(),
                    minimumSpiritContribution = 0)
        }
        return circle
    }
}