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
        // For how many minutes should we get practitioners in database with ongoing sessions
        // in Session end report ( 1day * 24 hours * 60 minutes  =  1 day in minutes)
        const val SESSION_MINUTES : Long = 1 * 24 * 60
        // For how many minutes should we count sessions for companion page (24 hours * 60 minutes = 1 day in minutes)
        const val COMPANION_NUMBER_OF_MINUTES : Long = 24 * 60
        // What is the goal in minutes for companion page (10 000 hours * 60 minutes)
        const val COMPANION_GOAL_MINUTES : Long = 10000 * 60
        // Admin PassPhrase, must be passed as body when trying to reach any of the admin paths
        const val ADMIN_PASS_PHRASE = "Do you mind if I don't smoke"
    }
}