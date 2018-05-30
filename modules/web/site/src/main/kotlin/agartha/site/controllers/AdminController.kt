package agartha.site.controllers

import agartha.data.objects.*
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.controllers.utils.DevGeolocationSelect
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
            intentions = listOf(
                    IntentionDBO("Wellbeing", "This is the wish for Restoration of the optimal state of the receiver, at any level the sender wishes- physical, emotional, mental, energetic, or spiritual. Covers anything from a simple physical injury to soul wounds."),
                    IntentionDBO("Harmony", "Harmony contains the aspiration towards peace, but activates and uplifts it. This is understood to contain all possibilities for peace and both Inner and Outer, from personal to interpersonal to international."),
                    IntentionDBO("Freedom", "This is the ideal human condition unrestricted, and includes both “freedom TO” and “freedom FROM”. Freedom to think, express, move, act. Freedom from suffering, censorship,oppression, imprisonment, addiction."),
                    IntentionDBO("Empowerment", "People want to feel capable, confident, and free to do as they truly desire, and not to do as they don’t. This includes simple profound power of yes and of no."),
                    IntentionDBO("Resolution", "Similar to peace, but with a felt sense of positive completion. Resolution of challenging situations, conflicts, disagreements, misunderstanding, and internal personal issues and experiences."),
                    IntentionDBO("Empathy", "We are inherently and fundamentally connected to one another. Joys are grown, and burdens lightened, through sharing."),
                    IntentionDBO("Abundance", "Abundance can be understood as an internal experience of deep sufficiency, or in a more mundane outward sense as what we call “wealth”. Many people with great material worth live an experience of internal poverty and hunger for more. Others have very little outwardly and experience themselves as having plenty. Enough to take great care of oneself and those one loves?"),
                    IntentionDBO("Love", "Whether romantic, familial, or unconditional, this embodies one of the greatest sources of basic human joy as well as one of our highest possible aspirations. We do our best to maximize heartfelt appreciation and to minimize judgement."),
                    IntentionDBO("Celebration", "Everyone wants these things and feels great when extending them to or sharing them with others. Can be for achievements and milestones of any size. Births, birthday, graduation, promotion, wedding, anniversary, completion."),
                    IntentionDBO("Transformation", "The only constant in life is change. How we handle change is one of the most powerful factors in our experience of life. Let’s help one another do our best.")
            ),
            disciplines = listOf(
                    // These are real, from Kim
                    DisciplineDBO("Meditation"),
                    DisciplineDBO("Yoga"),
                    // These are made up by DP
                    DisciplineDBO("Physical wellness"),
                    DisciplineDBO("Divination"),
                    DisciplineDBO("Martial and internal arts"),
                    DisciplineDBO("Self-expression"),
                    DisciplineDBO("Transformative technology"),
                    DisciplineDBO("Psychic realm"))
    )

    init {
        Spark.path("/admin") {
            // Get all practitioners from data source
            Spark.get("/practitioners", ::getPractitioners)
            // Generate [COUNT] number of new practitioners
            Spark.post("/generate/:count", ::generatePractitioners)
            // Add Session to existing practitioner
            Spark.post("/session/add/:id/:discipline/:intention", ::addSession)
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
