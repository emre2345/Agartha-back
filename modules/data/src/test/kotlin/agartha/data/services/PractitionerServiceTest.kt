package agartha.data.services

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions
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
     * Insert a user with single session in database
     */
    private fun putUserInDatabase(sessionStart: String, sessionEnd: String) {
        PractitionerService().insert(
                PractitionerDBO(sessions = listOf(
                        SessionDBO(null,"Yoga", "Love",
                                DateTimeFormat.stringToLocalDateTime(sessionStart), DateTimeFormat.stringToLocalDateTime(sessionEnd)))))
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
        val firstUser : PractitionerDBO? = PractitionerService().getAll().firstOrNull()
        // Throw exception if firstUser is null
        assertThat(firstUser!!.created.dayOfMonth).isEqualTo(18)
    }

    /**
     *
     */
    @Test
    fun insertUserWithSessions_collectionSize_1() {
        PractitionerService().insert(PractitionerDBO(sessions = listOf(
                SessionDBO(null,"Yoga", "Love"),
                SessionDBO(null,"Meditation", "Love"))))
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
        val user = PractitionerDBO(sessions = listOf(SessionDBO(null,"Test 1", "TestIntention 1")))
        // Insert a new practitioning user
        val item = PractitionerService().insert(user)
        // Start session
        val session = PractitionerService().startSession(item._id!!, null, "Test 2", "TestIntention 2")
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
        PractitionerService().startSession(item._id!!, null, "Test 1", "Testing 1")
        PractitionerService().startSession(item._id!!, null, "Test 2","Testing 2")
        PractitionerService().startSession(item._id!!, null, "Test 3","Testing 3")
        // Get user and Count sessions
        val practitioner = PractitionerService().getById(item._id!!)
        Assertions.assertThat(practitioner?.sessions?.size).isEqualTo(3)
    }

    /**
     *
     */
    @Test
    fun endSession_userIdMissing_false() {
        val response = PractitionerService().endSession("AnIdNotExisting")
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
        val response = PractitionerService().endSession(practitioner._id!!)
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
        PractitionerService().startSession(practitioner._id!!, null, "Test 1", "Testing 1")
        val response = PractitionerService().endSession(practitioner._id!!)
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
        PractitionerService().startSession(practitioner._id!!, null, "Test 1", "Testing 1")
        PractitionerService().startSession(practitioner._id!!, null, "Test 1", "Testing 1")
        PractitionerService().startSession(practitioner._id!!, null, "Test 1", "Testing 1")
        // End session (should end the last
        PractitionerService().endSession(practitioner._id!!)
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
        PractitionerService().startSession(practitioner._id!!, null, "Test 1", "Testing 1")
        PractitionerService().startSession(practitioner._id!!, null, "Test 1", "Testing 1")
        PractitionerService().startSession(practitioner._id!!, null, "Test 1", "Testing 1")
        // End session (should end the last
        PractitionerService().endSession(practitioner._id!!)
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
        PractitionerService().startSession(practitioner._id!!, null, "Test 1", "Testing 1")
        PractitionerService().startSession(practitioner._id!!, null, "Test 1", "Testing 1")
        PractitionerService().startSession(practitioner._id!!, null, "Test 1", "Testing 1")
        // End session (should end the last
        PractitionerService().endSession(practitioner._id!!)
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
        PractitionerService().startSession(practitioner._id!!, null, "Test 1", "Testing 1")
        PractitionerService().endSession(practitioner._id!!)
        // Get from database
        val item = PractitionerService().getById(practitioner._id!!)
        assertThat(item!!.sessions.last().endTime).isNotNull()
    }
}