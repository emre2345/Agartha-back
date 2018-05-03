package agartha.data.services

import agartha.data.objects.DisciplineDBO
import agartha.data.objects.IntentionDBO
import agartha.data.objects.PracticeDBO
import agartha.data.objects.SettingsDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Purpose of this class is test Settings service
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
class SettingsServiceTest : DatabaseHandler() {
    private val settingsOne = SettingsDBO(
            intentions = mutableListOf(IntentionDBO("The Title", "The Description")),
            disciplines = listOf(DisciplineDBO("The title", listOf(PracticeDBO("The Title"))))
    )
    private val settingsTwo = SettingsDBO(
            intentions = mutableListOf(
                    IntentionDBO("Intention title 1", "The Description"),
                    IntentionDBO("Intention title 2", "The Description")
            ),
            disciplines = listOf(
                    DisciplineDBO("Discipline title 1",
                            listOf( PracticeDBO("Practice title 1"),
                                    PracticeDBO("Practice title 2"))
            ),
                    DisciplineDBO("Discipline title 1",
                            listOf( PracticeDBO("Practice title 1"),
                                    PracticeDBO("Practice title 2"))
            ))
    )


    /**
     *
     */
    @Before
    fun setupBeforeFunctions() {
        dropCollection(CollectionNames.SETTINGS_SERVICE)
    }

    /**
     * Insert
     */
    @Test
    fun settingService_insert_collectionSize1() {
        val settings = SettingsService().insert(settingsOne)
        val allSettings = SettingsService().getAll()
        assertThat(allSettings.size).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun settingService_insert_insertedReturned() {
        val settings = SettingsService().insert(settingsOne)
        assertThat(settings._id).isNotNull()
    }

    /**
     *
     */
    @Test
    fun settingService_mutipleInsert_onlyOneGetsSaved() {
        SettingsService().insert(SettingsDBO(intentions = mutableListOf(
                IntentionDBO("The Title 1", "The Description 1"))))
        SettingsService().insert(SettingsDBO(intentions = mutableListOf(
                IntentionDBO("The Title 2", "The Description 2"))))
        SettingsService().insert(SettingsDBO(intentions = mutableListOf(
                IntentionDBO("The Title 3", "The Description 3"))))

        val allSettings = SettingsService().getAll()
        assertThat(allSettings.size).isEqualTo(1)
    }

    /**
     * intentions
     */
    @Test
    fun settingsService_intentionsTitle_match() {
        val settings = SettingsService().insert(settingsTwo)
        assertThat(settings.intentions.first().title).isEqualTo("Intention title 1")
    }

    /**
     *
     */
    @Test
    fun settingService_practicesTitle_match() {
        val settings = SettingsService().insert(settingsTwo)
        assertThat(settings.disciplines.first().title).isEqualTo("Discipline title 1")
    }

    /**
     * addIntention
     */
    @Test
    fun settingService_addIntention_updatedIntentionsList() {
        val newIntention = IntentionDBO("DOGS", "Description about dogs")
        val updatedSettings = SettingsService().addIntention(newIntention)
        val newestSettings = SettingsService().getAll()[0].intentions
        assertThat(updatedSettings.intentions).isEqualTo(newestSettings)
    }
}