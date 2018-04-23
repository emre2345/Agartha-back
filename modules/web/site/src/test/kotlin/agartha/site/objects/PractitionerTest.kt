package agartha.site.objects

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime
import java.util.*

/**
 * Purpose of this file is Test for Practitioner response object
 *
 * Created by Jorgen Andersson on 2018-04-23.
 */
class PractitionerTest {

    @Test
    fun practitionerLastSessionTime_WithNoSessions_zero() {
        val user = Practitioner(null, null, null)
        assertThat(user.lastSessionTime).isEqualTo(0)
    }

    @Test
    fun practitionerLastSessionTime_WithSessions_zero() {
        val user = Practitioner(PractitionerDBO(
                listOf(
                       // SessionDBO(0, "", true, LocalDateTime.parse(""), null)
                ),
                Date(),
                "abc"

        ))
        assertThat(user.lastSessionTime).isEqualTo(0)
    }

    @Test
    fun practitionerTotalSessionTime_WithNoSessions_zero() {
        val user = Practitioner(null, null, null)
        assertThat(user.totalSessionTime).isEqualTo(0)
    }
}