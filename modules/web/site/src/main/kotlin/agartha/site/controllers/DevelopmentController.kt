package agartha.site.controllers

import agartha.data.objects.GeolocationDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import agartha.site.controllers.utils.DevGeolocationSelect
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
                //
                if (!isDev) {
                    Spark.halt(401, "Request not allowed")
                }
            })
            // API to push more users to database
            Spark.get("/dbsetup", ::pushSomeUsersInDatabase)
        }
    }

    private fun pushSomeUsersInDatabase(request: Request, response: Response) : String {
        // User that has not been active for a long time and should not be in companion report
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(30),
                sessions = listOf(
                        SessionDBO(0, DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO, "Yoga","Harmony",
                                LocalDateTime.now().minusDays(30).minusMinutes(200),
                                LocalDateTime.now().minusDays(30).minusMinutes(100)),
                        // Abandoned session
                        SessionDBO(1, DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO, "Yoga","Love",
                                LocalDateTime.now().minusDays(29).minusMinutes(200)),
                        SessionDBO(2, DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO, "Yoga","Empowerment",
                                LocalDateTime.now().minusDays(28).minusMinutes(200),
                                LocalDateTime.now().minusDays(28).minusMinutes(100))
                )))
        // Insert a user with multipler sessions but not registered any name
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(5).minusMinutes(410),
                sessions = listOf(
                        SessionDBO(0, null,"Yoga","Wellbeing",
                                LocalDateTime.now().minusDays(5).minusMinutes(400),
                                LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(1, null, "Yoga","Love",
                                LocalDateTime.now().minusDays(4).minusMinutes(400),
                                LocalDateTime.now().minusDays(4).minusMinutes(300)),
                        SessionDBO(2, null, "Yoga","Transformation",
                                LocalDateTime.now().minusDays(3).minusMinutes(400),
                                LocalDateTime.now().minusDays(3).minusMinutes(300)),
                        SessionDBO(3, null, "Yoga","Harmony",
                                LocalDateTime.now().minusDays(2).minusMinutes(400),
                                LocalDateTime.now().minusDays(2).minusMinutes(300)),
                        SessionDBO(4, null, "Yoga","Empowerment",
                                LocalDateTime.now().minusDays(1).minusMinutes(400),
                                LocalDateTime.now().minusDays(1).minusMinutes(300))
                )))
        // Insert a user with multipler sessions but registered name
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(5).minusMinutes(405),
                fullName = "John Hanibal Smith",
                email = "john@kollektiva.se",
                description = "I love it when a plan comes together",
                sessions = listOf(
                        SessionDBO(0, DevGeolocationSelect.SYDNEY_OPERA_HOUSE.geolocationDBO, "Meditation", "Empowerment",
                                LocalDateTime.now().minusDays(5).minusMinutes(400),
                                LocalDateTime.now().minusDays(5).minusMinutes(300)),
                        SessionDBO(1, DevGeolocationSelect.SYDNEY_OPERA_HOUSE.geolocationDBO, "Meditation", "Harmony",
                                LocalDateTime.now().minusDays(4).minusMinutes(400),
                                LocalDateTime.now().minusDays(4).minusMinutes(300)),
                        SessionDBO(2, DevGeolocationSelect.SYDNEY_OPERA_HOUSE.geolocationDBO, "Meditation", "Empathy",
                                LocalDateTime.now().minusDays(3).minusMinutes(400),
                                LocalDateTime.now().minusDays(3).minusMinutes(300)),
                        SessionDBO(3, null, "Meditation", "Freedom",
                                LocalDateTime.now().minusDays(2).minusMinutes(400),
                                LocalDateTime.now().minusDays(2).minusMinutes(300)),
                        SessionDBO(4, DevGeolocationSelect.SYDNEY_OPERA_HOUSE.geolocationDBO, "Meditation", "Love",
                                LocalDateTime.now().minusDays(1).minusMinutes(400),
                                LocalDateTime.now().minusDays(1).minusMinutes(300))
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
                        SessionDBO(0, DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO, "Yoga","Harmony",
                                LocalDateTime.now().minusDays(3).minusMinutes(200),
                                LocalDateTime.now().minusDays(3).minusMinutes(100)),
                        SessionDBO(1, DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO, "Yoga","Love",
                                LocalDateTime.now().minusDays(2).minusMinutes(200),
                                LocalDateTime.now().minusDays(2).minusMinutes(100)),
                        SessionDBO(2, DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO, "Yoga","Empowerment",
                                LocalDateTime.now().minusDays(1).minusMinutes(200))
                )))
        // Insert a set of user with ongoing session
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(2).minusMinutes(202),
                sessions = listOf(
                        SessionDBO(0, null, "Yoga","Freedom",
                                LocalDateTime.now().minusDays(2).minusMinutes(200),
                                LocalDateTime.now().minusDays(2).minusMinutes(100)),
                        SessionDBO(1, DevGeolocationSelect.SYDNEY_OPERA_HOUSE.geolocationDBO, "Meditation","Harmony",
                                LocalDateTime.now().minusMinutes(40))
                )
        ))
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusMinutes(50),
                sessions = listOf(
                        SessionDBO(1, DevGeolocationSelect.MALMO_TRIANGELN.geolocationDBO, "Meditation","Freedom",
                                LocalDateTime.now().minusMinutes(45))
                )
        ))
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(2).minusMinutes(202),
                sessions = listOf(
                        SessionDBO(0, DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO, "Yoga", "Empathy",
                                LocalDateTime.now().minusDays(2).minusMinutes(200),
                                LocalDateTime.now().minusDays(2).minusMinutes(100)),
                        SessionDBO(1, DevGeolocationSelect.BJORNSTORP.geolocationDBO, "Yoga","Freedom",
                                LocalDateTime.now().minusMinutes(40))
                )
        ))
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusMinutes(35),
                sessions = listOf(
                        SessionDBO(1, null, "Meditation","Empathy",
                                LocalDateTime.now().minusMinutes(30))
                )
        ))
        // Insert the logged on user, should we have one ongoing session?
        val id = ObjectId().toHexString()
        mService.insert(PractitionerDBO(
                _id = id,
                sessions = listOf(
                        SessionDBO(0, DevGeolocationSelect.MALMO_KOLLEKTIVA.geolocationDBO, "Yoga","Transformation",
                                LocalDateTime.now().minusDays(2).minusMinutes(200),
                                LocalDateTime.now().minusDays(2).minusMinutes(100))
                )
        ))
        // Return the Id
        return "{\"id\":\"${id}\"}"
    }
}