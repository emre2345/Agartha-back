package agartha.site.controllers

import agartha.data.objects.MonitorDBO
import agartha.data.services.IBaseService
import spark.Spark

/**
 * Purpose of this file is for Monitoring.
 * This file holds API for monitoring service to request to see if
 * a. Site is up
 * b. It is possible to write to database
 * c. It is possible to read from database
 *
 * Created by Jorgen Andersson on 2018-04-06.
 *
 * @param mService object for reading data from data source
 */
class MonitorController(private val mService: IBaseService<MonitorDBO>) {

    init {
        Spark.path("/monitoring") {
            Spark.get("/status") { _, _ ->
                """{"status":"Still alive"}"""
            }
            // Monitoring DB is read/writable
            Spark.path("/db") {
                // Write monitoring
                Spark.post("/write") { _, _ ->
                    val insert = mService.insert(MonitorDBO("Item to insert"))
                    """{"status":"${insert._id?.isNotEmpty()}"}"""
                }
                // Read monitoring
                Spark.get("/read") { _, _ ->
                    val list = mService.getAll()
                    """{"status":"${list.isNotEmpty()}"}"""
                }
            }
        }
    }
}