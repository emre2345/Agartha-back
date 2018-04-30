package agartha.site.objects.request

data class StartSessionInformation(
        val discipline: String,
        val practice: String?,
        val intention: String
)