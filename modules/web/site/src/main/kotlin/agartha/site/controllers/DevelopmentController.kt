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
import java.util.*

/**
 * Purpose of this file is Generate Test users in database
 * Usage:
 * 1. set cookie xyz=blaha in browser
 * 2. call SERVERPATH/v1/dev/tjohej
 * 3. read the response user id
 *
 * Created by Jorgen Andersson on 2018-04-26.
 *
 * @param mService object for reading data from data source
 */
class DevelopmentController(private val mService : IPractitionerService) {

    init {
        Spark.path("/dev") {
            //
            Spark.before("/*", { _, _ ->
                val isDev: Boolean = ConfigVar.create(".env").getValue("A_ENVIRONMENT") == "development"
                //
                if (!isDev) {
                    Spark.halt(401, "Request not allowed")
                }
            })
            // API to push more users to database
            Spark.get("/dbsetup", ::pushSomeUsersInDatabase)
        }
    }


    @Suppress("UNUSED_PARAMETER")
    private fun pushSomeUsersInDatabase(request: Request, response: Response): String {

        // User that has not been active for a long time and should not be in companion report
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(30),
                sessions = listOf(
                        SessionDBO(DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO, "Yoga", "Harmony",
                                LocalDateTime.now().minusDays(30).minusMinutes(200),
                                LocalDateTime.now().minusDays(30).minusMinutes(100)),
                        // Abandoned session
                        SessionDBO(DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO, "Yoga", "Love",
                                LocalDateTime.now().minusDays(29).minusMinutes(200)),
                        SessionDBO(DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO, "Yoga", "Empowerment",
                                LocalDateTime.now().minusDays(28).minusMinutes(200),
                                LocalDateTime.now().minusDays(28).minusMinutes(100))
                )))
        //
        // Insert 10 users with multipler sessions but not registered any name
        for (i in 1..10) {
            // Possible geolocations for this user
            val locations = listOf(
                    DevGeolocationSelect.SYDNEY_OPERA_HOUSE.geolocationDBO,
                    DevGeolocationSelect.SYDNEY_HARBOUR_BRIDGE.geolocationDBO,
                    null)
            mService.insert(PractitionerDBO(
                    created = LocalDateTime.now().minusDays(5).minusMinutes(410),
                    sessions = generateSessions(5, locations)))
        }
        //
        // Insert users with abandon sessions
        for (i in 1..5) {
            // Possible geolocations for this user
            val locations = listOf(
                    DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO,
                    DevGeolocationSelect.NEW_YORK_BATTERY_PARK.geolocationDBO,
                    DevGeolocationSelect.NEW_YORK_STATUE.geolocationDBO,
                    DevGeolocationSelect.NEW_YORK_BROOKLYN_BRIDGE.geolocationDBO,
                    DevGeolocationSelect.NEW_YORK_MSG.geolocationDBO)
            mService.insert(PractitionerDBO(
                    created = LocalDateTime.now().minusDays(3).minusMinutes(410),
                    sessions = addSession(
                            generateSessions(3, locations),
                            SessionDBO(DevGeolocationSelect.NEW_YORK_ESB.geolocationDBO, "Yoga", "Empowerment",
                                    LocalDateTime.now().minusDays(1).minusMinutes(200))
                    )))
        }

        //
        // Insert users with ongoing sessions
        for (i in 1..25) {
            val locations = listOf(
                    DevGeolocationSelect.HAVANG.geolocationDBO,
                    DevGeolocationSelect.VITEMOLLA.geolocationDBO,
                    DevGeolocationSelect.KIVIK.geolocationDBO,
                    DevGeolocationSelect.BRANTEVIK.geolocationDBO,
                    DevGeolocationSelect.BACKAKRA.geolocationDBO,
                    DevGeolocationSelect.KASEBERGA.geolocationDBO,
                    DevGeolocationSelect.YSTAD_SALTSJOBAD.geolocationDBO,
                    DevGeolocationSelect.ABBEKAS.geolocationDBO,
                    DevGeolocationSelect.SMYGEHUS.geolocationDBO,
                    null)
            // Random a value
            val minutesAgo = generateRandom(10, 3 * 60)
            mService.insert(PractitionerDBO(
                    created = LocalDateTime.now().minusMinutes(minutesAgo.toLong() + 1),
                    sessions = listOf(
                            SessionDBO(generateGeolocation(locations), generateDiscipline(), generateIntention(),
                                    LocalDateTime.now().minusMinutes(minutesAgo.toLong()))
                    )
            ))
        }

        //
        // Insert a user with multipler sessions but registered name
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(5).minusMinutes(405),
                fullName = "John Hanibal Smith",
                email = "john@agartha.com",
                description = "I love it when a plan comes together",
                sessions = generateSessions(5, listOf(
                        DevGeolocationSelect.SYDNEY_OPERA_HOUSE.geolocationDBO,
                        DevGeolocationSelect.SYDNEY_HARBOUR_BRIDGE.geolocationDBO))))
        //
        // Insert user without sessions, created a while ago
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusDays(10)
        ))
        //
        // Insert user without sessions, just created
        mService.insert(PractitionerDBO(
                created = LocalDateTime.now().minusMinutes(10)
        ))
        //
        // Insert the logged on user, should we have one ongoing session?
        val id = ObjectId().toHexString()
        mService.insert(PractitionerDBO(
                _id = id,
                sessions = listOf(
                        SessionDBO(DevGeolocationSelect.MALMO_KOLLEKTIVA.geolocationDBO, "Yoga", "Transformation",
                                LocalDateTime.now().minusDays(2).minusMinutes(200),
                                LocalDateTime.now().minusDays(2).minusMinutes(100))
                )
        ))
        // Return the Id
        return "{\"id\":\"$id\"}"
    }


    private fun generateRandom(from: Int, to: Int): Int {
        return Random().nextInt(to - from) + from
    }

    private fun generateSessions(
            sessionSize: Int,
            geolocations: List<GeolocationDBO?>): List<SessionDBO> {
        val sessions = mutableListOf<SessionDBO>()

        for (i in sessionSize.downTo(0)) {
            // Random between 5 hours ago and in 5 hours
            val start = generateRandom(5 * 60 * -1, 5 * 60)
            // Each session should last between 10 minutes and 3 hours
            val minutes = generateRandom(10, 3 * 60)
            val startDate = LocalDateTime.now().minusDays(i.toLong()).minusMinutes(start.toLong())
            val endDate = startDate.plusMinutes(minutes.toLong())
            //
            sessions.add(
                    SessionDBO(
                            geolocation = generateGeolocation(geolocations),
                            discipline = generateDiscipline(),
                            intention = generateIntention(),
                            startTime = startDate,
                            endTime = endDate)
            )
        }
        //
        return sessions.sortedBy {
            it.startTime
        }
    }

    private fun generateGeolocation(geolocations: List<GeolocationDBO?>): GeolocationDBO? {
        return geolocations[generateRandom(0, geolocations.lastIndex)]
    }

    private fun generateDiscipline(): String {
        val disciplines = listOf("Yoga", "Meditation", "Martial Arts")
        return disciplines[generateRandom(0, disciplines.lastIndex)]
    }

    private fun generateIntention(): String {
        val intentions = listOf("Wellbeing", "Harmony", "Freedom", "Empowerment", "Resolution", "Empathy",
                "Abundance", "Love", "Celebration", "Transformation")
        return intentions[generateRandom(0, intentions.lastIndex)]
    }

    private fun addSession(list: List<SessionDBO>, session: SessionDBO): List<SessionDBO> {
        val mutable = mutableListOf<SessionDBO>()
        mutable.addAll(list)
        mutable.add(session)
        return mutable
    }
}