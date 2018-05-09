package agartha.site.controllers.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Purpose of this file is wrapping Jackson ObjectMapper with serialisation of dates
 *
 * Created by Jorgen Andersson on 2018-05-09.
 */
class ControllerUtil {




    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        fun <T>objectToString(item: T): String {
            return getSerializer()
                    .writeValueAsString(item)
        }

        fun <T>stringToObject(value: String, clazz: Class<T>): T {
            return getDeserializer()
                    .readValue(value, clazz)
        }

        fun <T>objectListToString(items: List<T>): String {
            return getSerializer()
                    .writeValueAsString(items)
        }

        fun <T>stringToObjectList(value: String, clazz: Class<T>): List<T> {
            val objectMapper = getDeserializer()
            return objectMapper.readValue(value, objectMapper.getTypeFactory().constructCollectionType(List::class.java, clazz))
        }


        private fun getSerializer(): ObjectMapper {
            val javaTimeModule = JavaTimeModule()
            val localDateTimeSerializer = LocalDateTimeSerializer(dateTimeFormatter)
            javaTimeModule.addSerializer(LocalDateTime::class.java, localDateTimeSerializer)
            return jacksonObjectMapper()
                    .registerModule(javaTimeModule)
        }

        private fun getDeserializer(): ObjectMapper {
            val javaTimeModule = JavaTimeModule()
            val localDateTimeDeserializer = LocalDateTimeDeserializer(dateTimeFormatter)
            javaTimeModule.addDeserializer(LocalDateTime::class.java, localDateTimeDeserializer)

            return jacksonObjectMapper()
                    .registerModule(javaTimeModule)
        }
    }
}
