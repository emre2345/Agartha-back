package agartha.data.db.conn

import io.schinzel.basicutils.RandomUtil
import io.schinzel.basicutils.configvar.ConfigVar

/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-04-06.
 */
enum class Database constructor(val hostUrl: String) {
    /**
     * The runtime database.
     */
    RUNTIME(ConfigVar.create(".env").getValue("MONGOHQ_URL")),
    /**
     * The database used for tests.
     * Randomize database name with 9 characters so it can never be same as runtime name ghostpanda (10 chars)
     */
    TEST("mongodb://localhost:27017/${RandomUtil.getRandomString(9)}");
}
