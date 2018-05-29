package agartha.data.services

/**
 * Purpose of this file is holding names of all collections in database
 *
 * Created by Jorgen Andersson on 2018-03-20.
 */
enum class CollectionNames(val collectionName : String) {
    // Collection name for monitoring collection
    MONITOR_SERVICE ("monitor"),
    // Collection name for practitioners
    PRACTITIONER_SERVICE("practitioner"),
    // Collection name for settings
    SETTINGS_SERVICE("settings")
}