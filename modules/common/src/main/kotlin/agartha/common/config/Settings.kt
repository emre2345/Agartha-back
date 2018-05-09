package agartha.common.config

/**
 * Purpose of this class is holding constants of settings variables
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-27.
 */
class Settings {

    companion object {
        // Time before a Session is considered abandon (3 hours)
        const val ABANDON_SESSION_MINUTES : Long = 3 * 60
        // For how many minutes should we get practitioners in database with ongoing sessions
        // in Session end report (24 hours * 60 minutes =  1 day)
        const val SESSION_MINUTES : Long = 24 * 60
        // For how many minutes should we count sessions for companion page (14400minutes = 10days)
        const val COMPANION_NUMBER_OF_MINUTES : Long = 14400
        // What is the goal in minutes for companion page (10 000 hours * 60 minutes)
        const val COMPANION_GOAL_MINUTES : Long = 10000 * 60
    }
}