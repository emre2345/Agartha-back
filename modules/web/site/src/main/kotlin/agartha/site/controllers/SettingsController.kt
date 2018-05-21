package agartha.site.controllers

import agartha.data.objects.DisciplineDBO
import agartha.data.objects.IntentionDBO
import agartha.data.objects.SettingsDBO
import agartha.data.services.ISettingsService
import agartha.site.controllers.utils.ControllerUtil
import spark.Request
import spark.Response
import spark.Spark

/**
 * Purpose of this file is handling API requests for settings
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 *
 * @param mService object for reading data from data source
 */
class SettingsController(private val mService: ISettingsService) {

    init {
        Spark.path("/settings") {
            //
            Spark.get("") { _, _ ->
                //
                val list = mService.getAll()
                if (list.isNotEmpty()) {
                    // If Settings data source is not empty, return first item
                    ControllerUtil.objectToString(list[0])
                } else {
                    // If Settings data source is empty, get default
                    ControllerUtil.objectToString(mService.insert(getDefaultSettings()))
                }
            }
            // add Intention to the DB
            Spark.post("/intention", ::addIntention)
        }
    }


    /**
     * Adds an intention to the DBO
     * @return the added intention
     */
    private fun addIntention(request: Request, response: Response): String {
        // Get the new intention from body
        val newIntention: IntentionDBO = ControllerUtil.stringToObject(request.body(), IntentionDBO::class.java)
        // Update the database
        val updatedSettingsDBO: SettingsDBO = mService.addIntention(newIntention)
        // Return the updated SettingsDBO
        return ControllerUtil.objectToString(updatedSettingsDBO)
    }

    /**
     * Create default settings if non exists in database
     * @return default settings
     */
    private fun getDefaultSettings(): SettingsDBO {
        return SettingsDBO(
                intentions = getDefaultIntentions(),
                disciplines = getDefaultDisciplines())
    }

    /**
     * Default Intentions is settings in database is empty
     */
    private fun getDefaultIntentions(): MutableList<IntentionDBO> {
        return mutableListOf(
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
        )
    }

    /**
     * Default practices if settings in database is empty
     */
    private fun getDefaultDisciplines(): List<DisciplineDBO> {
        return listOf(
                DisciplineDBO("Meditation"),
                DisciplineDBO("Yoga"))
    }
}
