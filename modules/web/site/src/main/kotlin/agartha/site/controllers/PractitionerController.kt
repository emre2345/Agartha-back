package agartha.site.controllers

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.controllers.utils.EndSession
import agartha.site.controllers.utils.ErrorMessagesEnum
import agartha.site.controllers.utils.ReqArgument
import agartha.site.objects.request.PractitionerInvolvedInformation
import agartha.site.objects.request.StartSessionInformation
import agartha.site.objects.response.PractitionerReport
import io.schinzel.basicutils.configvar.IConfigVar
import spark.Request
import spark.Response
import spark.Spark
import spark.Spark.halt

/**
 * Purpose of this file is handling API requests for practitioner's sessions
 *
 * Created by Jorgen Andersson on 2018-04-09.
 *
 * @param mService object for reading data from data source
 */
class PractitionerController(private val mService: IPractitionerService, private val config: IConfigVar) : AbstractController(mService) {

    init {
        // API path for session
        Spark.path("/practitioner") {

            //
            // When creating new practitioner send a customized id from device - return info for the practitioner
            Spark.get("/create/${ReqArgument.DEVICE_ID.value}", ::createPractitioner)
            //
            //
            Spark.before("/${ReqArgument.PRACTITIONER_ID.value}", ::validatePractitioner)
            // Where practitionerId has been set - return info for the practitioner and about practitioner
            Spark.get("/${ReqArgument.PRACTITIONER_ID.value}", ::getInformation)
            // Update practitioner data
            Spark.post("/${ReqArgument.PRACTITIONER_ID.value}", ::updatePractitioner)
            //
            // Start a Session
            Spark.before("/session/start/${ReqArgument.PRACTITIONER_ID.value}", ::validatePractitioner)
            Spark.before("/session/start/${ReqArgument.PRACTITIONER_ID.value}", ::validateSessionStart)
            Spark.post("/session/start/${ReqArgument.PRACTITIONER_ID.value}", ::startSession)
            //
            // End a Session
            Spark.before("/session/end/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.POINTS.value}", ::validatePractitioner)
            Spark.post("/session/end/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.POINTS.value}", ::endSession)
            //
            // Start practicing by Joining a Circle
            Spark.before("/circle/join/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::validatePractitioner)
            Spark.before("/circle/join/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::validateSessionStart)
            Spark.before("/circle/join/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::validateActiveCircle)
            Spark.before("/circle/join/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::validateJoinCircle)
            Spark.post("/circle/join/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::joinCircle)
            //
            // Registered practitioner interest to a circle
            Spark.before("/circle/register/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::validatePractitioner)
            Spark.before("/circle/register/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::validateCircle)
            Spark.post("/circle/register/${ReqArgument.PRACTITIONER_ID.value}/${ReqArgument.CIRCLE_ID.value}", ::registerToCircle)
            //
            // Get practitioners spiritBankHistory
            Spark.before("/spiritbankhistory/${ReqArgument.PRACTITIONER_ID.value}", ::validatePractitioner)
            Spark.get("/spiritbankhistory/${ReqArgument.PRACTITIONER_ID.value}", ::getSpiritBankHistory)
            //
            // Get practitioner from email address
            Spark.get("/find/email/${ReqArgument.PRACTITIONER_EMAIL.value}", ::getFromEmail)
            //
            // Donate points to a practitioner
            Spark.before("/donate/${ReqArgument.FROM_ID.value}/${ReqArgument.TO_ID.value}/${ReqArgument.POINTS.value}", ::validateDonate)
            Spark.post("/donate/${ReqArgument.FROM_ID.value}/${ReqArgument.TO_ID.value}/${ReqArgument.POINTS.value}", ::donatePoints)
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
     * Validate that practitioner can join circle
     */
    @Suppress("UNUSED_PARAMETER")
    private fun validateJoinCircle(request: Request, response: Response) {
        // Get the original circle user wants to join
        val circle = getCircle(request, true)
        val sessionInfo: StartSessionInformation =
                ControllerUtil.stringToObject(request.body(), StartSessionInformation::class.java)
        //
        // Validate that the practitioner can afford joining circle
        if (getPractitioner(request).calculateSpiritBankPointsFromLog() < circle.minimumSpiritContribution) {
            Spark.halt(400, ErrorMessagesEnum.PRACTITIONER_NOT_AFFORD_CIRCLE.name)
        }
        //
        // Validate that circle is active and the selected discipline and intention matching
        if (circle._id.isEmpty()) {
            halt(400, ErrorMessagesEnum.CIRCLE_NOT_ACTIVE_OR_EXIST.message)
        } else {
            // Only try to match if circle has disciplines
            if (circle.disciplines.isNotEmpty()) {
                if (circle.disciplines.find { it.title == sessionInfo.discipline } == null) {
                    Spark.halt(400, ErrorMessagesEnum.DISCIPLINE_NOT_MATCHED.message)
                }
            }
            // Only try to match if circle has intentions
            if (circle.intentions.isNotEmpty()) {
                if (circle.intentions.find { it.title == sessionInfo.intention } == null) {
                    Spark.halt(400, ErrorMessagesEnum.INTENTION_NOT_MATCHED.message)
                }
            }
        }
    }

    /**
     * Validate that a practitioner can donate to another practitioner
     */
    @Suppress("UNUSED_PARAMETER")
    private fun validateDonate(request: Request, response: Response) {
        val fromPractitioner = mService.getById(request.params(ReqArgument.FROM_ID.value)) ?: PractitionerDBO(_id = "")
        val toPractitioner = mService.getById(request.params(ReqArgument.TO_ID.value)) ?: PractitionerDBO(_id = "")
        val points = request.params(ReqArgument.POINTS.value).toLong()

        // Either of practitioner is missing in database
        if (fromPractitioner._id.isNullOrEmpty() || toPractitioner._id.isNullOrEmpty()) {
            halt(400, ErrorMessagesEnum.PRACTITIONER_ID_INCORRECT.message)
        } else {
            // Points is zero or less
            if (points <= 0) {
                halt(400, ErrorMessagesEnum.NEGATIVE_INTEGER_VALUE.message)
            }
            // Giver has not enough points to give
            if(fromPractitioner.calculateSpiritBankPointsFromLog() < points) {
                halt(400, ErrorMessagesEnum.PRACTITIONER_OUT_OF_FUNDS.message)
            }
        }
    }

    /**
     * Create new practitioner with customized id
     * Get information about current practitioner
     * @return Object with general information
     */
    @Suppress("UNUSED_PARAMETER")
    private fun createPractitioner(request: Request, response: Response): String {
        // Get unique identifier for practitioner from request parameters
        val uniqueIdentifier: String = request.params(ReqArgument.DEVICE_ID.value)
        // Get practitioner from data source and set Id to the identifier
        val practitioner = mService.insert(PractitionerDBO(_id = uniqueIdentifier))
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
        val sessionInfo: StartSessionInformation =
                ControllerUtil.stringToObject(request.body(), StartSessionInformation::class.java)
        // Start a session
        val session = mService.startSession(
                practitioner,
                SessionDBO(
                        geolocation = sessionInfo.geolocation,
                        discipline = sessionInfo.discipline,
                        intention = sessionInfo.intention))
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
        // object to calculate points to be awarded to practitioner
        val sessionEnd = EndSession(
                practitioner,
                mService.getAll(),
                request.params(ReqArgument.POINTS.value).toLong(),
                config.getValue("A_CONTRIBUTION_PERCENT").toLong())
        //
        mService.endSession(
                practitionerId = sessionEnd.practitionerId,
                contributionPoints = sessionEnd.contributionPoints)
        mService.endCircle(
                practitionerId = sessionEnd.practitionerId,
                creator = sessionEnd.creator,
                circle = sessionEnd.circle,
                contributionPoints = sessionEnd.circlePoints)
        return ControllerUtil.objectToString(mService.getById(sessionEnd.practitionerId))
    }

    /**
     * Start a session by joining a circle
     */
    @Suppress("UNUSED_PARAMETER")
    private fun joinCircle(request: Request, response: Response): String {
        // Get from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        val circle: CircleDBO = getCircle(request, true)
        // Get selected geolocation, discipline and intention
        val sessionInfo: StartSessionInformation =
                ControllerUtil.stringToObject(request.body(), StartSessionInformation::class.java)
        // Create a session
        val session = SessionDBO(
                geolocation = sessionInfo.geolocation,
                discipline = sessionInfo.discipline,
                intention = sessionInfo.intention,
                startTime = DateTimeFormat.localDateTimeUTC(),
                circle = circle)
        // Add session to practitioner
        return ControllerUtil.objectToString(mService.startSession(practitioner, session))
    }

    /**
     * Register practitioner's interest in an circle
     */
    @Suppress("UNUSED_PARAMETER")
    private fun registerToCircle(request: Request, response: Response): String {
        // Get from data source
        val practitioner: PractitionerDBO = getPractitioner(request)
        val circle: CircleDBO = getCircle(request, false)
        // Add session to practitioner
        return ControllerUtil.objectToString(mService.addRegisteredCircle(practitioner._id!!, circle._id))
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

    @Suppress("UNUSED_PARAMETER")
    private fun getFromEmail(request: Request, response: Response): String {
        val email: String = request.params(ReqArgument.PRACTITIONER_EMAIL.value)
        val practitioner = mService
                .getAll()
                .firstOrNull { it.email == email }
        if (practitioner == null) {
            halt(404, ErrorMessagesEnum.EMAIL_NOT_FOUND.message)
        }
        return ControllerUtil.objectToString(practitioner)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun donatePoints(request: Request, response: Response): String {
        val fromPractitioner = mService.getById(request.params(ReqArgument.FROM_ID.value)) ?: PractitionerDBO(_id = "")
        val toPractitioner = mService.getById(request.params(ReqArgument.TO_ID.value)) ?: PractitionerDBO(_id = "")
        val points = request.params(ReqArgument.POINTS.value).toLong()
        // Store
        mService.donatePoints(
                fromPractitionerId = fromPractitioner._id ?: "",
                toPractitionerId = toPractitioner._id ?: "",
                points = points)

        return "true"
    }

}