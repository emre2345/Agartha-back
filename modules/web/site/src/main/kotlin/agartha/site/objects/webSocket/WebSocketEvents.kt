package agartha.site.objects.webSocket


/**
 * Enum to hold the different types of webSocket events
 */
enum class WebSocketEvents(val eventName : String) {
    START_SESSION ("start_session"),
    START_VIRTUAL_SESSION ("start_virtual_session"),
    RECONNECT_SESSION("reconnect_session"),
    NEW_COMPANION ("new_companion"),
    COMPANIONS_SESSIONS ("companions_sessions"),
    COMPANION_LEFT ("companion_left"),
    COMPANION_LEFT_WITH_VIRTUAL_SESSIONS ("companion_left_with_virtual_sessions")
}
