package agartha.site.controllers.utils

import io.schinzel.basicutils.configvar.ConfigVar

/**
 * Purpose of this class ...
 *
 * Created by Jorgen Andersson (jorgen@kollektiva.se) on 2018-06-13.
 */
enum class PassPhrase constructor(val passPhrase: String) {
    /**
     * The runtime passphrase.
     */
    RUNTIME(ConfigVar.create(".env").getValue("A_PASS_PHRASE")),
    /**
     * The test passphrase.
     */
    TEST("Santa");
}
