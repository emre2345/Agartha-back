package agartha.site

import agartha.data.db.conn.Database
import agartha.data.db.conn.MongoConnection
import agartha.data.services.MonitorService
import agartha.data.services.PractitionerService
import agartha.data.services.SettingsService
import agartha.site.controllers.*
import io.schinzel.basicutils.configvar.ConfigVar
import spark.Spark
import spark.Spark.webSocket

/**
 * Purpose of this file is to Start a WebServer
 *
 * Created by Jorgen Andersson on 2018-04-06.
 */
fun startServer(args: Array<String>) {
    val port: Int = ConfigVar.create(".env").getValue("PORT").toInt()

    // Set Connection to database
    MongoConnection.setConnection(Database.RUNTIME)

    // Start WebSocket
    webSocket("/websocket", WebSocketHandler::class.java)

    // Port where Spark Server is running
    spark.kotlin.port(port)

    // Handling the API
    Spark.path("/v1") {
        /*
         * CORS (Cross Origin stuff)
         * Allow requests from any origin, needed to be able to access this path
         */
        Spark.before("/*", { _, response -> response.header("Access-Control-Allow-Origin", "*") })
        //
        // Controller/Service for current Practitioner
        SettingsController(SettingsService())
        PractitionerController(PractitionerService())
        // Controller/Service for companion Practitioner
        CompanionController(PractitionerService())
        // Developer stuff
        DevelopmentController(PractitionerService())
    }

    // Add Paths for Monitoring - No need to have CORS since this should be called from Monitoring tool fx Pingdom
    MonitorController(MonitorService())

    // Init server
    Spark.init()
}