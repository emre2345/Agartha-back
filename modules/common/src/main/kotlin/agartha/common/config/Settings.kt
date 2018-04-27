package agartha.common.config

/**
 * Purpose of this class ...
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-27.
 */
class Settings {

    companion object {
        // Time before a Session is considered abandone
        val ABONDON_SESSION_MINUTES : Long = 3 * 60
        // For how many hours should we get practitioners in database with ongoing sessions
        // in Session end report
        val SESSION_HOURS : Long = 24
        // For how many days should we count sessions for companion page
        val COMPAION_NUMBER_OF_DAYS : Long = 10
    }
}