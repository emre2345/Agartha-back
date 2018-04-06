package agartha.data.services

import agartha.data.db.conn.Database
import agartha.data.db.conn.MongoConnection

/**
 * Purpose of this file is being a helper for tests to use database connection
 * Database with name matching Database.RUNTIME will not be droped
 *
 * Created by Jorgen Andersson on 2018-03-20.
 */
class MongoConnectionHelper {
    // Setup default database
    private var database = MongoConnection.getDatabase()

    /**
     * Drop a collection by name
     * Drops collection if database is test (ie not runtime)
     * @param name of collection in current database connection requester wants to drop
     */
    fun dropCollection(collectionName: String) {
        // if we are allowed to drop this collection
        if (isDropable(database.name)) {
            database.getCollection(collectionName).drop()
        }
    }

    /**
     * Drop entire database
     * Drops database if test (ie not runtime)
     */
    fun dropDatabase() {
        // if we are allowed to drop this database
        if (isDropable(database.name)) {
            database.drop()
        }
    }

    /**
     * Function to see it argument database name is runtime (production or runtime Dev)
     * If connection string ends with /ghostpanda enviroment is runtime, else test
     * @param name of current database
     * @return true if current database is runtime
     */
    private fun isRuntime(datbaseName: String): Boolean {
        return Database.RUNTIME.hostUrl.endsWith(datbaseName)
    }

    /**
     * Function to see if we are allowed to perform drop tasks on this database
     * @param name of current database
     * @return true if current database is test and therefore drop-able
     */
    private fun isDropable(databaseName : String) : Boolean {
        return !isRuntime(databaseName)
    }
}