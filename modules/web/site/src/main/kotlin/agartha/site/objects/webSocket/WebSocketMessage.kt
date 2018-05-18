package agartha.site.objects.webSocket

/**
 * Small class to hold a type and a message that is sent from and to the client
 */
class WebSocketMessage(val event: String, val data: Any)