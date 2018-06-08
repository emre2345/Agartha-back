package agartha.site.controllers

import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.services.IPractitionerService
import org.bson.types.ObjectId
import spark.Request
import spark.Spark.halt
import java.time.LocalDateTime

/**
 * Purpose of this file is handle mapping requests that can go wrong
 *
 * Created by Jorgen Andersson on 2018-06-07.
 */
abstract class AbstractController {

    /**
     * Get practitioner from database or response with 400
     */
    fun getPractitionerFromDatabase(practitionerId: String, service: IPractitionerService): PractitionerDBO {
        val practitioner = service.getById(practitionerId)
        // Halt the request and send 400
        if (practitioner == null) {
            halt(400, "Practitioner Id missing or incorrect")
        }
        // The if practitioner is null will not be excuted since we already sent a 400 response
        return practitioner ?: PractitionerDBO()
    }

    fun getActiveCircleFromDatabase(circleId: String, service: IPractitionerService): CircleDBO {
        val circle = service
                // Get all practitioner
                .getAll()
                // Get all circles from practitioner
                .flatMap {
                    it.circles
                }
                // Filter out active
                .filter {
                    it.active()
                }
                // Find the one with correct id
                .find {
                    it._id == circleId
                }

        if (circle == null) {
            halt(400, "Circle not active")
        }
        // The if circle is null will not be excuted since we already sent a 400 response
        return circle ?: CircleDBO(
                name = "",
                description = "",
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now(),
                disciplines = listOf(),
                intentions = listOf(),
                minimumSpiritContribution = 0)
    }
}