package agartha.site.controllers

import agartha.data.objects.IntentionDBO
import agartha.data.objects.SettingsDBO
import agartha.data.services.IBaseService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Spark.get

/**
 * Purpose of this file is handling API requests for settings
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
class SettingController {
    val mService: IBaseService<SettingsDBO>
    // For mapping objects to string
    val mMapper = jacksonObjectMapper()

    constructor(service: IBaseService<SettingsDBO>) {
        mService = service

        get("/settings") { _, _ ->
            val list = mService.getAll()
            //
            if (list.isNotEmpty()) {
                mMapper.convertValue(list[0], SettingsDBO::class.java)
            } else {
                mService.insert(getDefaultSettings())
            }
        }
    }

    /**
     * Create default settings if non exists in database
     * @return default settings
     */
    private fun getDefaultSettings(): SettingsDBO {
        return SettingsDBO(
                listOf(
                        IntentionDBO("WELLBEING", "This is the wish for Restoration of the optimal state of the receiver, at any level the sender wishes- physical, emotional, mental, energetic, or spiritual. Covers anything from a simple physical injury to soul wounds."),
                        IntentionDBO("HARMONY", "Harmony contains the aspiration towards peace, but activates and uplifts it. This is understood to contain all possibilities for peace and both Inner and Outer, from personal to interpersonal to international."),
                        IntentionDBO("FREEDOM", "This is the ideal human condition unrestricted, and includes both “freedom TO” and “freedom FROM”. Freedom to think, express, move, act. Freedom from suffering, censorship,oppression, imprisonment, addiction."),
                        IntentionDBO("EMPOWERMENT", "People want to feel capable, confident, and free to do as they truly desire, and not to do as they don’t. This includes simple profound power of yes and of no."),
                        IntentionDBO("RESOLUTION", "Similar to peace, but with a felt sense of positive completion. Resolution of challenging situations, conflicts, disagreements, misunderstanding, and internal personal issues and experiences."),
                        IntentionDBO("EMPATHY", "We are inherently and fundamentally connected to one another. Joys are grown, and burdens lightened, through sharing."),
                        IntentionDBO("ABUNDANCE", "Abundance can be understood as an internal experience of deep sufficiency, or in a more mundane outward sense as what we call “wealth”. Many people with great material worth live an experience of internal poverty and hunger for more. Others have very little outwardly and experience themselves as having plenty. Enough to take great care of oneself and those one loves?"),
                        IntentionDBO("LOVE", "Whether romantic, familial, or unconditional, this embodies one of the greatest sources of basic human joy as well as one of our highest possible aspirations. We do our best to maximize heartfelt appreciation and to minimize judgement."),
                        IntentionDBO("CELEBRATION", "Everyone wants these things and feels great when extending them to or sharing them with others. Can be for achievements and milestones of any size. Births, birthday, graduation, promotion, wedding, anniversary, completion."),
                        IntentionDBO("TRANSFORMATION", "The only constant in life is change. How we handle change is one of the most powerful factors in our experience of life. Let’s help one another do our best.")
                ))
    }
}
