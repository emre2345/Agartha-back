package agartha.site.objects.request

/**
 * The purpose of this class is to represent the information that is needed when updating the user to a involved user
 * This class is used in the PractitionerController when converting data from the request body to a PractitionerInvolvedInformation-object
 */
data class PractitionerInvolvedInformation(
        val fullName: String,
        val email: String,
        val description: String
)