package agartha.data.services

import agartha.data.db.conn.MongoConnection
import agartha.data.objects.SettingsDBO
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.toList

/**
 * Purpose of this file is reading settings from data storage
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-04-12.
 */
class SettingsService : IBaseService<SettingsDBO> {

    // Get MongoDatabase
    private val database = MongoConnection.getDatabase()
    // MongoCollection
    protected val collection = database.getCollection<SettingsDBO>(CollectionNames.SETTINGS_SERVICE.collectionName)

    /**
     * The settings storage can only have one item
     * @param item Default settings object to insert (if collection is empty)
     * @return Stored settings data object
     */
    override fun insert(item: SettingsDBO): SettingsDBO {
        val items = getAll()
        if (items.isNotEmpty()) {
            return items.first()
        }
        return item.apply {
            collection.insertOne(item)
        }
    }

    override fun getById(id: String): SettingsDBO? {
        return collection.findOneById(id)
    }

    override fun getAll(): List<SettingsDBO> {
        return collection.find().toList()
    }


}