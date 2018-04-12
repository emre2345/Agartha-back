package agartha.site.controllers

import agartha.data.objects.IntentionDBO
import agartha.data.objects.SettingsDBO
import agartha.data.services.IBaseService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Spark.get

/**
 * Purpose of this file is handling API requests for settings
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
class SettingController {
    val mService: IBaseService<SettingsDBO>
    // For mapping objects to string
    val mMapper = jacksonObjectMapper()

    constructor(service: IBaseService<SettingsDBO>) {
        mService = service

        get("/settings") { _, _ ->
            val settings = if (mService.getAll().firstOrNull() != null)
                mService.getAll().first() else mService.insert(getDefaultSettings())
            mMapper.writeValueAsString(settings)
        }

    }


    /**
     * Create default settings if non exists in database
     * @return default settings
     */
    private fun getDefaultSettings(): SettingsDBO {
        return SettingsDBO(
                listOf(
                        IntentionDBO("Daniel Nilsson", "En himla massa text", "example/staff/dn.jpg"),
                        IntentionDBO("Daniel Palmér", "En himla massa text", "example/staff/dp.jpg"),
                        IntentionDBO("Henrik Schinzel", "En himla massa text", "example/staff/hs.jpg"),
                        IntentionDBO("Jörgen Andersson", "En himla massa text", "example/staff/ja.jpg"),
                        IntentionDBO("Malin Eriksson", "En himla massa text", "example/staff/me.jpg"),
                        IntentionDBO("Måns Holmgren", "En himla massa text", "example/staff/mh.jpg"),
                        IntentionDBO("Nicklas Larsson", "En himla massa text", "example/staff/nl.jpg"),
                        IntentionDBO("Pontus Kristiansson", "En himla massa text", "example/staff/pk.jpg"),
                        IntentionDBO("Rebecca Fransson", "En himla massa text", "example/staff/rf.jpg")))
    }
}
