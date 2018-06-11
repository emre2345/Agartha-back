package agartha.data.services

import agartha.data.db.conn.MongoConnection
import agartha.data.objects.ImageDBO
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.toList
import org.litote.kmongo.updateOne

/**
 * Purpose of this Service is reading/writing Images to database
 *
 * Created by Jorgen Andersson on 2018-06-11.
 */
class ImageService  : IBaseService<ImageDBO> {
    // Get MongoDatabase
    private val database = MongoConnection.getDatabase()
    // MongoCollection
    protected val collection = database.getCollection<ImageDBO>(CollectionNames.IMAGE_SERVICE.collectionName)

    /**
     * Insert / Update image
     */
    override fun insert(item: ImageDBO): ImageDBO {
        val existing = getById(item._id)

        return item.apply {
            if (existing == null) {
                collection.insertOne(item)
            }
            else {
                collection.updateOne(item)
            }
        }
    }

    /**
     * This function call can be quite big to return via http
     * Use with care
     */
    override fun getAll(): List<ImageDBO> {
        return collection.find().toList()
    }

    /**
     * Get an image by its id
     */
    override fun getById(id: String): ImageDBO? {
        return collection.findOneById(id)
    }

}