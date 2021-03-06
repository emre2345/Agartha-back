package agartha.data.services

import agartha.data.db.conn.MongoConnection
import agartha.data.objects.ImageDBO
import org.litote.kmongo.*

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
        // Try to read the image from database
        val existing = getById(item._id)

        return item.apply {
            // If it does not exists, insert
            if (existing == null) {
                collection.insertOne(item)
            }
            // Or if already exists, replace
            else {
                collection.replaceOne(item)
            }
        }
    }

    /**
     * Returns an empty list
     * This function call can be quite big to return via http and therefore should not be used
     */
    override fun getAll(): List<ImageDBO> {
       return listOf()
    }

    /**
     * Get an image by its id
     */
    override fun getById(id: String): ImageDBO? {
        return collection.findOneById(id)
    }

}