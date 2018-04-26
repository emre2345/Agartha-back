package agartha.site.controllers.mocks

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
    val practitionerList: MutableList<PractitionerDBO> = mutableListOf()

    override fun insert(item: PractitionerDBO): PractitionerDBO {
        practitionerList.add(item)
        return item
    }

    override fun updatePractitionerWithInvolvedInformation(user: PractitionerDBO, fullName: String, email: String, description: String): PractitionerDBO {
        val index = practitionerList.indexOf(user)
        user.addInvolvedInformation(fullName, email, description)
        practitionerList[index] = user
        return user
    }

    override fun getAll(): List<PractitionerDBO> {
        return practitionerList
    }

    override fun getById(id: String): PractitionerDBO? {
        if (practitionerList.isEmpty()) {
            return null
        }
        return practitionerList.filter {
            it._id.equals(id)
        }?.first()
    }

    /**
     * Return all with no logic (logic is not what we are testing here)
     */
    override fun getPractitionersWithSessionBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<PractitionerDBO> {
        return practitionerList
    }

    /**
     * Clear database
     */
    fun clear() {
        practitionerList.clear()
    }


}