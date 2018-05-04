package agartha.data.objects

import org.assertj.core.api.Assertions
import org.junit.Test

class SettingsDBOTest {
    val settings = SettingsDBO(
            intentions = mutableListOf(
                IntentionDBO("LOVE", "description of love"),
                IntentionDBO("HORSES", "description of horses")),
            disciplines = listOf(DisciplineDBO("Yoga", listOf(PracticeDBO("Hatha")))),
            companionDays = 1,
            companionGoalHours = 2
    )
    /**************************
     * Variables - intentions *
     **************************/
    @Test
    fun settings_intentionsSize_2() {
        Assertions.assertThat(settings.intentions.size).isEqualTo(2)
    }

    /**
     *
     */
    @Test
    fun settings_firstIntentionsTitle_LOVE() {
        Assertions.assertThat(settings.intentions[0].title).isEqualTo("LOVE")
    }

    /**
     *
     */
    @Test
    fun settings_firstIntentionsDescription_LOVE() {
        Assertions.assertThat(settings.intentions[0].description).isEqualTo("description of love")
    }


    /**************************
     * Variable - disciplines *
     **************************/
    @Test
    fun settings_disciplinesSize_1() {
        Assertions.assertThat(settings.disciplines.size).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun settings_firstDisciplineTitle_yoga() {
        Assertions.assertThat(settings.disciplines[0].title).isEqualTo("Yoga")
    }

    /**
     *
     */
    @Test
    fun settings_firstDisciplinePracticeTitle_hatha() {
        Assertions.assertThat(settings.disciplines[0].practices[0].title).isEqualTo("Hatha")
    }

    /***************************
     * Variable - companionDay *
     ***************************/
    @Test
    fun settings_companionDays_1() {
        Assertions.assertThat(settings.companionDays).isEqualTo(1)
    }

    /*********************************
     * Variable - companionGoalHours *
     *********************************/
    @Test
    fun settings_companionGoalHours_2() {
        Assertions.assertThat(settings.companionGoalHours).isEqualTo(2)
    }

}