package agartha.site.controllers

import com.despegar.sparkjava.test.SparkServer
import spark.servlet.SparkApplication

/**
 * Purpose of this file is setup a fake Spark server to which we can attach a Controller
 *
 * https://github.com/despegar/spark-test
 * Created by Jorgen Andersson on 2018-03-22.
 */
class ControllerServer {

    /**
     * As per readme file in above git repo, Mock the Spark Application for Server
     */
    class MockedSparkApplication : SparkApplication {
        override fun init() {
            println("Test application initialized")
        }

        override fun destroy() {
            println("Test application stopped")
        }
    }

    // testServer to access API from
    // Example: val getRequest = testServer.get("/todos/", false)
    val testServer: SparkServer<MockedSparkApplication> = SparkServer<MockedSparkApplication>(MockedSparkApplication::class.java, 4567)
}