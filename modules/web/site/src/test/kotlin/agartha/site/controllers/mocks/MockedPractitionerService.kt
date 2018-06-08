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
        val first = practitionerList
                .filter {
                    it._id.equals(practitionerId)
                }
                .first()

        val session = SessionDBO(geolocation, disciplineName, intentionName)
        // Due to sessions is unmutable list in practitionerDBO
        // we must first extract sessions and add the new to new list
        // drop practitioner from list
        // add again
        val sessions = first
                .sessions
                .toMutableList()
                .plus(session)
        // remove from list
        practitionerList.remove(first)
        // re-add
        practitionerList.add(
                PractitionerDBO(
                        _id = first._id,
                        created = first.created,
                        sessions = sessions,
                        fullName = first.fullName,
                        email = first.email,
                        description = first.description)
        )
        return session
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
        // Return the new practitioner with updated sessions
        return PractitionerDBO(practitioner._id, practitioner.created, sessions)
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
        return practitionerList.find {
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