package agartha.site.objects.request

import agartha.data.objects.GeolocationDBO

/**
 * Purpose of this class is to represent the data that is needed to start a session
 * This class is used in the PractitionerController when converting data from the request body to a StartSessionInformation-object
 */
data class StartSessionInformation(
        val geolocation: GeolocationDBO?,
        val discipline: String,
        val practice: String?,
        val intention: String)