package agartha.site.controllers.mocks

import agartha.data.objects.*
import agartha.data.services.IPractitionerService
import java.time.LocalDateTime


/**
 * Purpose of this file is Mocked service for testing Controller
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
class MockedPractitionerService : IPractitionerService {
    /**
     * Overloading a new function to the list
     */
    fun MutableList<PractitionerDBO>.removeGenerated(): Boolean {
        val item = this.find { it.description == "Generated Practitioner" }
        return this.remove(item)
    }

    fun MutableList<PractitionerDBO>.removeById(id: String): Boolean {
        val item = this.find { it._id == id }
        return this.remove(item)
    }

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

        val practitioner = getById(practitionerId)

        val session = SessionDBO(geolocation, disciplineName, intentionName)
        if (practitioner != null) {
            // Due to sessions is unmutable list in practitionerDBO
            // we must first extract sessions and add the new to new list
            // drop practitioner from list
            // add again
            val sessions = practitioner
                    .sessions
                    .toMutableList()
                    .plus(session)
            // remove from list
            practitionerList.remove(practitioner)
            // re-add
            practitionerList.add(
                    PractitionerDBO(
                            _id = practitioner._id,
                            created = practitioner.created,
                            sessions = sessions,
                            circles = practitioner.circles,
                            fullName = practitioner.fullName,
                            email = practitioner.email,
                            description = practitioner.description)
            )
        }
        return session
    }


    override fun addCircle(practitionerId: String, circle: CircleDBO): PractitionerDBO? {
        val practitioner = getById(practitionerId)
        if (practitioner != null) {
            val circles = practitioner.circles.toMutableList().plus(circle)
            practitionerList.remove(practitioner)
            practitionerList.add(
                    PractitionerDBO(
                            _id = practitioner._id,
                            created = practitioner.created,
                            sessions = practitioner.sessions,
                            circles = circles,
                            fullName = practitioner.fullName,
                            email = practitioner.email,
                            description = practitioner.description))
        }
        return getById(practitionerId)
    }

    override fun endSession(practitionerId: String, contributionPoints: Long): PractitionerDBO? {

        val practitioner = practitionerList
                .filter {
                    it._id.equals(practitionerId)
                }
                .lastOrNull()
        // Set endTime on last session
        val lastSession = practitioner!!.sessions.last()
        val session = SessionDBO(lastSession.geolocation, lastSession.discipline, lastSession.intention, lastSession.startTime, LocalDateTime.now())
        val sessions = practitioner.sessions.toMutableList()
        sessions.removeAt(0)
        sessions.add(session)
        // Add a log to the SpiritBankLog for this practitioner
        val logs = practitioner.spiritBankLog.toMutableList()
        logs.add(SpiritBankLogItemDBO(type = SpiritBankLogItemType.SESSION, points = contributionPoints))
        // Return the new practitioner with updated sessions
        return PractitionerDBO(practitioner._id, practitioner.created, sessions, spiritBankLog = logs)
    }

    override fun removeAll(): Boolean {
        practitionerList.clear()
        return true
    }

    override fun removeGenerated(): List<PractitionerDBO> {
        practitionerList.removeGenerated()
        return practitionerList
    }

    override fun removeById(practitionerId: String): Boolean {
        practitionerList.removeById(practitionerId)
        return true
    }

    override fun getAll(): List<PractitionerDBO> {
        return practitionerList
    }

    override fun getById(id: String): PractitionerDBO? {
        return practitionerList
                .find {
                    it._id.equals(id)
                }
    }

    /**
     * Clear database
     */
    fun clear() {
        practitionerList.clear()
    }
}