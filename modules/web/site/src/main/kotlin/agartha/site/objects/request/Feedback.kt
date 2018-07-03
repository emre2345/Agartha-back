package agartha.site.objects.request


/**
 * Purpose of this class is to represent the feedback that is needed to end a circle
 * This class is used in the PractitionerController when converting data from the request body to a Feedback-object
 */
data class Feedback(
        val feedback: Long)