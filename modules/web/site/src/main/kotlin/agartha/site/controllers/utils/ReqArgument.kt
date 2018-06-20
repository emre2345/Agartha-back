package agartha.site.controllers.utils

/**
 * Purpose of this class ...
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-06-20.
 */
enum class ReqArgument(val value: String) {
    PRACTITIONER_ID(":userId"),
    CIRCLE_ID(":circleId"),
    IMAGE_ID(":imageId"),
    POINTS(":points"),
    COUNT(":count"),
    DISCIPLINE(":discipline"),
    INTENTION(":intention")
}