package agartha.data.services

import agartha.data.objects.SettingsDBO

/**
 * Purpose of this file is reading settings from data storage
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
class SettingsService : MongoBaseService<SettingsDBO>(CollectionNames.SETTINGS_SERVICE)