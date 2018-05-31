package agartha.site.controllers.utils

import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by Jorgen Andersson on 2018-05-09.
 */
class ControllerUtilTest {

    private val SINGLE_SESSION_OBJECT = SessionDBO(
            null,
            "D",
            "I",
            LocalDateTime.parse("2018-05-09 11:58:27", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))

    private val MULTIPLE_SESSION_OBJECTS = listOf(
            SessionDBO(
                    null,
                    "D1",
                    "I1",
                    LocalDateTime.parse("2018-05-09 10:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.parse("2018-05-09 11:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
            SINGLE_SESSION_OBJECT,
            SessionDBO(
                    null,
                    "D2",
                    "I2",
                    LocalDateTime.parse("2018-05-09 14:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
    )

    private val SINGLE_SESSION_STRING = "{\"geolocation\":null,\"discipline\":\"D\",\"intention\":\"I\",\"startTime\":\"2018-05-09T11:58:27.000Z\",\"endTime\":null}"

    private val MULTIPLE_SESSIONS_STRING = "[" +
            "{\"geolocation\":null,\"discipline\":\"D1\",\"intention\":\"I1\",\"startTime\":\"2018-05-09T10:00:00.000Z\",\"endTime\":\"2018-05-09T11:00:00.000Z\"}," +
            "{\"geolocation\":null,\"discipline\":\"D\",\"intention\":\"I\",\"startTime\":\"2018-05-09T11:58:27.000Z\",\"endTime\":null}," +
            "{\"geolocation\":null,\"discipline\":\"D2\",\"intention\":\"I2\",\"startTime\":\"2018-05-09T14:00:00.000Z\",\"endTime\":null}" +
            "]"

    @Test
    fun objectToString_searilize_singleString() {
        val str = ControllerUtil.objectToString(SINGLE_SESSION_OBJECT)
        assertThat(str).isEqualTo(SINGLE_SESSION_STRING)
    }

    @Test
    fun stringToObject_deserailize_singleObject() {
        val obj = ControllerUtil.stringToObject(SINGLE_SESSION_STRING, SessionDBO::class.java)
        assertThat(obj.discipline).isEqualTo("D")
    }

    @Test
    fun objectListToString_serialize_mutipleString() {
        val str = ControllerUtil.objectListToString(MULTIPLE_SESSION_OBJECTS)
        assertThat(str).isEqualTo(MULTIPLE_SESSIONS_STRING)
    }

    @Test
    fun stringToObjects_deserialize_multipleObjects() {
        val objs = ControllerUtil.stringToObjectList(MULTIPLE_SESSIONS_STRING, SessionDBO::class.java)
        assertThat(objs).isEqualTo(MULTIPLE_SESSION_OBJECTS)
    }
}