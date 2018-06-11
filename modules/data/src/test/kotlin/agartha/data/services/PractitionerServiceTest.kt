package agartha.data.services

import agartha.data.objects.CircleDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.objects.SpiritBankLogItemType
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
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
        val date = LocalDateTime.parse("2018-04-18 12:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
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
        val item = PractitionerService().insert(user)
        // Start session
        val session = PractitionerService().startSession(item._id!!, SessionDBO(null, "Test 2", "TestIntention 2"))
        assertThat(session.discipline).isEqualTo("Test 2")
    }

    /**
     *
     */
    @Test
    fun startSession_sessionsSize_3() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val item = PractitionerService().insert(user)
        // Insert sessions
        PractitionerService().startSession(item._id!!, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().startSession(item._id!!, SessionDBO(null, "Test 2", "Testing 2"))
        PractitionerService().startSession(item._id!!, SessionDBO(null, "Test 3", "Testing 3"))
        // Get user and Count sessions
        val practitioner = PractitionerService().getById(item._id!!)
        assertThat(practitioner?.sessions?.size).isEqualTo(3)
    }

    @Test
    fun circle_size_2() {
        val user = PractitionerService().insert(
                PractitionerDBO(
                        sessions = listOf(
                                SessionDBO(
                                        discipline = "Yoga",
                                        intention = "Love",
                                        startTime = LocalDateTime.now().minusHours(2),
                                        endTime = LocalDateTime.now().minusHours(1))),
                        circles = listOf(
                                CircleDBO(
                                        name = "",
                                        description = "",
                                        startTime = LocalDateTime.now().plusMinutes(15),
                                        endTime = LocalDateTime.now().plusMinutes(45),
                                        intentions = listOf(),
                                        disciplines = listOf(),
                                        minimumSpiritContribution = 12))))
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
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
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
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
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
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
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
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().startSession(practitioner._id!!,SessionDBO( null, "Test 1", "Testing 1"))
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
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
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
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
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
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
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().endSession(practitioner._id!!, 7)
        // Get from database
        val item = PractitionerService().getById(practitioner._id!!)
        assertThat(item!!.spiritBankLog.last().type).isEqualTo(SpiritBankLogItemType.SESSION)
    }

    /**
     *
     */
    fun addCircle_responsePractitionerCircles_1() {
        // Insert a new practising user
        val practitioner = PractitionerService().insert(PractitionerDBO())
        // Add Circle
        val circlePractitioner = PractitionerService().addCircle(practitioner._id!!, CircleDBO(
                name = "",
                description = "",
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now().plusMinutes(15),
                disciplines = listOf(),
                intentions = listOf(),
                minimumSpiritContribution = 4))

        assertThat(circlePractitioner!!.circles.size).isEqualTo(1)
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
        PractitionerService().startSession(practitioner._id!!, SessionDBO(null, "Test 1", "Testing 1"))
        PractitionerService().endSession(practitioner._id!!, 7)
        // Get from database
        val item = PractitionerService().getById(practitioner._id!!)
        assertThat(item!!.spiritBankLog.size).isEqualTo(2)
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

    /**
     *
     */
    @Test
    fun removeGenerated_dataCount_1() {
        // Insert a new generated user and normal user
        PractitionerService().insert(PractitionerDBO(description = "Generated Practitioner"))
        PractitionerService().insert(PractitionerDBO())
        // Remove all generated users
        PractitionerService().removeGenerated()
        assertThat(PractitionerService().getAll().size).isEqualTo(1)
    }

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
}