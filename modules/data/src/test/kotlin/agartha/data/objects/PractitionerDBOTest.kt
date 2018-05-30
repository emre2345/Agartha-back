package agartha.data.objects

import org.assertj.core.api.Assertions
import org.junit.Test
import java.time.LocalDateTime

/**
 *
 */
class PractitionerDBOTest {
    val expectedFullName = "Santa Clause"
    val expectedEmail = "santa@agartha.com"
    val expectedDescription = "Jag gillar yoga!"

    /**
     * addInvolvedInformation
     */
    @Test
    fun practitionerInvolvedInformation_isInvolved_true() {
        val practitioner = PractitionerDBO()
        practitioner.addInvolvedInformation(expectedFullName, expectedEmail, expectedDescription)
        Assertions.assertThat(practitioner.involved()).isTrue()
    }

    /**
     * addInvolvedInformation
     */
    @Test
    fun practitionerInvolvedInformation_fullName_Stanta() {
        val practitioner = PractitionerDBO()
        practitioner.addInvolvedInformation(expectedFullName, "", "")
        Assertions.assertThat(practitioner.fullName).isEqualTo(expectedFullName)
    }

    /**
     * addInvolvedInformation
     */
    @Test
    fun practitionerInvolvedInformation_email_SantaAtAgarthaCom() {
        val practitioner = PractitionerDBO()
        practitioner.addInvolvedInformation("", expectedEmail, "")
        Assertions.assertThat(practitioner.email).isEqualTo(expectedEmail)
    }

    /**
     * addInvolvedInformation
     */
    @Test
    fun practitionerInvolvedInformation_description_JagGillarYoga() {
        val practitioner = PractitionerDBO()
        practitioner.addInvolvedInformation("", "", expectedDescription)
        Assertions.assertThat(practitioner.description).isEqualTo(expectedDescription)
    }

    @Test
    fun hasOngoingSession_singleSessionOngoing_true() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = LocalDateTime.now().minusMinutes(21),
                sessions = listOf(SessionDBO(
                        discipline = "d",
                        intention = "i",
                        startTime = LocalDateTime.now().minusMinutes(20))))
        Assertions.assertThat(practitioner.hasOngoingSession()).isTrue()
    }

    @Test
    fun hasOngoingSession_singleSessionAbandon_false() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = LocalDateTime.now().minusMinutes(182),
                sessions = listOf(SessionDBO(
                        discipline = "d",
                        intention = "i",
                        startTime = LocalDateTime.now().minusMinutes(181))))
        Assertions.assertThat(practitioner.hasOngoingSession()).isFalse()
    }

    @Test
    fun hasOngoingSession_multipleSessionOngoing_true() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = LocalDateTime.now().minusMinutes(21),
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = LocalDateTime.now().minusMinutes(20)),
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = LocalDateTime.now().minusMinutes(15))))
        Assertions.assertThat(practitioner.hasOngoingSession()).isTrue()
    }

    @Test
    fun hasOngoingSession_singleSessionClosed_false() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = LocalDateTime.now().minusMinutes(21),
                sessions = listOf(SessionDBO(
                        discipline = "d",
                        intention = "i",
                        startTime = LocalDateTime.now().minusMinutes(20),
                        endTime = LocalDateTime.now())))
        Assertions.assertThat(practitioner.hasOngoingSession()).isFalse()
    }

    @Test
    fun hasOngoingSession_multipleSessionSecondLastOngoing_false() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = LocalDateTime.now().minusMinutes(21),
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = LocalDateTime.now().minusMinutes(20)),
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = LocalDateTime.now().minusMinutes(15),
                                endTime = LocalDateTime.now())))
        Assertions.assertThat(practitioner.hasOngoingSession()).isFalse()
    }

}