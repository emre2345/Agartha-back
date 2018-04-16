package agartha.data.objects

/**
 * Purpose of this file is representing data object for a settings
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
data class SettingsDBO(
        // List of intentions
        val intentions : List<IntentionDBO> = emptyList(),
        // List of practices
        val practices : List<PracticeDBO> = emptyList(),
        // Database id
        val _id: String? = null)