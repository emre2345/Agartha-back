package agartha.data.services

import agartha.data.objects.IntentionDBO
import agartha.data.objects.SettingsDBO

/**
 * The purpose of interface is  that will be used by the settingsService
 * Having settings in database will make it possible to change settings without packaging a new version of native app
 * because the app will read settings via API on start and therefore always have latest settings
 */
interface ISettingsService : IBaseService<SettingsDBO>{
    /**
     * Update the SettingsDBO with a new intention to the intentions-list
     * @param item : Intention to add to the list
     * @return updated document as object : SettingsSBO
     */
    fun addIntention(item: IntentionDBO): SettingsDBO
}