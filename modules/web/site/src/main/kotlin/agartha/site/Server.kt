package agartha.site

import agartha.data.db.conn.Database
import agartha.data.db.conn.MongoConnection
import agartha.data.services.MonitorService
import agartha.data.services.PractitionerAndSessionService
import agartha.data.services.SettingsService
import agartha.site.controllers.MonitorController
import agartha.site.controllers.PractitionerController
import agartha.site.controllers.SessionController
import agartha.site.controllers.SettingController
import io.schinzel.basicutils.configvar.ConfigVar
import spark.Spark

/**
 * Purpose of this file is to Start a WebServer
 *
 * Created by Jorgen Andersson on 2018-04-06.
 */
fun startServer(args: Array<String>) {
    val port: Int = ConfigVar.create(".env").getValue("PORT").toInt()

    // Set Connection to database
    MongoConnection.setConnection(Database.RUNTIME)


    // Port where Spark Server is running
    spark.kotlin.port(port)

    // Handling the API
    Spark.path("/v1") {
        // Enable CORS - First version remove one of these when the FE ApiFetch is verified
        Spark.before ("/*", {_, response -> response.header("Access-Control-Allow-Origin", "*") })

        /**
         * CORS (Cross Origin stuff)
         * Allow requests from any origin, needed to be able to access this path
         */
//        Spark.options("/*") { request, response ->
//            response.header("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
//            response.header("Access-Control-Allow-Origin", "*")
//            response.header("Access-Control-Allow-Credentials", "true")
//            "OK"
//        }
        //
        SettingController(SettingsService())
        PractitionerController(PractitionerAndSessionService())
        SessionController(PractitionerAndSessionService())
    }

    // Add Paths for Monitoring - No need to have CORS since this should be called from Monitoring tool fx Pingdom
    MonitorController(MonitorService())

    // Init server
    Spark.init()
}