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
        // MutableList of intentions
        val intentions : List<IntentionDBO> = emptyList(),
        // List of disciplines
        val disciplines : List<DisciplineDBO> = emptyList(),
        // Number of minutes to calculate stats from in Companion
        val companionMinutes: Long = Settings.COMPANION_NUMBER_OF_MINUTES,
        // Number of hours as goal in Companions
        val companionGoalMinutes: Long = Settings.COMPANION_GOAL_MINUTES
)