package agartha.site.objects.webSocket


/**
 * Enum to hold the different types of webSocket events
 */
enum class WebSocketEvents(val eventName : String) {
    START_SESSION ("start_session"),
    NEW_COMPANION ("new_companion"),
    COMPANIONS_SESSIONS ("companions_sessions"),
    COMPANION_LEFT ("companion_left")
}
