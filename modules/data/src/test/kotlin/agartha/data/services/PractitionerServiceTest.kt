package agartha.data.services

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class PractitionerServiceTest : DatabaseHandler() {

    @Before
    fun setupBeforeFunctions() {
//        dropCollection(CollectionNames.PRACTITIONER_SERVICE)
    }


    @Test
    fun hovno() {
        val p = PractitionerDBO(listOf<SessionDBO>())
        val item = PractitionerService().insert(p)
        //
        // Insert session
        PractitionerService().insertSession(item._id as String, SessionDBO("Test"))
        assertThat(item._id).isNotNull()
    }
}