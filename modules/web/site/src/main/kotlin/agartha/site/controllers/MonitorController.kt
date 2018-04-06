package agartha.site.controllers

import agartha.data.objects.MonitorDBO
import agartha.data.services.IBaseService
import agartha.data.services.MonitorService
import spark.Spark

/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-04-06.
 */
class MonitorController {
    val mService : IBaseService<MonitorDBO>

    constructor(service: IBaseService<MonitorDBO>) {
        mService = service

        Spark.path("/monitoring") {
            Spark.get("/status") { _, _ -> "Still alive" }
            // Monitoring DB is read/writeable
            Spark.path("/db") {
                // Write monitoring
                Spark.get("/write") { _, _ ->
                    val insert = mService.insert(MonitorDBO("Item to insert"))
                    insert._id?.isNotEmpty().toString()
                }
                // Read monitoring
                Spark.get("/read") { _, _ ->
                    val list = mService.getAll()
                    list.isNotEmpty().toString()
                }
            }
        }
    }
}