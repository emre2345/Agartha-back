package agartha.site.controllers.mocks

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import agartha.data.services.ISessionService
import java.time.LocalDateTime

/**
 * Purpose of this file is Mocked service for testing Controller
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class MockedSessionService : ISessionService {
    val practitionerList: MutableList<PractitionerDBO> = mutableListOf()


    override fun getById(id: String): PractitionerDBO? {
        if (practitionerList.isEmpty()) {
            return null
        }
        return practitionerList.filter {
            it._id.equals(id)
        }?.first()
    }


    override fun startSession(userId: String, discipline: String, practice: String?, intention: String): Int {
        val first = practitionerList.filter {
            it._id.equals(userId)
        }.first()

        val nextIndex = first.sessions.count() + 1
        first.sessions.plus(SessionDBO(nextIndex, null, discipline, practice, intention))
        return nextIndex

    }
    override fun endSession(userId: String, sessionId: Int) {
        TODO("not implemented because not used") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insert(item: PractitionerDBO): PractitionerDBO {
        practitionerList.add(item)
        return item
    }

    override fun getAll(): List<PractitionerDBO> {
        return practitionerList
    }

    override fun getPractitionersWithSessionAfter(startDateTime: LocalDateTime): List<PractitionerDBO> {
        return practitionerList
    }


    /**
     * Clear database
     */
    fun clear() {
        practitionerList.clear()
    }


}