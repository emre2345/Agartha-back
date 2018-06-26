package agartha.site.controllers.utils

enum class ErrorMessagesEnum(val message: String){
    CIRCLE_NOT_ACTIVE_OR_EXIST("Circle is not active or does not exist"),
    PRACTITIONER_ID_INCORRECT("Practitioner Id missing or incorrect"),
    PRACTITIONER_NOT_AFFORD_CIRCLE("Practitioner cannot afford to join this circle"),
    DISCIPLINE_NOT_MATCHED("Selected discipline does not match any in Circle"),
    INTENTION_NOT_MATCHED("Selected intention does not match any in Circle")
}