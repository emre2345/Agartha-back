package agartha.data.services

import agartha.data.objects.DisciplineDBO
import agartha.data.objects.IntentionDBO
import agartha.data.objects.SettingsDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Purpose of this class is test Settings service
 *
 * Created by Jorgen Andersson on 2018-04-12.
 */
class SettingsServiceTest : DatabaseHandler() {
    private val settingsOne = SettingsDBO(
            intentions = mutableListOf(IntentionDBO("The Title", "The Description")),
            disciplines = listOf(DisciplineDBO("The title", "The Description"))
    )
    private val settingsTwo = SettingsDBO(
            intentions = mutableListOf(
                    IntentionDBO("Intention title 1", "The Description 1"),
                    IntentionDBO("Intention title 2", "The Description 2")
            ),
            disciplines = listOf(
                    DisciplineDBO("Discipline title 1", "The Description 1"),
                    DisciplineDBO("Discipline title 2", "The Description 2"))
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
        SettingsService().insert(settingsOne)
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

    @Test
    fun settingService_getOneExisting_notNull() {
        val settings = SettingsService().insert(settingsOne)
        val getObject = SettingsService().getById(settings._id ?: "")
        assertThat(getObject).isNotNull()
    }

    @Test
    fun settingService_getOneNonExisting_null() {
        val getObject = SettingsService().getById("ThisIdDoesNotExistInDB")
        assertThat(getObject).isNull()
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
    fun settingService_dicsiplineTitle_match() {
        val settings = SettingsService().insert(settingsTwo)
        assertThat(settings.disciplines.first().title).isEqualTo("Discipline title 1")
    }

    @Test
    fun settingService_dicsiplineDescription_match() {
        val settings = SettingsService().insert(settingsTwo)
        assertThat(settings.disciplines.first().description).isEqualTo("The Description 1")
    }

    /**
     * addIntention
     */
    @Test
    fun settingService_addIntention_updatedIntentionsList() {
        SettingsService().insert(settingsTwo)
        val newIntention = IntentionDBO("DOGS", "Description about dogs")
        val updatedSettings = SettingsService().addIntention(newIntention)
        val newestSettings = SettingsService().getAll()[0].intentions
        assertThat(updatedSettings.intentions).isEqualTo(newestSettings)
    }
}