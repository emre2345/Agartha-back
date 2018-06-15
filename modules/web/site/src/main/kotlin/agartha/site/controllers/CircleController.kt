package agartha.site.controllers

import agartha.common.config.Settings.Companion.SPIRIT_BANK_START_POINTS
import agartha.data.objects.CircleDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import spark.Request
import spark.Response
import spark.Spark

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
     * Add a circle to argument practitioner
     * @return practitioner object
     */
    private fun addCircle(request: Request, response: Response): String {
        // Get practitioner ID from API path
        val practitionerId: String = request.params(":userid")
        // Make sure practitionerId exists in database
        val practitioner = getPractitionerFromDatabase(practitionerId, mService)
        // Practitioner cannot create a circle if less then 50 points in spiritBank
        if(practitioner.calculateSpiritBankPointsFromLog() < SPIRIT_BANK_START_POINTS){
            Spark.halt(400, "Practitioner cannot create circle with less than 50 contribution points")
        }
        // Get circle data from body
        val circle: CircleDBO = ControllerUtil.stringToObject(request.body(), CircleDBO::class.java)
        // Store it and return the complete practitioner object
        return ControllerUtil.objectToString(mService.addCircle(practitionerId, circle))
    }
}