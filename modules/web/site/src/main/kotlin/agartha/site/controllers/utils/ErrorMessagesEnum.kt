package agartha.site.controllers.utils

enum class ErrorMessagesEnum(val message: String){
    CIRCLE_NOT_ACTIVE_OR_EXIST("Circle is not active or does not exist"),
    PRACTITIONER_ID_INCORRECT("Practitioner Id missing or incorrect"),
    PRACTITIONER_OUT_OF_FUNDS("Practitioner cannot afford, spirit bank log sum is too low"),
    PRACTITIONER_NOT_AFFORD_CIRCLE("Practitioner cannot afford to join this circle"),
    PRACTITIONER_NOT_CREATOR_CIRCLE("Practitioner is not the creator of this circle"),
    DISCIPLINE_NOT_MATCHED("Selected discipline does not match any in Circle"),
    INTENTION_NOT_MATCHED("Selected intention does not match any in Circle"),
    EMAIL_NOT_FOUND("Email is not registered in database"),
    NEGATIVE_INTEGER_VALUE("Value cannot be negative"),
    PRACTITIONER_NOT_AFFORD_ADD_VIRTUAL("Practitioner cannot afford to have this many virtual registered to this circle")
}