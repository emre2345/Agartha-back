package agartha.data.services

import agartha.data.objects.SettingsDBO

/**
 * Purpose of this file is reading settings from data storage
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
class SettingsService : MongoBaseService<SettingsDBO>(CollectionNames.SETTINGS_SERVICE) {

    /**
     * The settings storage can only have one item
     * @param item Default settings object to insert (if collection is empty)
     * @return Stored settings data object
     */
    override fun insert(item: SettingsDBO): SettingsDBO {
        val items = getAll()
        if (items.isNotEmpty()) {
            return items.first()
        }
        return super.insert(item)
    }
}