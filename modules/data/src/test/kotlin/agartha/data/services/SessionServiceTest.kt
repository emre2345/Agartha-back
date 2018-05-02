package agartha.data.services

import agartha.common.utils.DateTimeFormat
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test


/**
 * Purpose of this file is to test session service
 *
 * Created by Jorgen Andersson on 2018-04-25.
 */
class SessionServiceTest : DatabaseHandler() {

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
        SessionService().insert(
                PractitionerDBO(sessions = listOf(
                        SessionDBO(0, null,"Yoga", "Mindfulness", "Salary raise",
                                DateTimeFormat.stringToLocalDateTime(sessionStart), DateTimeFormat.stringToLocalDateTime(sessionEnd)))))
    }

    /**
     * Add session
     */
    @Test
    fun addSessionToUser_IndexReturned_1() {
        val user = PractitionerDBO(sessions = listOf(SessionDBO(0, null,"Test", "Testis", "TestIntention")))
        // Insert a new practitioning user
        val item = SessionService().insert(user)
        // Insert session
        val sessionId = SessionService().startSession(item._id!!, null, "Test", "Testis", "TestIntention")
        assertThat(sessionId).isEqualTo(1)
    }

    /**
     * Add session
     */
    @Test
    fun addSessionWithoutPracticeToUser_IndexReturned_1() {
        val user = PractitionerDBO(sessions = listOf(SessionDBO(0, null,"Test", null, "TestIntention")))
        // Insert a new practitioning user
        val item = SessionService().insert(user)
        // Insert session
        val sessionId = SessionService().startSession(item._id!!, null, "Test",  null, "TestIntention")
        assertThat(sessionId).isEqualTo(1)
    }
    /**
     *
     */
    @Test
    fun addSessionsToUser_sessionsSize_3() {
        val user = PractitionerDBO()
        // Insert a new practising user
        val item = SessionService().insert(user)
        // Insert sessions
        SessionService().startSession(item._id!!, null, "Test 1", "Testis 1", "Testing 1")
        SessionService().startSession(item._id!!, null, "Test 2", "Testis 2","Testing 2")
        SessionService().startSession(item._id!!, null, "Test 3", "Testis 3","Testing 3")
        // Get user and Count sessions
        val practitioner = SessionService().getById(item._id!!)
        Assertions.assertThat(practitioner?.sessions?.size).isEqualTo(3)
    }

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
        val list = SessionService().getPractitionersWithSessionAfter(
                DateTimeFormat.stringToLocalDateTime("2018-04-15 19:00:00"))
        // Count 'em
        Assertions.assertThat(list.size).isEqualTo(4)
    }
}