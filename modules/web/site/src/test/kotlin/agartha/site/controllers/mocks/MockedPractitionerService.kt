package agartha.site.controllers.mocks

import agartha.common.config.Settings
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
            practitioner: PractitionerDBO,
            fullName: String,
            email: String,
            description: String): PractitionerDBO {
        val index = practitionerList.indexOf(practitioner)
        practitioner.addInvolvedInformation(fullName, email, description)
        practitionerList[index] = practitioner
        return practitioner
    }

    override fun startSession(
            practitioner: PractitionerDBO,
            session: SessionDBO): SessionDBO {
        return session
    }

    override fun endSession(practitionerId: String, contributionPoints: Long) {

    }


    override fun addRegisteredCircle(practitionerId: String, circleId: String): PractitionerDBO? {
        val practitioner = getById(practitionerId)
        if (practitioner != null) {
            val circles = practitioner.registeredCircles.toMutableList().plus(circleId)
            return PractitionerDBO(
                    _id = practitioner._id,
                    created = practitioner.created,
                    sessions = practitioner.sessions,
                    circles = practitioner.circles,
                    fullName = practitioner.fullName,
                    email = practitioner.email,
                    description = practitioner.description,
                    registeredCircles = circles)
        }
        return practitioner
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


    override fun endCircle(practitionerId: String, creator: Boolean, circle: CircleDBO?, contributionPoints: Long) {

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

    override fun payForAddingVirtualSessions(practitioner: PractitionerDBO, virtualRegistered: Long): Boolean {
        val practitionerId: String = practitioner._id ?: ""
        val pointsToPay = Settings.COST_ADD_VIRTUAL_SESSION_POINTS * virtualRegistered
        val spiritBankLog = practitioner.spiritBankLog.toMutableList()
        if (checkPractitionerCanAffordVirtualRegistered(practitioner, virtualRegistered)) {
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

    override fun checkPractitionerCanAffordVirtualRegistered(practitioner: PractitionerDBO, virtualRegistered: Long): Boolean {
        val pointsToPay = Settings.COST_ADD_VIRTUAL_SESSION_POINTS * virtualRegistered
        return practitioner.calculateSpiritBankPointsFromLog() >= pointsToPay
    }

    override fun giveFeedback(circle: CircleDBO, feedbackPoints: Long): Boolean {
        // Go through all the practitioners
        val creator = getAll().firstOrNull {
            // Is practitioner creator of this circle?
            it.creatorOfCircle(circle) }
        // Return true if circle exist/has creator
        return creator != null
    }

    /**
     * Clear database
     */
    fun clear() {
        practitionerList.clear()
    }
}