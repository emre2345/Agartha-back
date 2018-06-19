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
            Spark.get("", ::getInformation)
            //
            // Where practitionerId has been set - return info for the practitioner and about practitioner
            Spark.get("/:userid", ::getInformation)
            //
            // Update practitioner data
            Spark.post("/:userid", ::updatePractitioner)
            //
            // Start a Session
            Spark.before("/session/start/:userid", ::validateSessionStart)
            Spark.post("/session/start/:userid", ::startSession)
            //
            // End a Session
            Spark.post("/session/end/:userid/:contributionpoints", ::endSession)
            //
            // Start practicing by Joining a Circle
            Spark.before("/circle/join/:userid/:circleid", ::validateSessionStart)
            Spark.post("/circle/join/:userid/:circleid", ::joinCircle)
            //
            // Get practitioners spiritBankHistory
            Spark.get("/spiritbankhistory/:userid", ::getSpiritBankHistory)
        }
    }

    private fun validateSessionStart(request: Request, response: Response) {
        // Get selected geolocation, discipline and intention
        val sessionInfo: StartSessionInformation =
                ControllerUtil.stringToObject(request.body(), StartSessionInformation::class.java)

        if (sessionInfo.discipline.isEmpty() || sessionInfo.intention.isEmpty()) {
            halt(400, "Discipline and Intention cannot be empty")
        }
    }

    /**
     * Get information about current practitioner
     * @return Object with general information
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getInformation(request: Request, response: Response): String {
        // Get current practitionerid or generate new
        val practitionerId = getPractitionerIdFromRequest(request)
        // Get practitioner from data source
        val practitioner: PractitionerDBO = getPractitionerFromDataSource(practitionerId)
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
        val involvedInformation: PractitionerInvolvedInformation =
                ControllerUtil.stringToObject(request.body(), PractitionerInvolvedInformation::class.java)
        // Get params
        val practitionerId: String = request.params(":userid")
        // Get practitioner
        val practitioner: PractitionerDBO = getPractitionerFromDatabase(practitionerId, mService)
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
     * Get a practitioner from its practitionerId from datasource or create if it does not exists
     *
     * @param practitionerId database id for practitioner
     * @return Database representation of practitioner
     */
    private fun getPractitionerFromDataSource(practitionerId: String): PractitionerDBO {
        // If practitioner exists in database, return it otherwise create, store and return
        return mService.getById(practitionerId)
                ?: mService.insert(PractitionerDBO(practitionerId, LocalDateTime.now(), mutableListOf()))
    }

    /**
     * Start a new practitioner session
     * @return id/index for started session
     */
    @Suppress("UNUSED_PARAMETER")
    private fun startSession(request: Request, response: Response): String {
        // Get current practitionerId
        val practitionerId: String = request.params(":userid")
        // Make sure practitionerId exists in database
        val practitioner = getPractitionerFromDatabase(practitionerId, mService)
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
        // Get current practitionerId
        val practitionerId: String = request.params(":userid")
        val contributionPoints: Long = request.params(":contributionpoints").toLong()
        // Make sure practitionerId exists in database
        getPractitionerFromDatabase(practitionerId, mService)
        // Stop the last session for practitioner with the total gathered contributionPoints
        val practitioner = mService.endSession(practitionerId, contributionPoints)
        // Return the updated practitioner
        return ControllerUtil.objectToString(practitioner)
    }

    /**
     * Start a session by joining a circle
     */
    @Suppress("UNUSED_PARAMETER")
    private fun joinCircle(request: Request, response: Response): String {
        // Get params
        val practitionerId: String = getPractitionerIdFromRequest(request)
        val circleId: String = request.params(":circleid")
        // Get selected geolocation, discipline and intention
        val startSessionInformation: StartSessionInformation =
                ControllerUtil.stringToObject(request.body(), StartSessionInformation::class.java)

        // Get objects and validate they exist
        val practitioner = getPractitionerFromDatabase(practitionerId, mService)
        val circle: CircleDBO = getActiveCircleFromDatabase(circleId, mService)

        // Validate
        validateDiscipline(circle, startSessionInformation.discipline)
        validateIntention(circle, startSessionInformation.intention)
        validatePractitionerCanAffordToJoin(practitioner, circle.minimumSpiritContribution)

        // Create a session
        val session = SessionDBO(
                geolocation = startSessionInformation.geolocation,
                discipline = startSessionInformation.discipline,
                intention = startSessionInformation.intention,
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
        // Get params
        val practitionerId: String = getPractitionerIdFromRequest(request)
        // Get practitioner
        val practitioner = getPractitionerFromDatabase(practitionerId, mService)
        // Return the list
        return ControllerUtil.objectListToString(practitioner.spiritBankLog)
    }

    /**
     * Get practitioner id, and if missing create a new
     * @param request object
     * @return practitionerId as string (GUID/UUID)
     */
    private fun getPractitionerIdFromRequest(request: Request): String {
        return request.params(":userid") ?: ObjectId().toHexString()
    }

    private fun validateDiscipline(circle: CircleDBO, discipline: String) {
        if (circle.disciplines.isEmpty()) {
            return
        }

        circle.disciplines.forEach {
            if (it.title == discipline) {
                return
            }
        }

        Spark.halt(400, "Selected discipline does not match any in Circle")
    }

    private fun validateIntention(circle: CircleDBO, intention: String) {
        if (circle.intentions.isEmpty()) {
            return
        }

        circle.intentions.forEach {
            if (it.title == intention) {
                return
            }
        }

        Spark.halt(400, "Selected intention does not match any in Circle")
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