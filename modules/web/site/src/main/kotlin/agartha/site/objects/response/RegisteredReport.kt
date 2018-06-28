package agartha.site.objects.response

/**
 * Purpose of this file is to create a response object for a registered report
 * Used in api calls for the circle
 *
 * Created by Rebecca Fransson on 2018-07-28.
 */

data class RegisteredReport(
        val virtualRegistered: Long,
        val practitionersRegistered: Long)