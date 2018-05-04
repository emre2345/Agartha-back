package agartha.data.services

import agartha.data.objects.IntentionDBO
import agartha.data.objects.SettingsDBO

/**
 * The purpose of this class is to create a interface that will be used by the settingsService
 */
interface ISettingsService : IBaseService<SettingsDBO>{
    /**
     * Update the SettingsDBO with a new intention to the intentions-list
     * @param item : Intention to add to the list
     * @return updated document as object : SettingsSBO
     */
    fun addIntention(item: IntentionDBO): SettingsDBO
}