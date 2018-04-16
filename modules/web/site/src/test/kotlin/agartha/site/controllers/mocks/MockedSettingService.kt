package agartha.site.controllers.mocks

import agartha.data.objects.SettingsDBO
import agartha.data.services.IBaseService
import java.util.*

/**
 * Purpose of this class ...
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
class MockedSettingService : IBaseService<SettingsDBO> {
    val settingList: MutableList<SettingsDBO> = mutableListOf()


    override fun insert(item: SettingsDBO): SettingsDBO {
        if (settingList.isEmpty()) {
            val uuid = UUID.randomUUID()
            val createdItem = SettingsDBO(item.intentions, uuid.toString())
            settingList.add(createdItem)
            return createdItem
        }
        return settingList.first()
    }

    override fun getAll(): List<SettingsDBO> {
        return settingList
    }

    override fun getById(id: String?): SettingsDBO? {
        TODO("Never is or will be used")
    }

    fun clear() {
        settingList.clear()
    }

}