package agartha.data.services

import agartha.data.db.conn.MongoConnection
import org.litote.kmongo.*

/**
 * Purpose of this file is to hold Database connections and base function that will be needed
 * for all Mongo collections
 * IBaseService interface holding more extensive documentation about params and returns
 *
 * Created by Jorgen Andersson on 2018-03-19.
 */
open class MongoBaseService<T : Any>(collectionName: CollectionNames) : IBaseService<T> {

    // Get MongoDatabase
    private val database = MongoConnection.getDatabase()
    // MongoCollection
    protected val collection = database.getCollection<Any>(collectionName.collectionName)

    /**
     * Insert item into database
     */
    override fun insert(item: T): T {
        return item.apply {
            collection.insertOne(item)
        }
    }

    /**
     * Insert/Replace item in database depending on items database id exists or is null
     */
    override fun upsert(item: T): T {
        return item.apply {
            collection.save(item)
        }
    }

    /**
     * Delete from database by Id as string
     */
    override fun delete(id: String?): Boolean {
        if (id != null) {
            val deleteOneById = collection.deleteOneById(id)
            return deleteOneById.deletedCount.equals(1L)
        }
        return false
    }

    /**
     * Get all items from database
     */
    override fun getAll(): List<T> {
        return collection.find().toList() as List<T>
    }

    /**
     * Get single item from database
     */
    override fun getById(id: String?): T? {
        if (id != null) {
            return collection.findOneById(id) as? T
        }
        return null
    }

}