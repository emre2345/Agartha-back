package agartha.site.controllers.mocks

import agartha.data.objects.IntentionDBO
import agartha.data.objects.SettingsDBO
import agartha.data.services.ISettingsService
import java.util.*

/**
 * Mocked service for settings for testing SettingController
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
class MockedSettingsService : ISettingsService {
    private val settingsList: MutableList<SettingsDBO> = mutableListOf()

    override fun insert(item: SettingsDBO): SettingsDBO {
        if (settingsList.isEmpty()) {
            val uuid = UUID.randomUUID()
            val createdItem = SettingsDBO(uuid.toString(), item.intentions, item.disciplines)
            settingsList.add(createdItem)
            return createdItem
        }
        return settingsList.first()
    }

    override fun addIntention(item: IntentionDBO): SettingsDBO {
        val settingsObject = settingsList[0]
        val copyIntentionMutableList: MutableList<IntentionDBO> = settingsObject.intentions.toMutableList()
        copyIntentionMutableList.add(item)
        // Create a updatedSettingsDBO with all the same variables as the old one except the new intentionsList
        return SettingsDBO(settingsObject._id, copyIntentionMutableList, settingsObject.disciplines, settingsObject.companionMinutes, settingsObject.companionGoalMinutes)
    }

    override fun getAll(): List<SettingsDBO> {
        return settingsList
    }

    override fun getById(id: String): SettingsDBO? {
        TODO("Never is or will be used")
    }

    /**
     * Clear the storage
     */
    fun clear() {
        settingsList.clear()
    }

}