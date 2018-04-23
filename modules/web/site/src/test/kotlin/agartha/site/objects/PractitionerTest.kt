package agartha.site.objects

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
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
    fun practitionerLastSessionTime_WithSessions_20() {
        val user = Practitioner(PractitionerDBO(
                listOf(
                        SessionDBO(0, "Yoga", false, Date(2018, 4, 18, 12, 0, 0), Date(2018, 4, 18, 12, 40, 0)),
                        SessionDBO(1, "Mindfulness", false, Date(2018, 4, 19, 12, 0, 0), Date(2018, 4, 19, 12, 30, 0)),
                        SessionDBO(2, "Yes", false, Date(2018, 4, 20, 12, 0, 0), Date(2018, 4, 20, 12, 20, 0))
                ),
                Date(),
                "abc"

        ))
        assertThat(user.lastSessionTime).isEqualTo(20)
    }

    @Test
    fun practitionerTotalSessionTime_WithNoSessions_zero() {
        val user = Practitioner(null, null, null)
        assertThat(user.totalSessionTime).isEqualTo(0)
    }

    @Test
    fun practitionerTotalSessionTime_WithSessions_80() {
        val user = Practitioner(PractitionerDBO(
                listOf(
                        SessionDBO(0, "Yoga", false, Date(2018, 4, 18, 12, 0, 0), Date(2018, 4, 18, 12, 40, 0)),
                        SessionDBO(1, "Mindfulness", false, Date(2018, 4, 19, 12, 0, 0), Date(2018, 4, 19, 12, 30, 0)),
                        SessionDBO(2, "Yes", false, Date(2018, 4, 20, 12, 0, 0), Date(2018, 4, 20, 12, 20, 0))
                ),
                Date(),
                "abc"

        ))
        assertThat(user.totalSessionTime).isEqualTo(80)
    }
}