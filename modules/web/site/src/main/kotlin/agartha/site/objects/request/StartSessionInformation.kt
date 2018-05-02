package agartha.site.objects.request

data class GeoLocation(val latitude : Double, val longitude : Double)

data class StartSessionInformation(
        val geolocation: GeoLocation,
        val discipline: String,
        val practice: String,
        val intention: String)