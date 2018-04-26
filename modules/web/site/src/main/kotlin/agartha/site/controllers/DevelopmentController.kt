package agartha.site.controllers

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import io.schinzel.basicutils.configvar.ConfigVar
import org.bson.types.ObjectId
import spark.Request
import spark.Response
import spark.Spark
import java.time.LocalDateTime

/**
 * Purpose of this file is Generate Test users in database
 * Usage:
 * 1. set cookie xyz=blaha in browser
 * 2. call [server]/v1/dev/tjohej
 * 3. read the response user id
 *
 * Created by Jorgen Andersson on 2018-04-26.
 */
class DevelopmentController {
    // Practitioner data service
    private val mService: IPractitionerService

    constructor(service: IPractitionerService) {
        mService = service

        Spark.path("/dev") {
            //
            Spark.before("/*", { request, _ ->
                val isDev: Boolean = ConfigVar.create(".env").getValue("A_ENVIRONMENT").equals("development")
                val isAllowed: Boolean = request.cookie("xyz")?.equals("blaha") ?: false

                if (!(isDev && isAllowed)) {
                    Spark.halt(401, "Not Authenticated")
                }
            })
            //
            Spark.get("/tjohej", ::pushSomeUsersInDatabase)
        }
    }

    private fun pushSomeUsersInDatabase(request: Request, response: Response) : String {

        // Insert a user with multipler sessions but not registered any name
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(5).minusMinutes(410),
                sessions = listOf(
                        SessionDBO(0, "Yoga", false, LocalDateTime.now().minusDays(5).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(1, "Yoga", false, LocalDateTime.now().minusDays(4).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(2, "Yoga", false, LocalDateTime.now().minusDays(3).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(3, "Yoga", false, LocalDateTime.now().minusDays(2).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(4, "Yoga", false, LocalDateTime.now().minusDays(1).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300))
                )))
        // Insert a user with multipler sessions but registered name
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(5).minusMinutes(405),
                fullName = "John Hanibal Smith",
                email = "john@kollektiva.se",
                description = "I love it when a plan comes together",
                sessions = listOf(
                        SessionDBO(0, "Meditation", false, LocalDateTime.now().minusDays(5).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(1, "Meditation", false, LocalDateTime.now().minusDays(4).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(2, "Meditation", false, LocalDateTime.now().minusDays(3).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(3, "Meditation", false, LocalDateTime.now().minusDays(2).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(4, "Meditation", false, LocalDateTime.now().minusDays(1).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300))
                )))
        // Insert user without sessions, created a while ago
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(10)
        ))
        // Insert user without sessions, just created
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusMinutes(10)
        ))
        // Insert user with abandon sessions
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(3).minusMinutes(210),
                sessions = listOf(
                        SessionDBO(0, "Yoga", false, LocalDateTime.now().minusDays(3).minusMinutes(200), LocalDateTime.now().minusDays(5).minusMinutes(100)),
                        SessionDBO(1, "Yoga", false, LocalDateTime.now().minusDays(2).minusMinutes(200), LocalDateTime.now().minusDays(5).minusMinutes(100)),
                        SessionDBO(2, "Yoga", false, LocalDateTime.now().minusDays(1).minusMinutes(200))
                )))
        // Insert a set of user with ongoing session
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(2).minusMinutes(202),
                sessions = listOf(
                        SessionDBO(0, "Yoga", false, LocalDateTime.now().minusDays(2).minusMinutes(200), LocalDateTime.now().minusDays(5).minusMinutes(100)),
                        SessionDBO(1, "Meditation", false, LocalDateTime.now().minusMinutes(40))
                )
        ))
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusMinutes(60),
                sessions = listOf(
                        SessionDBO(1, "Meditation", false, LocalDateTime.now().minusMinutes(59))
                )
        ))
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(2).minusMinutes(202),
                sessions = listOf(
                        SessionDBO(0, "Yoga", false, LocalDateTime.now().minusDays(2).minusMinutes(200), LocalDateTime.now().minusDays(5).minusMinutes(100)),
                        SessionDBO(1, "Yoga", false, LocalDateTime.now().minusMinutes(40))
                )
        ))
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusMinutes(60),
                sessions = listOf(
                        SessionDBO(1, "Meditation", false, LocalDateTime.now().minusMinutes(59))
                )
        ))
        // Insert the logged on user
        val id = ObjectId().toHexString()
        mService.insert(PractitionerDBO(
                _id = id,
                sessions = listOf(
                        SessionDBO(0, "Yoga", false, LocalDateTime.now().minusDays(2).minusMinutes(200), LocalDateTime.now().minusDays(5).minusMinutes(100))
                )
        ))
        // Return the Id
        return id
    }
}