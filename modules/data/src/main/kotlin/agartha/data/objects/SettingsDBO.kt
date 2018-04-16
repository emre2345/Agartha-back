package agartha.data.objects

/**
 * Purpose of this file is representing data object for a settings
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
data class SettingsDBO(
        val intentions : List<IntentionDBO>,
        val _id: String? = null)