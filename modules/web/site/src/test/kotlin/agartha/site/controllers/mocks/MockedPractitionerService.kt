package agartha.site.controllers.mocks

import agartha.data.objects.GeolocationDBO
import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.IPractitionerService
import java.time.LocalDateTime

/**
 * Purpose of this file is Mocked service for testing Controller
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class MockedPractitionerService : IPractitionerService {

    private val practitionerList: MutableList<PractitionerDBO> = mutableListOf()

    override fun insert(item: PractitionerDBO): PractitionerDBO {
        practitionerList.add(item)
        return item
    }

    override fun updatePractitionerWithInvolvedInformation(
            user: PractitionerDBO,
            fullName: String,
            email: String,
            description: String): PractitionerDBO {
        val index = practitionerList.indexOf(user)
        user.addInvolvedInformation(fullName, email, description)
        practitionerList[index] = user
        return user
    }

    override fun startSession(
            practitionerId: String,
            geolocation: GeolocationDBO?,
            disciplineName: String,
            intentionName: String): SessionDBO {
        val first = practitionerList
                .filter {
                    it._id.equals(practitionerId)
                }
                .first()

        val nextIndex = first.sessions.count() + 1
        val session = SessionDBO(nextIndex, geolocation, disciplineName, intentionName)
        first.sessions.plus(session)
        return session
    }

    override fun endSession(practitionerId: String): Boolean {
        val last = practitionerList
                .filter {
                    it._id.equals(practitionerId)
                }
                .lastOrNull()
        // Return if we have a last item
        return last != null
    }

    override fun getAll(): List<PractitionerDBO> {
        return practitionerList
    }

    override fun getById(id: String): PractitionerDBO? {
        return practitionerList.find {
            it._id.equals(id)
        }
    }

    /**
     * Return all with no logic (logic is not what we are testing here)
     */
    override fun getPractitionersWithSessionAfter(startDate: LocalDateTime): List<PractitionerDBO> {
        return practitionerList
    }

    /**
     * Clear database
     */
    fun clear() {
        practitionerList.clear()
    }


}