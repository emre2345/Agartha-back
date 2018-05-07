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

/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-05-07.
 */
data class Jojje(
        val id: String = "ABCD",
        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", shape=JsonFormat.Shape.STRING)
        val createdDateTime: LocalDateTime = LocalDateTime.now())


class Hovno {

    fun getTheJacksonStuff(): ObjectMapper {
        val javaTimeModule = JavaTimeModule()
        //
//        javaTimeModule.addSerializer(LocalDate::class.java, LocalDateSerializer.INSTANCE)
//        javaTimeModule.addDeserializer(LocalDate::class.java, LocalDateDeserializer.INSTANCE)
        //
//        javaTimeModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer.INSTANCE)
//        javaTimeModule.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer.INSTANCE)
        //

        return jacksonObjectMapper()
                .registerModule(javaTimeModule)
                //.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
//                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
//                .registerModule(KotlinModule())
    }
}