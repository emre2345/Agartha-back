package agartha.site.controllers

import agartha.common.config.Settings.Companion.ADMIN_PASS_PHRASE
import agartha.data.objects.*
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.controllers.utils.DevGeolocationSelect
import agartha.site.controllers.utils.SetupUtil
import spark.Request
import spark.Response
import spark.Spark

/**
 * Purpose of this file is handling administration page for when app is in dev/test
 *
 * TODO: These API requests will manipulate production Database.
 * TODO: Remove this API before production to avoid test data in production database
 *
 * Created by Jorgen Andersson on 2018-05-30.
 */
class AdminController(private val mService: IPractitionerService, private val settings: SettingsDBO?) {

    // Generate a list of geolocation to random from
    private val geolocations = DevGeolocationSelect.values().map { it.geolocationDBO }
    // Get or generate a settings object to random disciplines and intentions from
    private val safeSettings = settings ?: SettingsDBO(
            intentions = SetupUtil.getDefaultIntentions(),
            disciplines = SetupUtil.getDefaultDisciplines())

    init {
        Spark.path("/admin") {
            // Validate that a pass phrase exists in body
            Spark.before("/*", {request: Request, _ ->
                val body = request.body() ?: ""

                if (body != ADMIN_PASS_PHRASE) {
                    Spark.halt(401, "Unauthorized")
                }
            })

            // All API must have type POST to be able to have a body
            // Validate pass Phrase
            Spark.post("/auth", { _, _ ->
                "true"
            })
            // Get all practitioners from data source
            Spark.post("/practitioners", ::getPractitioners)
            // Generate [COUNT] number of new practitioners
            Spark.post("/generate/:count", ::generatePractitioners)
            // Add Session to existing practitioner
            Spark.post("/session/add/:id/:discipline/:intention", ::addSession)
            // Remove all practitioners
            Spark.post("/remove/all", ::removeAll)
            // Remove all generated practitioners
            Spark.post("/remove/generated", ::removeGenerated)
            // Remove a practitioner
            Spark.post("/remove/practitioner/:userid", ::removePractitioner)
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
                                                    intention = getRandomIntention().title)),
                                    description = "Generated Practitioner")))
        }

        // Return list of inserted practitioners
        return ControllerUtil.objectListToString(practitioners)
    }

    /**
     * Add/Start a session for an existing practitioner
     */
    private fun addSession(request: Request, response: Response): String {
        val userId = request.params(":id")
        val discipline = request.params(":discipline")
        val intention = request.params(":intention")

        var practitioner = mService.getById(userId)
        if (practitioner != null) {
            val session = mService.startSession(
                    practitionerId = userId,
                    geolocation = getRandomGeolocation(),
                    disciplineName = if (discipline.startsWith("random", true)) getRandomDiscipline().title else discipline,
                    intentionName = if (intention.startsWith("random", true)) getRandomIntention().title else intention)

            return ControllerUtil.objectToString(session)
        }

        response.status(400)
        return "Practitioner id $userId does not exist in database"
    }

    /**
     * Remove all practitioners
     */
    private fun removeAll(request: Request, response: Response): String {
        return "${mService.removeAll()}"
    }

    /**
     * Remove all generated practitioners
     */
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
        val userId: String = request.params(":userid")
        // Remove by id
        return ControllerUtil.objectToString(mService.removeById(userId))
    }


    /**
     * Get a random Geolocation
     * @return Geolocation
     */
    private fun getRandomGeolocation(): GeolocationDBO {
        return geolocations.shuffled().take(1)[0]
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
     * @return Settings
     */
    private fun getRandomIntention(): IntentionDBO {
        return safeSettings.intentions.shuffled().take(1)[0]
    }
}
