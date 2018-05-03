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
        val intentions : MutableList<IntentionDBO> = mutableListOf(),
        // List of disciplines
        val disciplines : List<DisciplineDBO> = emptyList(),
        // Number of days to calculate stats from in Companion
        val companionDays: Long = Settings.COMPAINON_NUMBER_OF_DAYS,
        // Number of hours as goal in Companions
        val companionGoalHours: Long = Settings.COMPANION_GOLS_HOURS
){
    /**
     * Adds a new intention to the intentions list if it doesn't already exist
     * @param intention - the new intention that will be added
     */
    fun addIntention(intention: IntentionDBO){
        // Find the index of the new intention and see if it already exists in the list
        val index = this.intentions.indexOf(intention)
        // If the index is -1 then the intentions does not exist in the list
        if(index == -1){
            this.intentions.add(intention)
        }
    }
}