package agartha.data.objects

import agartha.common.utils.DateTimeFormat
import java.time.LocalDateTime


/**
 * Purpose of this file is representing data object for a log in the SpiritBankLog contaning a practitioner
 *
 * Created by Rebecca Fransson on 2018-06-07.
 */
data class SpiritBankLogItemDBO(
        val created: LocalDateTime = DateTimeFormat.localDateTimeUTC(),
        val type: SpiritBankLogItemType,
        val points: Long)


/**
 * Enum to hold the different spirit bank log item types
 */
enum class SpiritBankLogItemType {
    // When practitioner joins Agartha
    START,
    // When practitioner ends a session
    ENDED_SESSION,
    // When practitioner Joins a Circle
    JOINED_CIRCLE,
    // When a circle creator ends his/her session in circle
    ENDED_CREATED_CIRCLE,
    // When a circle creator wants to add virtual sessions to its circle
    ADD_VIRTUAL_TO_CIRCLE,
    // Points donated from user to user
    DONATE
}
