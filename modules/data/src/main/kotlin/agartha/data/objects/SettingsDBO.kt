package agartha.data.objects

import agartha.common.config.Settings

/**
 * Purpose of this file is representing data object for a settings
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
data class SettingsDBO(
        // Database id
        val _id: String? = null,
        // List of intentions
        val intentions : List<IntentionDBO> = emptyList(),
        // List of practices
        val practices : List<PracticeDBO> = emptyList(),
        // Number of days to calculate stats from in Companion
        val companionDays: Long = Settings.COMPAINON_NUMBER_OF_DAYS,
        // Number of hours as goal in Companions
        val companionGoalHours: Long = Settings.COMPANION_GOLS_HOURS)