package agartha.site

import agartha.data.db.conn.Database
import agartha.data.db.conn.MongoConnection
import agartha.data.services.MonitorService
import agartha.data.services.PractitionerService
import agartha.data.services.SettingsService
import agartha.site.controllers.MonitorController
import agartha.site.controllers.PractitionerController
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
        SettingController(SettingsService())
        PractitionerController(PractitionerService())
    }

    // Add Paths for Monitoring
    MonitorController(MonitorService())

    // Init server
    Spark.init()
}