package agartha.data.services

import agartha.data.objects.MonitorDBO
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

/**
 * Purpose of this file is to test the MonitorService
 *
 * Created by Jorgen Andersson on 2018-04-06.
 */
class MonitorServiceTest : DatabaseHandler() {

    @Before
    fun setupBeforeFunctions() {
        dropCollection(CollectionNames.MONITOR_SERVICE)
    }

    @Test
    fun todoService_insertItem_idIsReturned() {
        val item = MonitorService().insert(MonitorDBO("InsertedItem"))
        Assertions.assertThat(item._id).isNotNull()
    }

    @Test
    fun todoService_getAll_size2() {
        MonitorService().insert(MonitorDBO("first thing todo"))
        MonitorService().insert(MonitorDBO("second thing todo"))
        val todoList = MonitorService().getAll()
        Assertions.assertThat(todoList.size).isEqualTo(2)
    }

    @Test
    fun settingService_getOneExisting_notNull() {
        val item = MonitorService().insert(MonitorDBO("InsertedItem"))
        val getObject = MonitorService().getById(item._id ?: "")
        Assertions.assertThat(getObject).isNotNull()
    }

    @Test
    fun settingService_getOneNonExisting_null() {
        val getObject = MonitorService().getById("ThisIdDoesNotExistInDB")
        Assertions.assertThat(getObject).isNull()
    }
}