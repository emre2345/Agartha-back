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
                        SessionDBO(0, null,"Yoga", "Mindfulness", "Love",
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
                SessionDBO(0, null,"Yoga", "Mindfulness", "Love"),
                SessionDBO(1, null,"Meditation", "Mindfulness", "Love"))))
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
                        "Rebecca Fransson",
                        "rebecca@kollektiva.se",
                        "Jag gillar yoga!")

        // Find the inserted user
        val newUpdatedUser: PractitionerDBO? = PractitionerService().getById(insertedUser._id.toString())
        assertThat(newUpdatedUser).isEqualTo(updatedUser)
    }

    /**
     * Add session
     */
    @Test
    fun addSessionToUser_IndexReturned_1() {
        val user = PractitionerDBO(sessions = listOf(SessionDBO(0, null,"Test", "Testis", "TestIntention")))
        // Insert a new practitioning user
        val item = PractitionerService().insert(user)
        // Insert session
        val sessionId = PractitionerService().startSession(item._id!!, null, "Test", "Testis", "TestIntention")
        assertThat(sessionId).isEqualTo(1)
    }

    /**
     * Add session
     */
    @Test
    fun addSessionWithoutPracticeToUser_IndexReturned_1() {
        val user = PractitionerDBO(sessions = listOf(SessionDBO(0, null,"Test", null, "TestIntention")))
        // Insert a new practitioning user
        val item = PractitionerService().insert(user)
        // Insert session
        val sessionId = PractitionerService().startSession(item._id!!, null, "Test",  null, "TestIntention")
        assertThat(sessionId).isEqualTo(1)
    }
    /**
     *
     */
    @Test
    fun addSessionsToUser_sessionsSize_3() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val item = PractitionerService().insert(user)
        // Insert sessions
        PractitionerService().startSession(item._id!!, null, "Test 1", "Testis 1", "Testing 1")
        PractitionerService().startSession(item._id!!, null, "Test 2", "Testis 2","Testing 2")
        PractitionerService().startSession(item._id!!, null, "Test 3", "Testis 3","Testing 3")
        // Get user and Count sessions
        val practitioner = PractitionerService().getById(item._id!!)
        Assertions.assertThat(practitioner?.sessions?.size).isEqualTo(3)
    }

    /**
     *
     */
    @Test
    fun practitioners_withMatchingSessions_4() {
        // User with session before search
        putUserInDatabase("2018-04-15 17:00:00", "2018-04-15 18:00:00")
        // User with session end matching
        putUserInDatabase("2018-04-15 18:00:00", "2018-04-15 20:00:00")
        // User with session start and end matching
        putUserInDatabase("2018-04-15 19:30:00", "2018-04-15 20:30:00")
        // User with session start matching
        putUserInDatabase("2018-04-15 20:00:00", "2018-04-15 23:00:00")
        // User with session after search
        putUserInDatabase("2018-04-15 22:00:00", "2018-04-15 23:00:00")
        //
        val list = PractitionerService().getPractitionersWithSessionAfter(
                DateTimeFormat.stringToLocalDateTime("2018-04-15 19:00:00"))
        // Count 'em
        Assertions.assertThat(list.size).isEqualTo(4)
    }

    /**
     *
     */
    @Test
    fun matchSessions_firstSessionPoints_2() {
        Assertions.assertThat(true).isEqualTo(false)
    }
}