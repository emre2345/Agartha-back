package agartha.data.objects

import agartha.common.utils.DateTimeFormat
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 *
 */
class PractitionerDBOTest {
    private val expectedFullName = "Santa Clause"
    private val expectedEmail = "santa@agartha.com"
    private val expectedDescription = "Jag gillar yoga!"

    /****************************
     * add Involved Information *
     ****************************/
    @Test
    fun practitionerInvolvedInformation_isInvolved_true() {
        val practitioner = PractitionerDBO()
        practitioner.addInvolvedInformation(expectedFullName, expectedEmail, expectedDescription)
        assertThat(practitioner.involved()).isTrue()
    }
    @Test
    fun practitionerInvolvedInformation_fullName_Stanta() {
        val practitioner = PractitionerDBO()
        practitioner.addInvolvedInformation(expectedFullName, "", "")
        assertThat(practitioner.fullName).isEqualTo(expectedFullName)
    }
    @Test
    fun practitionerInvolvedInformation_email_SantaAtAgarthaCom() {
        val practitioner = PractitionerDBO()
        practitioner.addInvolvedInformation("", expectedEmail, "")
        assertThat(practitioner.email).isEqualTo(expectedEmail)
    }
    @Test
    fun practitionerInvolvedInformation_description_JagGillarYoga() {
        val practitioner = PractitionerDBO()
        practitioner.addInvolvedInformation("", "", expectedDescription)
        assertThat(practitioner.description).isEqualTo(expectedDescription)
    }

    /***********************
     * has Session Between *
     ***********************/
    @Test
    fun hasSessionBetween_before_false() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = DateTimeFormat.localDateTimeUTC().minusMinutes(21),
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(60),
                                endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(50)),
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(40),
                                endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30))))
        assertThat(practitioner.hasSessionBetween(
                DateTimeFormat.localDateTimeUTC().minusMinutes(75),
                DateTimeFormat.localDateTimeUTC().minusMinutes(70))).isFalse()
    }
    @Test
    fun hasSessionBetween_after_false() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = DateTimeFormat.localDateTimeUTC().minusMinutes(21),
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(60),
                                endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(50)),
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(40),
                                endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30))))
        assertThat(practitioner.hasSessionBetween(
                DateTimeFormat.localDateTimeUTC().minusMinutes(25),
                DateTimeFormat.localDateTimeUTC().minusMinutes(20))).isFalse()
    }
    @Test
    fun hasSessionBetween_around_true() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = DateTimeFormat.localDateTimeUTC().minusMinutes(21),
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(60),
                                endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(50)),
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(40),
                                endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30))))
        assertThat(practitioner.hasSessionBetween(
                DateTimeFormat.localDateTimeUTC().minusMinutes(75),
                DateTimeFormat.localDateTimeUTC().minusMinutes(20))).isTrue()
    }
    @Test
    fun hasSessionBetween_within_true() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = DateTimeFormat.localDateTimeUTC().minusMinutes(21),
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(60),
                                endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(50)),
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(40),
                                endTime = DateTimeFormat.localDateTimeUTC().minusMinutes(30))))
        assertThat(practitioner.hasSessionBetween(
                DateTimeFormat.localDateTimeUTC().minusMinutes(55),
                DateTimeFormat.localDateTimeUTC().minusMinutes(35))).isTrue()
    }
    @Test
    fun hasSessionBetween_ongoing_true() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = DateTimeFormat.localDateTimeUTC().minusMinutes(21),
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(60))))

        assertThat(practitioner.hasSessionBetween(
                DateTimeFormat.localDateTimeUTC().minusMinutes(55),
                DateTimeFormat.localDateTimeUTC().minusMinutes(35))).isTrue()
    }


    /******************************************
     * has Session In Circle After Start Time *
     ******************************************/
    val circle = CircleDBO(
            name = "Circle name",
            description = "Circle description",
            startTime = DateTimeFormat.localDateTimeUTC(),
            endTime = DateTimeFormat.localDateTimeUTC().plusHours(6),
            intentions = listOf(),
            disciplines = listOf(),
            minimumSpiritContribution = 12L,
            language = "Swedish")
    val circle2 = CircleDBO(
            name = "Circle name2",
            description = "Circle description2",
            startTime = DateTimeFormat.localDateTimeUTC(),
            endTime = DateTimeFormat.localDateTimeUTC().plusHours(5),
            intentions = listOf(),
            disciplines = listOf(),
            minimumSpiritContribution = 1200L,
            language = "Swedish")
    @Test
    fun hasSessionInCircleAfterStartTime_startedAfterAndSameCircle_true() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                                circle = circle)))

        assertThat(practitioner.hasSessionInCircleAfterStartTime(
                DateTimeFormat.localDateTimeUTC().minusMinutes(55), circle)).isTrue()
    }
    @Test
    fun hasSessionInCircleAfterStartTime_startedAfterNoCircle_false() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10))))

        assertThat(practitioner.hasSessionInCircleAfterStartTime(
                DateTimeFormat.localDateTimeUTC().minusMinutes(55), circle)).isFalse()
    }
    @Test
    fun hasSessionInCircleAfterStartTime_startedAfterNotSameCircle_false() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(10),
                                circle= circle2)))

        assertThat(practitioner.hasSessionInCircleAfterStartTime(
                DateTimeFormat.localDateTimeUTC().minusMinutes(55), circle)).isFalse()
    }
    @Test
    fun hasSessionInCircleAfterStartTime_startedBeforeSameCircle_false() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(50),
                                circle= circle)))

        assertThat(practitioner.hasSessionInCircleAfterStartTime(
                DateTimeFormat.localDateTimeUTC().minusMinutes(10), circle)).isFalse()
    }


    /***********************
     * has Ongoing Session *
     ***********************/
    @Test
    fun hasOngoingSession_empty_false() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = DateTimeFormat.localDateTimeUTC().minusMinutes(21),
                sessions = listOf())
        assertThat(practitioner.hasOngoingSession()).isFalse()
    }
    @Test
    fun hasOngoingSession_singleSessionOngoing_true() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = DateTimeFormat.localDateTimeUTC().minusMinutes(21),
                sessions = listOf(SessionDBO(
                        discipline = "d",
                        intention = "i",
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(20))))
       assertThat(practitioner.hasOngoingSession()).isTrue()
    }
    @Test
    fun hasOngoingSession_singleSessionAbandon_false() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = DateTimeFormat.localDateTimeUTC().minusMinutes(182),
                sessions = listOf(SessionDBO(
                        discipline = "d",
                        intention = "i",
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(181))))
        assertThat(practitioner.hasOngoingSession()).isFalse()
    }
    @Test
    fun hasOngoingSession_multipleSessionOngoing_true() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = DateTimeFormat.localDateTimeUTC().minusMinutes(21),
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(20)),
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(15))))
        assertThat(practitioner.hasOngoingSession()).isTrue()
    }
    @Test
    fun hasOngoingSession_singleSessionClosed_false() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = DateTimeFormat.localDateTimeUTC().minusMinutes(21),
                sessions = listOf(SessionDBO(
                        discipline = "d",
                        intention = "i",
                        startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(20),
                        endTime = DateTimeFormat.localDateTimeUTC())))
        assertThat(practitioner.hasOngoingSession()).isFalse()
    }
    @Test
    fun hasOngoingSession_multipleSessionSecondLastOngoing_false() {
        val practitioner = PractitionerDBO(
                _id = "abc",
                created = DateTimeFormat.localDateTimeUTC().minusMinutes(21),
                sessions = listOf(
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(20)),
                        SessionDBO(
                                discipline = "d",
                                intention = "i",
                                startTime = DateTimeFormat.localDateTimeUTC().minusMinutes(15),
                                endTime = DateTimeFormat.localDateTimeUTC())))
        assertThat(practitioner.hasOngoingSession()).isFalse()
    }

    /*******************
     * Spirit bank log *
     *******************/
    @Test
    fun spiritBankLog_startPoints_50() {
        val practitioner = PractitionerDBO()
        assertThat(practitioner.spiritBankLog[0].points).isEqualTo(50)
    }
    @Test
    fun spiritBankPoints_totalPointsNoTransactions_50() {
        val practitioner = PractitionerDBO()
        assertThat(practitioner.calculateSpiritBankPointsFromLog()).isEqualTo(50)
    }
    @Test
    fun spiritBankPoints_totalPointsPlusTransaction_53() {
        val practitioner = PractitionerDBO(
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = 50),
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.ENDED_SESSION, points = 3)))
        assertThat(practitioner.calculateSpiritBankPointsFromLog()).isEqualTo(53)
    }
    @Test
    fun spiritBankPoints_totalPointsSubtractTransaction_47() {
        val practitioner = PractitionerDBO(
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = 50),
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.JOINED_CIRCLE, points = -3)))
        assertThat(practitioner.calculateSpiritBankPointsFromLog()).isEqualTo(47)
    }
    @Test
    fun spiritBankPoints_totalPointsBothPlusAndSubtractTransactions_47() {
        val practitioner = PractitionerDBO(
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = 50),
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.JOINED_CIRCLE, points = 53),
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.JOINED_CIRCLE, points = -3)))
        assertThat(practitioner.calculateSpiritBankPointsFromLog()).isEqualTo(100)
    }

}