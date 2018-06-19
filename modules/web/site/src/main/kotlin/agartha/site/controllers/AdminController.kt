package agartha.site.controllers

import agartha.data.objects.*
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.controllers.utils.DevGeolocationSelect
import agartha.site.controllers.utils.SetupUtil
import io.schinzel.basicutils.configvar.IConfigVar
import spark.Request
import spark.Response
import spark.Spark
import java.time.LocalDateTime
import java.util.*

/**
 * Purpose of this file is handling administration page for when app is in dev/test
 *
 * TODO: These API requests will manipulate production Database.
 * TODO: Remove this API before production to avoid test data in production database
 *
 * Created by Jorgen Andersson on 2018-05-30.
 */
class AdminController(private val mService: IPractitionerService,
                      private val config: IConfigVar,
                      settings: SettingsDBO?) {

    // Generate a list of geolocation to random from
    private val geoLocations = DevGeolocationSelect.values().map { it.geolocationDBO }
    // Get or generate a settings object to random disciplines and intentions from
    private val safeSettings = settings ?: SettingsDBO(
            intentions = SetupUtil.getDefaultIntentions(),
            disciplines = SetupUtil.getDefaultDisciplines())
    // Authentication Pass Phrase, read from config variable (.env file locally or Heroku Settings), Default value for tests
    private val passPhrase: String = config.getValue("A_PASS_PHRASE")

    init {
        Spark.path("/admin") {
            // Validate that a pass phrase exists in body
            Spark.before("/*") { request: Request, _ ->
                val body = request.body() ?: ""

                if (body != passPhrase) {
                    Spark.halt(401, "Unauthorized")
                }
            }

            // All API must have type POST to be able to have a body
            // Validate pass Phrase
            Spark.post("/auth") { _, _ ->
                "true"
            }

            // Get all practitioners from data source
            Spark.post("/practitioners", ::getPractitioners)
            // Generate [COUNT] number of new practitioners
            Spark.post("/generate/:count", ::generatePractitioners)
            // Add Session to existing practitioner
            Spark.post("/session/add/:userid/:discipline/:intention", ::addSession)
            // Remove all practitioners
            Spark.post("/remove/all", ::removeAll)
            // Remove all generated practitioners
            Spark.post("/remove/generated", ::removeGenerated)
            // Remove a practitioner
            Spark.post("/remove/practitioner/:userid", ::removePractitioner)
            // Remove a circle
            Spark.post("/remove/circle/:circleid", ::removeCircle)
        }
    }

    /**
     * Function to get all practitioners
     */
    @Suppress("UNUSED_PARAMETER")
    private fun getPractitioners(request: Request, response: Response): String {
        return ControllerUtil.objectListToString(mService.getAll())
    }

    /**
     * Function for randomizing argument number of new practitioners
     */
    @Suppress("UNUSED_PARAMETER")
    private fun generatePractitioners(request: Request, response: Response): String {
        // Get number of practitioners to generate, default (if missing) is zero
        val count: Long = request.params(":count").toLongOrNull() ?: 0
        // List of inserted practitioners (to be returned)
        val practitioners = mutableListOf<PractitionerDBO>()

        for (index in 1..count) {

            practitioners.add(
                    mService.insert(
                            PractitionerDBO(
                                    sessions = listOf(
                                            SessionDBO(
                                                    geolocation = getRandomGeolocation(),
                                                    discipline = getRandomDiscipline().title,
                                                    intention = getRandomIntention().title,
                                                    startTime = getShuffledStartTime())),
                                    description = "Generated Practitioner")))
        }

        // Return list of inserted practitioners
        return ControllerUtil.objectListToString(practitioners)
    }

    /**
     * Add/Start a session for an existing practitioner
     */
    private fun addSession(request: Request, response: Response): String {
        val practitionerId = request.params(":userid")
        val discipline = request.params(":discipline")
        val intention = request.params(":intention")

        val practitioner = mService.getById(practitionerId)
        if (practitioner != null) {
            val session = mService.startSession(
                    session = SessionDBO(
                    geolocation = getRandomGeolocation(),
                    discipline = if (discipline.startsWith("random", true)) getRandomDiscipline().title else discipline,
                    intention = if (intention.startsWith("random", true)) getRandomIntention().title else intention),
                    practitioner = practitioner)

            return ControllerUtil.objectToString(session)
        }

        response.status(400)
        return "Practitioner id $practitionerId does not exist in database"
    }

    /**
     * Remove all practitioners
     */
    @Suppress("UNUSED_PARAMETER")
    private fun removeAll(request: Request, response: Response): String {
        return "${mService.removeAll()}"
    }

    /**
     * Remove all generated practitioners
     */
    @Suppress("UNUSED_PARAMETER")
    private fun removeGenerated(request: Request, response: Response): String {
        return ControllerUtil.objectListToString(mService.removeGenerated())
    }


    /**
     * Remove a practitioner
     * @return true if everything went fine
     */
    @Suppress("UNUSED_PARAMETER")
    private fun removePractitioner(request: Request, response: Response): String {
        // Get current userid
        val practitionerId: String = request.params(":userid")
        // Remove by id
        return ControllerUtil.objectToString(mService.removeById(practitionerId))
    }

    /**
     * Remove a circle
     * @return true if everything went fine
     */
    @Suppress("UNUSED_PARAMETER")
    private fun removeCircle(request: Request, response: Response): String {
        // Get circleid
        val circleId: String = request.params(":circleid")
        // Find practitioner whom created this circle
        val practitioner: PractitionerDBO? = mService
                .getAll()
                .filter {
                    it.circles.filter { it._id == circleId }.isNotEmpty()
                }
                .firstOrNull()
        // Remove the circle
        return ControllerUtil.objectToString(
                mService.removeCircleById(
                        practitionerId = practitioner?._id ?: "", circleId = circleId))
    }

    /**
     * Get a random Geolocation
     * @return Geolocation
     */
    private fun getRandomGeolocation(): GeolocationDBO {
        return geoLocations.shuffled().take(1)[0]
    }

    /**
     * Get a random Discipline amongst the settings
     * @return Discipline
     */
    private fun getRandomDiscipline(): DisciplineDBO {
        return safeSettings.disciplines.shuffled().take(1)[0]
    }

    /**
     * Get a random Intention amongst the settings
     * @return IntentionDBO
     */
    private fun getRandomIntention(): IntentionDBO {
        return safeSettings.intentions.shuffled().take(1)[0]
    }

    /**
     * Get a LocalDateTime that is between now and 60 minutes ago
     * @return LocalDateTime
     */
    private fun getShuffledStartTime(): LocalDateTime {
        val minutes: Long = (Random().nextInt(60)).toLong()
        return LocalDateTime.now().minusMinutes(minutes)
    }
}
