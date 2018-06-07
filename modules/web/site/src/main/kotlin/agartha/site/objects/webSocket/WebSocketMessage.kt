package agartha.site.objects.webSocket

/**
 * Small class to hold a type and a message that is sent from and to the client
 * @param event - String. A WebSocketEvent (used in both to and from client)
 * @param data - String. Data that is stringified. (used in both to and from client)
 * @param practitionersSession - String. The practitioners session (used only when sending connected or closed to client)
 */
class WebSocketMessage(val event: String, val data: String, val practitionersSession: String = "")
