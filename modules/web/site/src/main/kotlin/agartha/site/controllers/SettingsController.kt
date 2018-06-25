package agartha.site.controllers

import agartha.data.objects.IntentionDBO
import agartha.data.objects.SettingsDBO
import agartha.data.services.ISettingsService
import agartha.site.controllers.utils.ControllerUtil
import agartha.site.controllers.utils.SetupUtil
import spark.Request
import spark.Response
import spark.Spark

/**
 * Purpose of this file is handling API requests for settings
 *
 * Created by Jorgen Andersson on 2018-04-12.
 *
 * @param mService object for reading data from data source
 */
class SettingsController(private val mService: ISettingsService) {

    init {
        Spark.path("/settings") {
            //
            Spark.get("") { _, _ ->
                //
                val list = mService.getAll()
                if (list.isNotEmpty()) {
                    // If Settings data source is not empty, return first item
                    ControllerUtil.objectToString(list[0])
                } else {
                    // If Settings data source is empty, get default
                    ControllerUtil.objectToString(mService.insert(getDefaultSettings()))
                }
            }
            // add Intention to the DB
            Spark.post("/intention", ::addIntention)
        }
    }


    /**
     * Adds an intention to the DBO
     * @return the added intention
     */
    @Suppress("UNUSED_PARAMETER")
    private fun addIntention(request: Request, response: Response): String {
        // Get the new intention from body
        val newIntention: IntentionDBO = ControllerUtil.stringToObject(request.body(), IntentionDBO::class.java)
        // Update the database
        val updatedSettingsDBO: SettingsDBO = mService.addIntention(newIntention)
        // Return the updated SettingsDBO
        return ControllerUtil.objectToString(updatedSettingsDBO)
    }

    /**
     * Create default settings if non exists in database
     * @return default settings
     */
    private fun getDefaultSettings(): SettingsDBO {
        return SettingsDBO(
                intentions = SetupUtil.getDefaultIntentions(),
                disciplines = SetupUtil.getDefaultDisciplines(),
                languages = SetupUtil.getDefaultLanguages())
    }

}
