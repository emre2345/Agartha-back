package agartha.site.controllers

import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.request.PractitionerInvolvedInformation
import agartha.site.objects.request.StartSessionInformation
import agartha.site.objects.response.PractitionerReport
import org.bson.types.ObjectId
import spark.Request
import spark.Response
import spark.Spark
import spark.Spark.halt
import java.time.LocalDateTime

/**
 * Purpose of this file is handling API requests for practitioning sessions
 *
 * Created by Jorgen Andersson on 2018-04-09.
 *
 * @param mService object for reading data from data source
 */
class PractitionerController(private val mService: IPractitionerService) : AbstractController() {

    init {
        // API path for session
        Spark.path("/practitioner") {

            //
            // Where we have no practitionerId set yet - return info for the practitioner
            Spark.get("", ::createPractitioner)
            //
            //
            Spark.before("/:userid", ::validatePractitionerIdentity)
            // Where practitionerId has been set - return info for the practitioner and about practitioner
            Spark.get("/:userid", ::getInformation)
            // Update practitioner data
            Spark.post("/:userid", ::updatePractitioner)
            //
            // Start a Session
            Spark.before("/session/start/:userid", ::validatePractitionerIdentity)
            Spark.before("/session/start/:userid", ::validateSessionStart)
            Spark.post("/session/start/:userid", ::startSession)
            //
            // End a Session
            Spark.before("/session/end/:userid/:contributionpoints", ::validatePractitionerIdentity)
            Spark.post("/session/end/:userid/:contributionpoints", ::endSession)
            //
            // Start practicing by Joining a Circle
            Spark.before("/circle/join/:userid/:circleid", ::validatePractitionerIdentity)
            Spark.before("/circle/join/:userid/:circleid", ::validateSessionStart)
            Spark.before("/circle/join/:userid/:circleid", ::validateJoinCircle)
            Spark.post("/circle/join/:userid/:circleid", ::joinCircle)
            //
            // Get practitioners spiritBankHistory
            Spark.before("/spiritbankhistory/:userid", ::validatePractitionerIdentity)
            Spark.get("/spiritbankhistory/:userid", ::getSpiritBankHistory)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun validatePractitionerIdentity(request: Request, response: Response) {
        val practitionerId = request.params(":userid")
        val practitioner = mService.getById(practitionerId)
        if (practitioner == null) {
            halt(400, "Practitioner Id missing or incorrect")
        }
    }

    /**
     * Validate that required body exists and is readable before access to the API methods
     * for starting session
     */
    @Suppress("UNUSED_PARAMETER")
    private fun validateSessionStart(request: Request, response: Response) {
        // Get selected geolocation, discipline and intention
        val sessionInfo: StartSessionInformation =
                ControllerUtil.stringToObject(request.body(), StartSessionInformation::class.java)

        // If required info is missing
        if (sessionInfo.discipline.isEmpty() || sessionInfo.intention.isEmpty()) {
            halt(400, "Discipline and Intention cannot be empty")
        }
    }

    /**
     * Validate
     */
    @Suppress("UNUSED_PARAMETER")
    private fun validateJoinCircle(request: Request, response: Response) {
        val circleId: String = request.params(":circleid")
        val sessionInfo: StartSessionInformation =
                ControllerUtil.stringToObject(request.body(), StartSessionInformation::class.java)
        // Get the original circle user wants to join
        val circle = getActiveCircleFromDatabase(circleId, mService)
        // Validate that circle is active and the selected discipline and intention matching
        if (circle == null) {
            halt(400, "Circle not active")
        } else {
            // Only try to match if circle has disciplines
            if (circle.disciplines.isNotEmpty()) {
                if (circle.disciplines.find { it.title == sessionInfo.discipline } == null) {
                    Spark.halt(400, "Selected discipline does not match any in Circle")
                }
            }
            // Only try to match if circle has intentions
            if (circle.intentions.isNotEmpty()) {
                if (circle.intentions.find { it.title == sessionInfo.intention } == null) {
                    Spark.halt(400, "Selected intention does not match any in Circle")
                }
            }
        }
    }


    /**
     * Get practitioner from datasource
     * The null should not happen since Spark.before should catch these
     */
    private fun getPractitioner(request: Request) : PractitionerDBO {
        val practitionerId = request.params(":userid")
        return mService.getById(practitionerId) ?: PractitionerDBO()
    }

    /**
     * Get information about current practitioner
     * @return Object with general information
     */
    @Suppress("UNUSED_PARAMETER")
    private fun createPractitioner(request: Request, response: Response): String {
        // Get practitioner from data source
        val practitioner = mService.insert(PractitionerDBO(_id = ObjectId().toHexString()))
        // Create Report for current practitioner
        val report = PractitionerReport(practitioner)
        // Return
        return ControllerUtil.objectToString(report)
    }

    /**
     * Get information about current practitioner
     * @return Object with general information
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getInformation(request: Request, response: Response): String {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        // Create Report for current practitioner
        val report = PractitionerReport(practitioner)
        // Return
        return ControllerUtil.objectToString(report)
    }

    /**
     * Update practitioner with 'Get involved'-information
     * @return The updated practitioner
     */
    @Suppress("UNUSED_PARAMETER")
    private fun updatePractitioner(request: Request, response: Response): String {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        //
        val involvedInformation: PractitionerInvolvedInformation =
                ControllerUtil.stringToObject(request.body(), PractitionerInvolvedInformation::class.java)
        // Update practitioner
        val updatedPractitioner: PractitionerDBO = mService.updatePractitionerWithInvolvedInformation(
                practitioner,
                involvedInformation.fullName,
                involvedInformation.email,
                involvedInformation.description)
        // Return updated practitioner
        return ControllerUtil.objectToString(updatedPractitioner)
    }

    /**
     * Start a new practitioner session
     * @return id/index for started session
     */
    @Suppress("UNUSED_PARAMETER")
    private fun startSession(request: Request, response: Response): String {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        // Get selected geolocation, discipline and intention
        val startSessionInformation: StartSessionInformation =
                ControllerUtil.stringToObject(request.body(), StartSessionInformation::class.java)
        // Start a session
        val session = mService.startSession(
                practitioner,
                SessionDBO(
                        geolocation = startSessionInformation.geolocation,
                        discipline = startSessionInformation.discipline,
                        intention = startSessionInformation.intention))
        // Return the started session
        return ControllerUtil.objectToString(session)
    }

    /**
     * End an ongoing session
     * @return practitioner with the updated session
     */
    @Suppress("UNUSED_PARAMETER")
    private fun endSession(request: Request, response: Response): String {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        val contributionPoints: Long = request.params(":contributionpoints").toLong()
        // Stop the last session for practitioner with the total gathered contributionPoints
        // Return the updated practitioner
        return ControllerUtil.objectToString(mService.endSession(practitioner._id ?: "", contributionPoints))
    }

    /**
     * Start a session by joining a circle
     */
    @Suppress("UNUSED_PARAMETER")
    private fun joinCircle(request: Request, response: Response): String {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        val circleId: String = request.params(":circleid")
        // Get selected geolocation, discipline and intention
        val sessionInfo: StartSessionInformation =
                ControllerUtil.stringToObject(request.body(), StartSessionInformation::class.java)

        val circle: CircleDBO = getActiveCircleFromDatabase(circleId, mService)
        // Validate
        validatePractitionerCanAffordToJoin(practitioner, circle.minimumSpiritContribution)

        // Create a session
        val session = SessionDBO(
                geolocation = sessionInfo.geolocation,
                discipline = sessionInfo.discipline,
                intention = sessionInfo.intention,
                startTime = LocalDateTime.now(),
                circle = circle)
        // Add session to practitioner
        return ControllerUtil.objectToString(mService.startSession(practitioner, session))
    }

    /**
     * Return practitioners spirit bank log history
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getSpiritBankHistory(request: Request, response: Response): String {
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        // Return the list
        return ControllerUtil.objectListToString(practitioner.spiritBankLog)
    }

    /**
     * If practitioners spiritBankLog has less points than then spiritContributionCost then throw a 400
     */
    private fun validatePractitionerCanAffordToJoin(practitioner: PractitionerDBO, spiritContributionCost: Long) {
        if (practitioner.calculateSpiritBankPointsFromLog() < spiritContributionCost) {
            Spark.halt(400, "Practitioner cannot afford to join this circle")
        }
    }

}