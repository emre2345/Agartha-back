package agartha.data.objects

import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * Purpose of this class is to test the SettingsSBO
 */
class SettingsDBOTest {
    val settings = SettingsDBO(
            intentions = mutableListOf(
                    IntentionDBO("LOVE", "description of love"),
                    IntentionDBO("HORSES", "description of horses")),
            disciplines = listOf(DisciplineDBO("Yoga", "description of Yoga")),
            companionMinutes = 1,
            companionGoalMinutes = 2
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
    fun settings_firstDisciplineDescription_yoga() {
        Assertions.assertThat(settings.disciplines[0].description).isEqualTo("description of Yoga")
    }

    /***************************
     * Variable - companionDay *
     ***************************/
    @Test
    fun settings_companionMinutes_1() {
        Assertions.assertThat(settings.companionMinutes).isEqualTo(1)
    }

    /*********************************
     * Variable - companionGoalMinutes *
     *********************************/
    @Test
    fun settings_companionGoalMinutes_2() {
        Assertions.assertThat(settings.companionGoalMinutes).isEqualTo(2)
    }

    /************************
     * Variable - languages *
     ************************/
    @Test
    fun settings_languagesSize_6() {
        Assertions.assertThat(settings.languages.size).isEqualTo(6)
    }

}