package agartha.data.services

import agartha.data.db.conn.Database
import agartha.data.db.conn.MongoConnection
import org.junit.AfterClass
import org.junit.BeforeClass

/**
 * Purpose of this file is helping to setup and teardown databases for tests
 *
 * Created by Jorgen Andersson on 2018-03-20.
 */
abstract class DatabaseHandler {

    /**
     * Static methods
     */
    companion object {
        private var mMongoConnectionHelper : MongoConnectionHelper? = null

        /**
         * Create Database
         */
        @BeforeClass
        @JvmStatic fun setupBeforeClass() {
            MongoConnection.setConnection(Database.TEST)
            mMongoConnectionHelper = MongoConnectionHelper()
        }

        /**
         * Drop Database
         */
        @AfterClass
        @JvmStatic fun teardownAfterClass() {
            mMongoConnectionHelper!!.dropDatabase()
        }
    }

    /**
     * Function to drop a collection by name
     */
    protected fun dropCollection(collectionName : CollectionNames) {
        mMongoConnectionHelper!!.dropCollection(collectionName.collectionName)
    }

}