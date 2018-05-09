package agartha.site.controllers.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Purpose of this file is having Jackson ObjectMapper being able to format Dates in output
 * and keeping this in one single place
 *
 * Created by Jorgen Andersson on 2018-05-08.
 */
class ObjectToStringFormatter {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")


    /**
     * Get the Object Mapper with serializer/deserailizer for java.time.localDateTime
     *
     * @return Jackson ObjectMapper
     */
    fun getFormatter() : ObjectMapper {

        val javaTimeModule = JavaTimeModule()
        // LocalDateTimeFormatter
        val localDateTimeSerializer = LocalDateTimeSerializer(dateTimeFormatter)
        val localDateTimeDeserializer = LocalDateTimeDeserializer(dateTimeFormatter)
        // Add formatter
        javaTimeModule.addSerializer(LocalDateTime::class.java, localDateTimeSerializer)
        javaTimeModule.addDeserializer(LocalDateTime::class.java, localDateTimeDeserializer)

        return jacksonObjectMapper()
                .registerModule(javaTimeModule)
    }
}