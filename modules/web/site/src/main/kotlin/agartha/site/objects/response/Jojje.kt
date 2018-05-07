package agartha.site.objects.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-05-07.
 */
data class Jojje(
        val id: String = "ABCD",
        val createdDateTime: LocalDateTime = LocalDateTime.now())


class Hovno {

    fun getTheJacksonStuff(): ObjectMapper {
        val javaTimeModule = JavaTimeModule()
        //
//        javaTimeModule.addSerializer(LocalDate::class.java, LocalDateSerializer.INSTANCE)
//        javaTimeModule.addDeserializer(LocalDate::class.java, LocalDateDeserializer.INSTANCE)
        //
        val localDateTimeSerializer = LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME)
        val localDateTimeDeserializer = LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME)

        javaTimeModule.addSerializer(LocalDateTime::class.java, localDateTimeSerializer)
        javaTimeModule.addDeserializer(LocalDateTime::class.java, localDateTimeDeserializer)
        //

        return jacksonObjectMapper()
                .registerModule(javaTimeModule)
                //.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
//                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
//                .registerModule(KotlinModule())
    }
}