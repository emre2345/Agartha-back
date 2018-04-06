package agartha.data.db.conn

import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo

/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-04-06.
 */
/**
 * Purpose of this file is to handle the connection to the Mongo database
 * Written by Rebecca Fransson on 2018-03-13
 */
object MongoConnection {

    private var DATABASE : MongoDatabase? = null

    /**
     * Sets the connection to the mongo db
     * @param activeConnection enum item with database connection details
     * @return
     */
    fun setConnection(activeDatabase : Database) : MongoConnection {
        val clientURI = MongoClientURI(activeDatabase.hostUrl)
        val mongoClient = KMongo.createClient(clientURI)
        DATABASE = mongoClient.getDatabase(clientURI.database)
        return this
    }

    /**
     * Get MongoDatabase
     * @return MongoDatabase for current environmnet
     */
    fun getDatabase() : MongoDatabase {
        @Suppress("UNCHECKED_CAST")
        return DATABASE as MongoDatabase
    }


    /**
     * Function to reset the database
     * For testing purposes only
     */
    fun resetDatabase() {
        DATABASE = null
    }

}