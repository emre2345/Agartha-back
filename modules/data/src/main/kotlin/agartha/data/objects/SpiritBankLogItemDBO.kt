package agartha.data.objects

import java.time.LocalDateTime


/**
 * Purpose of this file is representing data object for a log in the SpiritBankLog contaning a practitioner
 *
 * Created by Rebecca Fransson on 2018-06-07.
 */
data class SpiritBankLogItemDBO(
        val created: LocalDateTime = LocalDateTime.now(),
        val type: SpiritBankLogItemType,
        val points: Long)


/**
 * Enum to hold the different spirit bank log item types
 */
enum class SpiritBankLogItemType {
    SESSION,
    CREATED_CIRCLE,
    START,
    JOINED_CIRCLE
}
