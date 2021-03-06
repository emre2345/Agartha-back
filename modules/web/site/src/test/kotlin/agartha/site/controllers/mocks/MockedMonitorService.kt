package agartha.site.controllers.mocks

import agartha.data.objects.MonitorDBO
import agartha.data.services.IBaseService
import java.util.*


/**
 * Purpose of this file is Mocked service for testing Monitor Controllers
 */
class MockedMonitorService : IBaseService<MonitorDBO> {
    val monitorList: MutableList<MonitorDBO> = mutableListOf()

    override fun insert(item: MonitorDBO): MonitorDBO {
        // data classes are immutable, therefore create new with generated id
        val uuid = UUID.randomUUID()
        val createdItem = MonitorDBO(item.value, item.created, uuid.toString())
        // Add it to list
        monitorList.add(createdItem)
        // return it
        return createdItem
    }

    override fun getAll(): List<MonitorDBO> {
        return monitorList
    }

    override fun getById(id: String): MonitorDBO? {
        return monitorList.find { it._id == id }
    }

    /**
     * Clear database
     */
    fun clear() {
        monitorList.clear()
    }
}