package agartha.data.services

import agartha.data.db.conn.MongoConnection
import agartha.data.objects.MonitorDBO
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.toList

/**
 * Purpose of this file is Service for reading/writing MonitorItems to database
 *
 * Created by Jorgen Andersson on 2018-04-06.
 */
class MonitorService : IBaseService<MonitorDBO> {
    // Get MongoDatabase
    private val database = MongoConnection.getDatabase()
    // MongoCollection
    protected val collection = database.getCollection<MonitorDBO>(CollectionNames.MONITOR_SERVICE.collectionName)


    override fun insert(item: MonitorDBO): MonitorDBO {
        return item.apply {
            collection.insertOne(item)
        }
    }

    override fun getById(id: String): MonitorDBO? {
        return collection.findOneById(id)
    }

    override fun getAll(): List<MonitorDBO> {
        return collection.find().toList()
    }


}