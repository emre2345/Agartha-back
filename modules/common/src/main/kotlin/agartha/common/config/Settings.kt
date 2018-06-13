package agartha.common.config

/**
 * Purpose of this class is holding constants of settings variables
 *
 * Created by Jorgen Andersson on 2018-04-27.
 */
class Settings {

    companion object {
        // Time before a Session is considered abandon (3 hours)
        const val ABANDON_SESSION_MINUTES : Long = 3 * 60
        // For how many minutes should we count sessions for companion page (24 hours * 60 minutes = 1 day in minutes)
        const val COMPANION_NUMBER_OF_MINUTES : Long = 24 * 60
        // What is the goal in minutes for companion page (10 000 hours * 60 minutes)
        const val COMPANION_GOAL_MINUTES : Long = 10000 * 60
        // Start points in the first log in the SpiritBank
        const val SPIRIT_BANK_START_POINTS: Long = 50

    }
}