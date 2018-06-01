package agartha.site.controllers.utils

import agartha.data.objects.DisciplineDBO
import agartha.data.objects.IntentionDBO

/**
 * Purpose of this file is holding setup stuff used for repopulating database when/if empty
 *
 * Created by Jorgen Andersson on 2018-06-01.
 */
class SetupUtil {

    companion object {
        fun getDefaultIntentions(): List<IntentionDBO> {
            return listOf(
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


        fun getDefaultDisciplines(): List<DisciplineDBO> {
            return listOf(
                    DisciplineDBO("Readings", "Astrology, tarot, psychic, iridology"),
                    DisciplineDBO("Meditation", "Sitting, walking, mantra, moving, healing, visualization"),
                    DisciplineDBO("Wellness", "Chiropractic, osteopathic, massage, energy, healing, attunement"),
                    DisciplineDBO("Movement", "Dance, feldenkreis, contact improv, 5 rhythms, ecstatic, mevlevi"),
                    DisciplineDBO("Martial arts", "Tai chi, qigong, aikido, karate, tae kwon do, krav maga"),
                    DisciplineDBO("Physical exercise", "Running, yoga, crossfit, pilates, weight-training"),
                    DisciplineDBO("Creative expression", "Singing, music making/listening, drawing, art, painting, dance, writing"),
                    DisciplineDBO("Outdoor activity", "Hiking, biking, surfing, kite-surfing, river-tracing, bouldering, rock-climbing"),
                    DisciplineDBO("Personal growth", "Therapy, coaching, goal-setting, leadership training"),
                    DisciplineDBO("Meals", "Individual, family, social, special occasion, date, holiday, fasting"))
        }
    }
}