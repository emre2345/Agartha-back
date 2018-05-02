package agartha.site.objects.request

import agartha.data.objects.GeolocationDBO

data class StartSessionInformation(
        val geolocation: GeolocationDBO?,
        val discipline: String,
        val practice: String?,
        val intention: String)