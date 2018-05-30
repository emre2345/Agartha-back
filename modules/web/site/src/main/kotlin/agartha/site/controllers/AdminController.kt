package agartha.site.controllers

import agartha.data.objects.GeolocationDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.objects.SettingsDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.controllers.utils.DevGeolocationSelect
import spark.Request
import spark.Response
import spark.Spark

/**
 * Purpose of this file is handling administration page for when app is in dev/test
 *
 * TODO: These API requests will manipulate production Database. Remove all possibility before sharp production mode
 *
 * Created by Jorgen Andersson on 2018-05-30.
 */
class AdminController(private val mService: IPractitionerService, private val settings: SettingsDBO?) {

    private val geolocations = DevGeolocationSelect.values().map { it.geolocationDBO }

    init {
        Spark.path("/admin") {

            Spark.get("/practitioners", ::getPractitioners)

            // TODO: CHANGE TO POST
            Spark.get("/generate/:count", ::generatePractitioners)
        }
    }

    /**
     * Function to get all practitioners
     */
    private fun getPractitioners(request: Request, response: Response): String {
        return ControllerUtil.objectListToString(mService.getAll())
    }

    /**
     * Function for randomizing argument number of new practitioners
     */
    private fun generatePractitioners(request: Request, response: Response): String {
        // Get number of practitioners to generate, default (if missing) is zero
        val count: Long = request.params(":count").toLongOrNull() ?: 0

        for (index in 1..count) {
            print("${index} ")

            PractitionerDBO(
                    sessions = listOf(SessionDBO(
                            index = 1,
                            geolocation = getRandomGeolocation(),
                            discipline = "",
                            intention = "")),
                    description = "Generated Practitioner")
        }

        return "OK"
    }


    private fun getRandomGeolocation(): GeolocationDBO {
        return geolocations.shuffled().take(1)[0]
    }
}
