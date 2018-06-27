package agartha.data.services

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Purpose of this file is to test practitioner service
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class PractitionerServiceTest : DatabaseHandler() {

    /**
     *
     */
    @Before
    fun setupBeforeFunctions() {
        dropCollection(CollectionNames.PRACTITIONER_SERVICE)
    }

    /**
     * Find
     */
    @Test
    fun findUser_userId_findUserId() {
        val user = PractitionerService().insert(PractitionerDBO())
        val findUser = PractitionerService().getById(user._id!!)
        assertThat(user._id).isEqualTo(findUser!!._id)
    }

    /**
     * Insert
     */
    @Test
    fun insertUser_collectionSize_1() {
        PractitionerService().insert(PractitionerDBO())
        val allUsers = PractitionerService().getAll()
        assertThat(allUsers.size).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun insertUser_dateSavedCorrect_18() {
        val date = LocalDateTime.parse("2018-04-18 12:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC")))
        PractitionerService().insert(PractitionerDBO(created = date, sessions = listOf()))
        val firstUser: PractitionerDBO? = PractitionerService().getAll().firstOrNull()
        // Throw exception if firstUser is null
        assertThat(firstUser!!.created.dayOfMonth).isEqualTo(18)
    }

    /**
     *
     */
    @Test
    fun insertUserWithSessions_collectionSize_1() {
        PractitionerService().insert(PractitionerDBO(sessions = listOf(
                SessionDBO(null, "Yoga", "Love"),
                SessionDBO(null, "Meditation", "Love"))))
        val allUsers = PractitionerService().getAll()
        assertThat(allUsers.size).isEqualTo(1)
    }

    /**
     * updatePractitioner
     */
    @Test
    fun updateUserWithNewInvolvedInformation_updatedUser_insertedUser() {
        // Insert a new user
        val insertedUser: PractitionerDBO = PractitionerService().insert(PractitionerDBO())
        // Update user
        val updatedUser: PractitionerDBO = PractitionerService()
                .updatePractitionerWithInvolvedInformation(
                        insertedUser,
                        "Santa Clause",
                        "santa@agartha.com",
                        "Jag gillar yoga!")

        // Find the inserted user
        val newUpdatedUser: PractitionerDBO? = PractitionerService().getById(insertedUser._id.toString())
        assertThat(newUpdatedUser).isEqualTo(updatedUser)
    }

    /**
     * Add session
     */
    @Test
    fun startSession_disciplineName_Test2() {
        val user = PractitionerDBO(sessions = listOf(SessionDBO(null, "Test 1", "TestIntention 1")))
        // Insert a new practitioner
        val practitioner = PractitionerService().insert(user)
        // Start session
        val session = PractitionerService().startSession(practitioner, SessionDBO(null, "Test 2", "TestIntention 2"))
        assertThat(session.discipline).isEqualTo("Test 2")
    }

    /**
     *
     */
    @Test
    fun startSession_sessionsSize_3() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val practitioner = PractitionerService().insert(user)
        // Insert sessions
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 2", "Testing 2"))
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 3", "Testing 3"))
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        // Get user and Count sessions
        val newPractitioner = PractitionerService().getById(practitioner._id!!)
        assertThat(newPractitioner?.sessions?.size).isEqualTo(3)
    }

    @Test
    fun circle_size_2() {
        val user = PractitionerService().insert(
                PractitionerDBO(
                        sessions = listOf(
                                SessionDBO(
                                        discipline = "Yoga",
                                        intention = "Love",
                                        startTime = DateTimeFormat.localDateTimeUTC().minusHours(2),
                                        endTime = DateTimeFormat.localDateTimeUTC().minusHours(1))),
                        circles = listOf(
                                CircleDBO(
                                        name = "",
                                        description = "",
                                        startTime = DateTimeFormat.localDateTimeUTC().plusMinutes(15),
                                        endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(45),
                                        intentions = listOf(),
                                        disciplines = listOf(),
                                        minimumSpiritContribution = 12,
                                        language = "Swedish"))))
        val practitioner = PractitionerService().getById(user._id!!)
        assertThat(practitioner?.circles?.size).isEqualTo(1)
    }

    /***************
     * end session *
     ***************/
    @Test
    fun endSession_userIdMissing_false() {
        val response = PractitionerService().endSession("AnIdNotExisting", 0)
        assertThat(response).isNull()
    }

    /**
     *
     */
    @Test
    fun endSession_userHasNoSessions_false() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val practitioner = PractitionerService().insert(user)
        val response = PractitionerService().endSession(practitioner._id!!, 0)
        assertThat(response).isEqualTo(practitioner)
    }

    /**
     *
     */
    @Test
    fun endSession_userHasSessions_true() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val practitioner = PractitionerService().insert(user)
        // Insert sessions
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        val response = PractitionerService().endSession(practitioner._id!!, 0)
        assertThat(response!!.sessions.last().endTime).isNotNull()
    }

    /**
     *
     */
    @Test
    fun endSession_sessionIsReplacedListSize_3() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val practitioner = PractitionerService().insert(user)
        // Start three session
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        // End session (should end the last
        PractitionerService().endSession(practitioner._id!!, 0)
        // Session should be poped and pushed
        val item = PractitionerService().getById(practitioner._id!!)
        assertThat(item!!.sessions.size).isEqualTo(3)
    }

    /**
     *
     */
    @Test
    fun endSession_secondItemNotEnded_Null() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val practitioner = PractitionerService().insert(user)
        // Start three session
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        // End session (should end the last
        PractitionerService().endSession(practitioner._id!!, 0)
        // Session should be poped and pushed
        val item = PractitionerService().getById(practitioner._id!!)
        // Only the last session should be ended
        assertThat(item!!.sessions.get(1).endTime).isNull()
    }

    /**
     *
     */
    @Test
    fun endSession_lastItemEnded_notNull() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val practitioner = PractitionerService().insert(user)
        // Start three session
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().startSession(practitioner,SessionDBO( null, "Test 1", "Testing 1"))
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        // End session (should end the last
        PractitionerService().endSession(practitioner._id!!, 0)
        // Session should be poped and pushed
        val item = PractitionerService().getById(practitioner._id!!)
        // Only the last session should be ended
        assertThat(item!!.sessions.get(2).endTime).isNotNull()
    }

    /**
     *
     */
    @Test
    fun endSession_endTimeIsSet_notNull() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val practitioner = PractitionerService().insert(user)
        // Insert sessions
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().endSession(practitioner._id!!, 0)
        // Get from database
        val item = PractitionerService().getById(practitioner._id!!)
        assertThat(item!!.sessions.last().endTime).isNotNull()
    }

    /**
     *
     */
    @Test
    fun endSession_contributionPointsStored_pointsIs7() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val practitioner = PractitionerService().insert(user)
        // Insert sessions
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().endSession(practitioner._id!!, 7)
        // Get from database
        val item = PractitionerService().getById(practitioner._id!!)
        assertThat(item!!.spiritBankLog.last().points).isEqualTo(7)
    }

    /**
     *
     */
    @Test
    fun endSession_contributionPointsStored_typeIsSession() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val practitioner = PractitionerService().insert(user)
        // Insert sessions
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().endSession(practitioner._id!!, 7)
        // Get from database
        val item = PractitionerService().getById(practitioner._id!!)
        assertThat(item!!.spiritBankLog.last().type).isEqualTo(SpiritBankLogItemType.ENDED_SESSION)
    }


    /**
     *
     */
    @Test
    fun endSession_inCircleContributionPointsCalculated_11points() {
        // Insert a new practising user
        val practitioner = PractitionerService().insert(PractitionerDBO(_id = "a"))
        val secondPractitioner = PractitionerService().insert(PractitionerDBO(_id = "b"))
        val circle = CircleDBO(
                name = "",
                description = "",
                startTime = DateTimeFormat.localDateTimeUTC(),
                endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(15),
                disciplines = listOf(),
                intentions = listOf(),
                minimumSpiritContribution = 4,
                language = "Swedish",
                virtualRegistered = 3)
        // Insert circle to practitioner
        PractitionerService().addCircle("a", circle)
        // Start session with the created circle
        PractitionerService().startSession(practitioner, SessionDBO(
                null,
                "Test 1",
                "Testing 1",
                startTime = DateTimeFormat.localDateTimeUTC(),
                circle = circle))
        // Start session for second practitioner with the created circle
        PractitionerService().startSession(secondPractitioner, SessionDBO(
                null,
                "Test 2",
                "Testing 2",
                startTime = DateTimeFormat.localDateTimeUTC(),
                circle = circle))
        // End session for circle creator
        PractitionerService().endSession("a", 7)
        // Get from database
        val item = PractitionerService().getById("a")
        assertThat(item!!.spiritBankLog.last().type).isEqualTo(SpiritBankLogItemType.ENDED_CREATED_CIRCLE)
    }

    /**
     *
     */
    @Test
    fun endSession_inCircleContributionPointsStored_typeIsEndedCreatedCircle() {
        // Insert a new practising user
        val practitioner = PractitionerService().insert(PractitionerDBO(_id = "a"))
        val secondPractitioner = PractitionerService().insert(PractitionerDBO(_id = "b"))
        val circle = CircleDBO(
                name = "",
                description = "",
                startTime = DateTimeFormat.localDateTimeUTC(),
                endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(15),
                disciplines = listOf(),
                intentions = listOf(),
                minimumSpiritContribution = 3,
                language = "Swedish",
                virtualRegistered = 3)
        // Insert circle to practitioner
        PractitionerService().addCircle("a", circle)
        // Start session with the created circle
        PractitionerService().startSession(practitioner, SessionDBO(
                null,
                "Test 1",
                "Testing 1",
                startTime = DateTimeFormat.localDateTimeUTC(),
                circle = circle))
        // Start session for second practitioner with the created circle
        PractitionerService().startSession(secondPractitioner, SessionDBO(
                null,
                "Test 2",
                "Testing 2",
                startTime = DateTimeFormat.localDateTimeUTC(),
                circle = circle))
        // End session for circle creator
        PractitionerService().endSession("a", 7)
        // Get from database
        val item = PractitionerService().getById("a")
        assertThat(item!!.spiritBankLog.last().points).isEqualTo(10)
    }

    /**
     *
     */
    @Test
    fun endSession_creatorOfCircle_circleEndTime() {
        // Insert a new practising user
        val practitioner = PractitionerService().insert(PractitionerDBO(_id = "a"))
        val circleEndTime = DateTimeFormat.localDateTimeUTC().plusMinutes(15)
        val circle = CircleDBO(
                name = "",
                description = "",
                startTime = DateTimeFormat.localDateTimeUTC(),
                endTime = circleEndTime,
                disciplines = listOf(),
                intentions = listOf(),
                minimumSpiritContribution = 3,
                language = "Swedish")
        // Insert circle to practitioner
        PractitionerService().addCircle("a", circle)
        // Start session with the created circle
        PractitionerService().startSession(practitioner, SessionDBO(
                null,
                "Test 1",
                "Testing 1",
                startTime = DateTimeFormat.localDateTimeUTC(),
                circle = circle))
        // End session for circle creator
        PractitionerService().endSession("a", 7)
        // Get from database
        val updatedPractitioner = PractitionerService().getById("a")
        assertThat(updatedPractitioner!!.circles[0].endTime).isNotEqualTo(circleEndTime)
    }

    /**
     *
     */
    @Test
    fun endSession_contributionPointsStored_storedOneNewLog() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val practitioner = PractitionerService().insert(user)
        // Insert sessions
        PractitionerService().startSession(practitioner, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().endSession(practitioner._id!!, 7)
        // Get from database
        val item = PractitionerService().getById(practitioner._id!!)
        assertThat(item!!.spiritBankLog.size).isEqualTo(2)
    }

    /*************************
     * add registered circle *
     *************************/
    @Test
    fun addRegisteredCircle_responsePractitionerRegisteredCircles_1() {
        // Insert a new practising user
        val practitioner = PractitionerService().insert(PractitionerDBO())
        // Add registered Circle
        val circlePractitioner = PractitionerService().addRegisteredCircle(practitioner._id!!, "1")
        //
        assertThat(circlePractitioner!!.registeredCircles.size).isEqualTo(1)
    }

    /**************
     * add circle *
     **************/
    @Test
    fun addCircle_responsePractitionerCircles_1() {
        // Insert a new practising user
        val practitioner = PractitionerService().insert(PractitionerDBO())
        // Add Circle
        val circlePractitioner = PractitionerService().addCircle(practitioner._id!!, CircleDBO(
                name = "",
                description = "",
                startTime = DateTimeFormat.localDateTimeUTC(),
                endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(15),
                disciplines = listOf(),
                intentions = listOf(),
                minimumSpiritContribution = 4,
                language = "Swedish"))

        assertThat(circlePractitioner!!.circles.size).isEqualTo(1)
    }

    /***************
     * edit circle *
     ***************/
    @Test
    fun editCircle_responseCircleName_EditedName() {
        val circle = CircleDBO(
                _id = "1",
                name = "",
                description = "",
                startTime = DateTimeFormat.localDateTimeUTC(),
                endTime = DateTimeFormat.localDateTimeUTC().plusMinutes(15),
                disciplines = listOf(),
                intentions = listOf(),
                minimumSpiritContribution = 4,
                language = "Swedish")
        // Insert a new practising user
        val practitioner = PractitionerService().insert(PractitionerDBO())
        // Add Circle
        PractitionerService().addCircle(practitioner._id!!, circle)
        // Edit the circle
        val editCircle = CircleDBO(
                _id = "1",
                name = "Edited Name",
                description = circle.description,
                startTime = circle.startTime,
                endTime = circle.endTime,
                disciplines = circle.disciplines,
                intentions = circle.intentions,
                minimumSpiritContribution = circle.minimumSpiritContribution,
                language = circle.language)

        val circlePractitioner = PractitionerService().editCircle(practitioner._id!!, editCircle)

        assertThat(circlePractitioner!!.circles.last().name).isEqualTo("Edited Name")
    }

    /**************
     * remove all *
     **************/
    @Test
    fun removeAll_dataCount_0() {
        // Insert a new practising user
        PractitionerService().insert(PractitionerDBO())
        // Remove all users
        PractitionerService().removeAll()
        assertThat(PractitionerService().getAll().size).isEqualTo(0)
    }

    /*******************
     * removeGenerated *
     *******************/
    @Test
    fun removeGenerated_dataCount_1() {
        // Insert a new generated user and normal user
        PractitionerService().insert(PractitionerDBO(description = "Generated Practitioner"))
        PractitionerService().insert(PractitionerDBO())
        // Remove all generated users
        PractitionerService().removeGenerated()
        assertThat(PractitionerService().getAll().size).isEqualTo(1)
    }

    /**************
     * removeById *
     **************/
    @Test
    fun removeById_itemExists_true() {
        val practitioner = PractitionerService().insert(PractitionerDBO())
        val response = PractitionerService().removeById(practitioner._id ?: "")
        assertThat(response).isTrue()
    }

    @Test
    fun removeById_itemNotExists_false() {
        val response = PractitionerService().removeById("ThisIdDoesNotExistInDB")
        assertThat(response).isFalse()
    }

    /****************
     * removeCircle *
     ****************/
    @Test
    fun removeCircle_practitionerMissing_false() {
        PractitionerService().insert(PractitionerDBO(_id = "p1"))
        val response = PractitionerService().removeCircleById("p2", "c1")
        assertThat(response).isFalse()
    }

    @Test
    fun removeCircle_circleMissing_false() {
        PractitionerService().insert(PractitionerDBO(_id = "p1"))
        val response = PractitionerService().removeCircleById("p1", "c1")
        assertThat(response).isFalse()
    }

    private fun generateCirclePractitioner(): PractitionerDBO {
        return PractitionerDBO(
                _id = "p1",
                circles = listOf(
                        CircleDBO(
                                _id = "c1", name = "", description = "",
                                startTime = DateTimeFormat.localDateTimeUTC(), endTime = DateTimeFormat.localDateTimeUTC().plusHours(1),
                                intentions = listOf(), disciplines = listOf(), minimumSpiritContribution = 3,
                                language = "Swedish"),
                        CircleDBO(
                                _id = "c2", name = "", description = "",
                                startTime = DateTimeFormat.localDateTimeUTC(), endTime = DateTimeFormat.localDateTimeUTC().plusHours(1),
                                intentions = listOf(), disciplines = listOf(), minimumSpiritContribution = 3,
                                language = "Swedish"),
                        CircleDBO(
                                _id = "c3", name = "", description = "",
                                startTime = DateTimeFormat.localDateTimeUTC(), endTime = DateTimeFormat.localDateTimeUTC().plusHours(1),
                                intentions = listOf(), disciplines = listOf(), minimumSpiritContribution = 3,
                                language = "Swedish")))
    }


    @Test
    fun removeCircle_firstOfMany_true() {
        PractitionerService().insert(generateCirclePractitioner())
        val response = PractitionerService().removeCircleById("p1", "c1")
        assertThat(response).isTrue()
    }

    @Test
    fun removeCircle_middleOfMany_true() {
        PractitionerService().insert(generateCirclePractitioner())
        val response = PractitionerService().removeCircleById("p1", "c2")
        assertThat(response).isTrue()
    }

    @Test
    fun removeCircle_lastOfMany_true() {
        PractitionerService().insert(generateCirclePractitioner())
        val response = PractitionerService().removeCircleById("p1", "c3")
        assertThat(response).isTrue()
    }

    @Test
    fun removeCircle_oneOfThree_twoLeft() {
        PractitionerService().insert(generateCirclePractitioner())
        PractitionerService().removeCircleById("p1", "c2")
        val practitioner = PractitionerService().getById("p1")
        assertThat(practitioner!!.circles.size).isEqualTo(2)
    }

    @Test
    fun removeCircle_retrieveRemovedOne_null() {
        PractitionerService().insert(generateCirclePractitioner())
        PractitionerService().removeCircleById("p1", "c2")
        val practitioner = PractitionerService().getById("p1")
        val removedCircle = practitioner!!.circles.filter { it._id == "c2" }.firstOrNull()
        assertThat(removedCircle).isNull()
    }

    /*******************************
     * payForAddingVirtualSessions *
     *******************************/
    private fun generatePractitionerWithPoints(): PractitionerDBO {
        return PractitionerDBO(
                _id = "p1",
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = 50)))
    }
    @Test
    fun payForAddingVirtualSessions_practitionerCanAfford_true() {
        val practitioner = generatePractitionerWithPoints()
        PractitionerService().insert(practitioner)
        val wentFine = PractitionerService().payForAddingVirtualSessions(practitioner, 3)
        assertThat(wentFine).isTrue()
    }
    @Test
    fun payForAddingVirtualSessions_practitionerCanAfford_practitionerSpiritBankPoints35() {
        val practitioner = generatePractitionerWithPoints()
        PractitionerService().insert(practitioner)
        PractitionerService().payForAddingVirtualSessions(practitioner, 3)
        val points = PractitionerService().getById(practitioner._id!!)!!.calculateSpiritBankPointsFromLog()
        assertThat(points).isEqualTo(35)
    }
    @Test
    fun payForAddingVirtualSessions_practitionerCanAfford_newLogInSpiritBankType() {
        val practitioner = generatePractitionerWithPoints()
        PractitionerService().insert(practitioner)
        PractitionerService().payForAddingVirtualSessions(practitioner, 3)
        val logItem = PractitionerService().getById(practitioner._id!!)!!.spiritBankLog.last()
        assertThat(logItem.type).isEqualTo(SpiritBankLogItemType.ADD_VIRTUAL_TO_CIRCLE)
    }
    @Test
    fun payForAddingVirtualSessions_practitionerCanNotAfford_false() {
        val practitioner = PractitionerDBO(
                _id = "p1",
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = 50),
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.ADD_VIRTUAL_TO_CIRCLE, points = -45)
                        ))
        PractitionerService().insert(practitioner)
        val wentFine = PractitionerService().payForAddingVirtualSessions(practitioner, 3)
        assertThat(wentFine).isFalse()
    }

    /***********************************************
     * checkPractitionerCanAffordVirtualRegistered *
     ***********************************************/
    @Test
    fun checkPractitionerCanAffordVirtualRegistered_practitionerCanAfford_true() {
        val practitioner = generatePractitionerWithPoints()
        PractitionerService().insert(practitioner)
        val canAfford = PractitionerService().checkPractitionerCanAffordVirtualRegistered(practitioner, 3)
        assertThat(canAfford).isTrue()
    }
    @Test
    fun checkPractitionerCanAffordVirtualRegistered_practitionerCanNotAfford_false() {
        val practitioner = PractitionerDBO(
                _id = "p1",
                spiritBankLog = listOf(
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.START, points = 50),
                        SpiritBankLogItemDBO(type = SpiritBankLogItemType.ADD_VIRTUAL_TO_CIRCLE, points = -45)
                ))
        PractitionerService().insert(practitioner)
        val wentFine = PractitionerService().checkPractitionerCanAffordVirtualRegistered(practitioner, 3)
        assertThat(wentFine).isFalse()
    }
}