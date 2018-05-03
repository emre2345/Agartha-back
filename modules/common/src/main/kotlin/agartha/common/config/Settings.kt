package agartha.common.config

/**
 * Purpose of this class is holding constants of settings variables
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-27.
 */
class Settings {

    companion object {
        // Time before a Session is considered abandone
        const val ABONDON_SESSION_MINUTES : Long = 3 * 60
        // For how many hours should we get practitioners in database with ongoing sessions
        // in Session end report
        const val SESSION_HOURS : Long = 24
        // For how many days should we count sessions for companion page
        const val COMPANION_NUMBER_OF_DAYS : Long = 10
        const val COMPANION_NUMBER_OF_HOURS = COMPANION_NUMBER_OF_DAYS * 24
        // What is the goal in hours for companion page
        const val COMPANION_GOLS_HOURS : Long = 10000
    }
}