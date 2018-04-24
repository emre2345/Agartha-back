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
                PractitionerDBO(listOf(
                        SessionDBO(0, "Yoga", false, DateTimeFormat.stringToLocalDateTime(sessionStart), DateTimeFormat.stringToLocalDateTime(sessionEnd)))))
    }

    /**
     *
     */
    @Test
    fun insertUser_collectionSize_1() {
        PractitionerService().insert(PractitionerDBO())
        val allUsers = PractitionerService().getAll()
        assertThat(allUsers.size).isEqualTo(1)
    }

    @Test
    fun insertUser_dateSavedCorrect_18() {
        val date = LocalDateTime.parse("2018-04-18 12:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        PractitionerService().insert(PractitionerDBO(listOf(), date))
        val firstUser : PractitionerDBO? = PractitionerService().getAll().firstOrNull()
        // Throw exception if firstUser is null
        assertThat(firstUser!!.created.dayOfMonth).isEqualTo(18)
    }

    /**
     *
     */
    @Test
    fun insertUserWithSessions_collectionSize_1() {
        PractitionerService().insert(PractitionerDBO(listOf(
                SessionDBO(0, "Yoga"), SessionDBO(1, "Meditation"))))
        val allUsers = PractitionerService().getAll()
        assertThat(allUsers.size).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun addSessionToUser_IndexReturned_1() {
        val user = PractitionerDBO(listOf(SessionDBO(0, "Test")))
        // Insert a new practisioning user
        val item = PractitionerService().insert(user)
        // Insert session
        val sessionId = PractitionerService().startSession(item._id!!, "Test")
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
        PractitionerService().startSession(item._id!!, "Test 1")
        PractitionerService().startSession(item._id!!, "Test 2")
        PractitionerService().startSession(item._id!!, "Test 3")
        // Get user and Count sessions
        val practitioner = PractitionerService().getById(item._id!!)
        assertThat(practitioner?.sessions?.size).isEqualTo(3)
    }

}