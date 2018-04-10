package agartha.data.services

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

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
     *
     */
    @Test
    fun practitionerService_insertUser_1() {
        val user = PractitionerService().insert(PractitionerDBO(listOf<SessionDBO>()))
        val allUsers = PractitionerService().getAll()
        assertThat(allUsers.size).isEqualTo(1)
    }

    /**
     *
     */
    @Test
    fun practitionerService_insertSessionIndexReturned_1() {
        val user = PractitionerDBO(listOf<SessionDBO>())
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
    fun practitionerService_insertSessionCount_3() {
        val user = PractitionerDBO(listOf<SessionDBO>())
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