package agartha.data.db.conn

import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

/**
 * Test the Mongo Connection
 */
class MongoConnectionTest {

    @Before
    fun setupBeforeEachMethod() {
        MongoConnection.resetDatabase()
    }

    @Test
    fun mongoConnection_getDatabaseTEST_databaseNameShouldMatchRandomGenerator() {
        MongoConnection.setConnection(Database.TEST)
        Assertions.assertThat(MongoConnection.getDatabase().name.length).isEqualTo(9)
    }

    @Test
    fun mongoConnection_getDatabaseRUNTIME_databaseNameShouldBeSameAsExpected() {
        MongoConnection.setConnection(Database.RUNTIME)
        Assertions.assertThat(MongoConnection.getDatabase().name).isEqualTo("agartha")
    }

    @Test
    fun mongoConnection_testTwoConnections_thereCanOnlyBeOneConnectionShouldThrowException() {
        val expectedExceptionMessage = "There is already a connection to a DB!"
        try {
            // Set first connection
            MongoConnection.setConnection(Database.TEST)
            // Try to set another connection
            MongoConnection.setConnection(Database.RUNTIME)
        } catch (e: RuntimeException) {
            Assertions.assertThat(e.message).isEqualTo(expectedExceptionMessage)
        }
    }

}

