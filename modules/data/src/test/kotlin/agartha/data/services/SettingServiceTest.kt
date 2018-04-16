package agartha.data.services

import agartha.data.objects.IntentionDBO
import agartha.data.objects.SettingsDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Purpose of this class is test Settings service
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
class SettingServiceTest : DatabaseHandler() {

    /**
     *
     */
    @Before
    fun setupBeforeFunctions() {
        dropCollection(CollectionNames.SETTINGS_SERVICE)
    }

    /**
     *
     */
    @Test
    fun practitionerService_insert_collectionSize1() {
        val settings = SettingsService().insert(SettingsDBO(listOf(
                IntentionDBO("The Title", "The Description")
        )))
        val allSettings = SettingsService().getAll()
        assertThat(allSettings.size).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun practitionerService_insert_insertedReturned() {
        val settings = SettingsService().insert(SettingsDBO(listOf(
                IntentionDBO("The Title", "The Description")
        )))
        assertThat(settings.intentions.first().title).isEqualTo("The Title")
        assertThat(settings._id).isNotNull()
    }

    /**
     *
     */
    @Test
    fun practitionerService_mutipleInsert_onlyOneGetsSaved() {
        SettingsService().insert(SettingsDBO(listOf(
                IntentionDBO("The Title 1", "The Description 1"))))
        SettingsService().insert(SettingsDBO(listOf(
                IntentionDBO("The Title 2", "The Description 2"))))
        SettingsService().insert(SettingsDBO(listOf(
                IntentionDBO("The Title 3", "The Description 3"))))

        val allSettings = SettingsService().getAll()
        assertThat(allSettings.size).isEqualTo(1)
        assertThat(allSettings.first().intentions.first().title).isEqualTo("The Title 1")
    }
}