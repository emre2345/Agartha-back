package agartha.common.config

/**
 * Purpose of this class is holding constants of settings variables
 *
 * Created by Jorgen Andersson on 2018-04-27.
 */
class Settings {

    companion object {
        // Time before a Session is considered abandon (3 hours)
        const val ABANDON_SESSION_MINUTES: Long = 3 * 60
        // For how many minutes should we count sessions for companion page (24 hours * 60 minutes = 1 day in minutes)
        const val COMPANION_NUMBER_OF_MINUTES: Long = 24 * 60
        // What is the goal in minutes for companion page (10 000 hours * 60 minutes)
        const val COMPANION_GOAL_MINUTES: Long = 10000 * 60
        // Admin PassPhrase, must be passed as body when trying to reach any of the admin paths
        const val ADMIN_PASS_PHRASE: String = "Do you mind if I don't smoke"
        // Start points in the first log in the SpiritBank
        const val SPIRIT_BANK_START_POINTS: Long = 50

        /**
         * Returns a negative number by multiplying the number with -1
         * Used to make the minimalContributionPoints from a circle to a negative number that is stored in the SpiritBankLog when joining a circle
         * @return negative number
         */
        fun returnNegativeNumber(number: Long): Long {
            // Make sure number is positive
            if (number > 0L) {
                return number * -1 // multiplied -1 makes the number negative
            }
            return number
        }

    }
}