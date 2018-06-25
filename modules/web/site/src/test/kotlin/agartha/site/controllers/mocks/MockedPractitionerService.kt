package agartha.site.controllers.mocks

import agartha.common.config.Settings
import agartha.common.utils.DateTimeFormat
import agartha.data.objects.*
import agartha.data.services.IPractitionerService


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
            practitioner: PractitionerDBO,
            session: SessionDBO): SessionDBO {

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
        // If session has a circle then it should
        // add a new item to the practitioners spiritBankLog
        val newSpiritBankLog = practitioner.spiritBankLog.toMutableList()
        if (session.circle !== null) {
            val cost = session.circle!!.minimumSpiritContribution - (session.circle!!.minimumSpiritContribution) * 2
            newSpiritBankLog.add(SpiritBankLogItemDBO(type = SpiritBankLogItemType.JOINED_CIRCLE, points = cost))
        }
        // re-add
        insert(PractitionerDBO(
                _id = practitioner._id,
                created = practitioner.created,
                sessions = sessions,
                circles = practitioner.circles,
                fullName = practitioner.fullName,
                email = practitioner.email,
                description = practitioner.description,
                spiritBankLog = newSpiritBankLog))
        return session
    }


    override fun addCircle(practitionerId: String, circle: CircleDBO): PractitionerDBO? {
        val practitioner = getById(practitionerId)
        if (practitioner != null) {
            val circles = practitioner.circles.toMutableList().plus(circle)
            removeById(practitionerId)
            insert(PractitionerDBO(
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


    override fun editCircle(practitionerId: String, circle: CircleDBO): PractitionerDBO? {
        removeCircleById(practitionerId, circle._id)
        addCircle(practitionerId, circle)
        return getById(practitionerId)
    }

    override fun endSession(practitionerId: String, contributionPoints: Long): PractitionerDBO? {

        val practitioner = practitionerList
                .lastOrNull {
                    it._id.equals(practitionerId)
                }
        // Set endTime on last session
        val lastSession = practitioner!!.sessions.last()
        val session = SessionDBO(lastSession.geolocation, lastSession.discipline, lastSession.intention, lastSession.startTime, DateTimeFormat.localDateTimeUTC(), lastSession.circle)
        val sessions = practitioner.sessions.toMutableList()
        sessions.removeAt(0)
        sessions.add(session)
        // Add a log to the SpiritBankLog for this practitioner

        // If session has a circle then it should
        // add a new item to the practitioners spiritBankLog
        val newSpiritBankLog = practitioner.spiritBankLog.toMutableList()
        var spiritBankLogItemType = SpiritBankLogItemType.ENDED_SESSION
        var addedContributionPoints = contributionPoints
        if (lastSession.circle !== null && practitioner.circles.contains(lastSession.circle!!)) {
            spiritBankLogItemType = SpiritBankLogItemType.ENDED_CREATED_CIRCLE
            val sessionsInCircle = getAll().filter { it.hasSessionInCircleAfterStartTime(lastSession.startTime, session.circle!!) }
            // Number of practitioner that started a session in "my" circle and payed the minimumSpiritContribution
            // should be multiplied by the minimumSpiritContribution
            addedContributionPoints = sessionsInCircle.size * session.circle!!.minimumSpiritContribution
        }
        newSpiritBankLog.add(SpiritBankLogItemDBO(type = spiritBankLogItemType, points = addedContributionPoints))
        // Return the new practitioner with updated sessions
        return PractitionerDBO(practitioner._id, practitioner.created, sessions, spiritBankLog = newSpiritBankLog)
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

    override fun removeCircleById(practitionerId: String, circleId: String): Boolean {
        val practitioner = getById(practitionerId)
        if (practitioner != null) {
            // Create new list of circles with all but the removed one, ie filter out the removed id
            val circles = practitioner.circles.filter { it._id != circleId }
            //
            removeById(practitionerId)
            insert(PractitionerDBO(
                    _id = practitioner._id,
                    created = practitioner.created,
                    sessions = practitioner.sessions,
                    circles = circles,
                    fullName = practitioner.fullName,
                    email = practitioner.email,
                    description = practitioner.description,
                    spiritBankLog = practitioner.spiritBankLog))
            return true
        }
        return false
    }

    override fun payForAddingVirtualSessions(practitioner: PractitionerDBO, numberOfSessions: Int): Boolean {
        val practitionerId: String = practitioner._id ?: ""
        val pointsToPay = Settings.COST_ADD_VIRTUAL_SESSION_POINTS * numberOfSessions
        val spiritBankLog = practitioner.spiritBankLog.toMutableList()
        if (practitioner.calculateSpiritBankPointsFromLog() >= pointsToPay) {
            // Add new logItem to spiritBank log
            spiritBankLog.add(SpiritBankLogItemDBO(
                    type = SpiritBankLogItemType.ADD_VIRTUAL_TO_CIRCLE,
                    points = Settings.returnNegativeNumber(pointsToPay)))
            // Replace old practitioner with updated with spiritBankLog-item
            removeById(practitionerId)
            insert(PractitionerDBO(
                    _id = practitioner._id,
                    created = practitioner.created,
                    sessions = practitioner.sessions,
                    circles = practitioner.circles,
                    fullName = practitioner.fullName,
                    email = practitioner.email,
                    description = practitioner.description,
                    spiritBankLog = spiritBankLog))
            return true
        }
        return false
    }

    /**
     * Clear database
     */
    fun clear() {
        practitionerList.clear()
    }
}