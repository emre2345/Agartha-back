package agartha.site

import io.schinzel.basicutils.configvar.ConfigVar
import spark.Spark

/**
 * Purpose of this file is to Start a WebServer
 *
 * Created by Jorgen Andersson on 2018-04-06.
 */
fun startServer(args: Array<String>) {
    val port: Int = ConfigVar.create(".env").getValue("PORT").toInt()

    // Port where Spark Server is running
    spark.kotlin.port(port)

    // Handling the API
    Spark.path("/v1") {

    }

    // Init server
    Spark.init()
}