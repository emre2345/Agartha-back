package agartha.data.services

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
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
class PractitionerAndSessionServiceTest : DatabaseHandler() {

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
        PractitionerAndSessionService().insert(
                PractitionerDBO(sessions = listOf(
                        SessionDBO(0, "Yoga", false, DateTimeFormat.stringToLocalDateTime(sessionStart), DateTimeFormat.stringToLocalDateTime(sessionEnd)))))
    }

    /**
     * Find
     */
    @Test
    fun findUser_userId_findUserId() {
        val user = PractitionerAndSessionService().insert(PractitionerDBO())
        val findUser = PractitionerAndSessionService().getById(user._id!!)
        assertThat(user._id).isEqualTo(findUser!!._id)
    }

    /**
     * Insert
     */
    @Test
    fun insertUser_collectionSize_1() {
        PractitionerAndSessionService().insert(PractitionerDBO())
        val allUsers = PractitionerAndSessionService().getAll()
        assertThat(allUsers.size).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun insertUser_dateSavedCorrect_18() {
        val date = LocalDateTime.parse("2018-04-18 12:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        PractitionerAndSessionService().insert(PractitionerDBO(created = date, sessions = listOf()))
        val firstUser : PractitionerDBO? = PractitionerAndSessionService().getAll().firstOrNull()
        // Throw exception if firstUser is null
        assertThat(firstUser!!.created.dayOfMonth).isEqualTo(18)
    }

    /**
     *
     */
    @Test
    fun insertUserWithSessions_collectionSize_1() {
        PractitionerAndSessionService().insert(PractitionerDBO(sessions = listOf(
                SessionDBO(0, "Yoga"), SessionDBO(1, "Meditation"))))
        val allUsers = PractitionerAndSessionService().getAll()
        assertThat(allUsers.size).isEqualTo(1)
    }

    /**
     * Update
     */
    @Test
    fun updateUserWithNewInvolvedData_updatedUser_insertedUser() {
        // Insert a new user
        val insertedUser = PractitionerAndSessionService().insert(PractitionerDBO())
        // Create a new user with the inserted users information an change some data
        val updatedUser = PractitionerDBO(insertedUser._id, insertedUser.created, fullName = "Rebecca Fransson", email = "rebecca@kollektiva.se", description = "Hej, jag gillar också yoga!")
        // Update inserted user
        PractitionerAndSessionService().update(insertedUser._id!!, updatedUser)
        // Find the inserted user
        val newUpdatedUser = PractitionerAndSessionService().getById(insertedUser._id.toString())
        assertThat(newUpdatedUser).isEqualTo(updatedUser)
    }

    /**
     * Add session
     */
    @Test
    fun addSessionToUser_IndexReturned_1() {
        val user = PractitionerDBO(sessions = listOf(SessionDBO(0, "Test")))
        // Insert a new practisioning user
        val item = PractitionerAndSessionService().insert(user)
        // Insert session
        val sessionId = PractitionerAndSessionService().startSession(item._id!!, "Test")
        assertThat(sessionId).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun addSessionsToUser_sessionsSize_3() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val item = PractitionerAndSessionService().insert(user)
        // Insert sessions
        PractitionerAndSessionService().startSession(item._id!!, "Test 1")
        PractitionerAndSessionService().startSession(item._id!!, "Test 2")
        PractitionerAndSessionService().startSession(item._id!!, "Test 3")
        // Get user and Count sessions
        val practitioner = PractitionerAndSessionService().getById(item._id!!)
        assertThat(practitioner?.sessions?.size).isEqualTo(3)
    }

    @Test
    fun practitioners_withMatchingSessions_3() {
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


        val list = PractitionerAndSessionService().getPractitionersWithSessionBetween(
                DateTimeFormat.stringToLocalDateTime("2018-04-15 19:00:00"),
                DateTimeFormat.stringToLocalDateTime("2018-04-15 21:00:00"))

        // Count 'em
        assertThat(list.size).isEqualTo(3)
    }
}