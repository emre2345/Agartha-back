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
                val isAllowed: Boolean = true //request.cookie("xyz")?.equals("blaha") ?: false

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
                        SessionDBO(0, "Yoga", "Wellbeing", false, LocalDateTime.now().minusDays(5).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(1, "Yoga", "Love", false, LocalDateTime.now().minusDays(4).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(2, "Yoga", "Transformation", false, LocalDateTime.now().minusDays(3).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(3, "Yoga", "Harmony", false, LocalDateTime.now().minusDays(2).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(4, "Yoga", "Empowerment", false, LocalDateTime.now().minusDays(1).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300))
                )))
        // Insert a user with multipler sessions but registered name
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(5).minusMinutes(405),
                fullName = "John Hanibal Smith",
                email = "john@kollektiva.se",
                description = "I love it when a plan comes together",
                sessions = listOf(
                        SessionDBO(0, "Meditation", "Empowerment", false, LocalDateTime.now().minusDays(5).minusMinutes(400), LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(1, "Meditation", "Harmony", false, LocalDateTime.now().minusDays(4).minusMinutes(400), LocalDateTime.now().minusDays(4).minusMinutes(300)),
                        SessionDBO(2, "Meditation", "Empathy", false, LocalDateTime.now().minusDays(3).minusMinutes(400), LocalDateTime.now().minusDays(3).minusMinutes(300)),
                        SessionDBO(3, "Meditation", "Freedom", false, LocalDateTime.now().minusDays(2).minusMinutes(400), LocalDateTime.now().minusDays(2).minusMinutes(300)),
                        SessionDBO(4, "Meditation", "Love", false, LocalDateTime.now().minusDays(1).minusMinutes(400), LocalDateTime.now().minusDays(1).minusMinutes(300))
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
                        SessionDBO(0, "Yoga", "Harmony", false, LocalDateTime.now().minusDays(3).minusMinutes(200), LocalDateTime.now().minusDays(3).minusMinutes(100)),
                        SessionDBO(1, "Yoga", "Love", false, LocalDateTime.now().minusDays(2).minusMinutes(200), LocalDateTime.now().minusDays(2).minusMinutes(100)),
                        SessionDBO(2, "Yoga", "Empowerment", false, LocalDateTime.now().minusDays(1).minusMinutes(200))
                )))
        // Insert a set of user with ongoing session
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(2).minusMinutes(202),
                sessions = listOf(
                        SessionDBO(0, "Yoga", "Freedom", false, LocalDateTime.now().minusDays(2).minusMinutes(200), LocalDateTime.now().minusDays(2).minusMinutes(100)),
                        SessionDBO(1, "Meditation", "Harmony", false, LocalDateTime.now().minusMinutes(40))
                )
        ))
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusMinutes(50),
                sessions = listOf(
                        SessionDBO(1, "Meditation", "Freedom", false, LocalDateTime.now().minusMinutes(45))
                )
        ))
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(2).minusMinutes(202),
                sessions = listOf(
                        SessionDBO(0, "Yoga", "Empathy", false, LocalDateTime.now().minusDays(2).minusMinutes(200), LocalDateTime.now().minusDays(2).minusMinutes(100)),
                        SessionDBO(1, "Yoga", "Freedom", false, LocalDateTime.now().minusMinutes(40))
                )
        ))
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusMinutes(35),
                sessions = listOf(
                        SessionDBO(1, "Meditation", "Empathy", false, LocalDateTime.now().minusMinutes(30))
                )
        ))
        // Insert the logged on user
        val id = ObjectId().toHexString()
        mService.insert(PractitionerDBO(
                _id = id,
                sessions = listOf(
                        SessionDBO(0, "Yoga", "Transformation", false, LocalDateTime.now().minusDays(2).minusMinutes(200), LocalDateTime.now().minusDays(2).minusMinutes(100)),
                        SessionDBO(0, "Yoga", "Celebration", false, LocalDateTime.now().minusMinutes(45), LocalDateTime.now())
                )
        ))
        // Return the Id
        return "{\"id\":\"${id}\"}"
    }
}