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
                PractitionerDBO(sessions = listOf(
                        SessionDBO(0, "Yoga", false, DateTimeFormat.stringToLocalDateTime(sessionStart), DateTimeFormat.stringToLocalDateTime(sessionEnd)))))
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
                SessionDBO(0, "Yoga"), SessionDBO(1, "Meditation"))))
        val allUsers = PractitionerService().getAll()
        assertThat(allUsers.size).isEqualTo(1)
    }

    /**
     * Update
     */
    @Test
    fun updateUserWithNewInvolvedData_updatedUser_insertedUser() {
        // Insert a new user
        val insertedUser = PractitionerService().insert(PractitionerDBO())
        // Create a new user with the inserted users information an change some data
        val updatedUser = PractitionerDBO(insertedUser._id, insertedUser.created, fullName = "Rebecca Fransson", email = "rebecca@kollektiva.se", description = "Hej, jag gillar också yoga!")
        // Update inserted user
        PractitionerService().updatePractitioner(insertedUser._id!!, updatedUser)
        // Find the inserted user
        val newUpdatedUser = PractitionerService().getById(insertedUser._id.toString())
        assertThat(newUpdatedUser).isEqualTo(updatedUser)
    }
}