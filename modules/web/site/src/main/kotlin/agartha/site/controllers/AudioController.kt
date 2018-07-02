package agartha.site.controllers

import agartha.site.controllers.utils.ControllerUtil
import agartha.site.objects.response.AudioVars
import spark.Request
import spark.Response
import spark.Spark
import com.opentok.OpenTok



/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-06-29.
 */
class AudioController {

    private val API_KEY = ""
    private val API_SECRET = ""

    private val opentok = OpenTok(Integer.parseInt(API_KEY), API_SECRET)


    companion object {
        val channels = HashMap<String, AudioVars>()
    }

    init {
        Spark.path("/audio") {
            Spark.get("/generate", ::getAudioVars)
            Spark.get("/channels", :: getChannels)
            Spark.get("/channel/:id", ::getChannel)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun getAudioVars(request: Request, response: Response) : String {

        val sessionId = opentok.createSession().sessionId

        val audioVars = AudioVars(
                apiKey = API_KEY,
                sessionId = sessionId,
                token = opentok.generateToken(sessionId))

        channels.put(sessionId, audioVars)

        return ControllerUtil.objectToString(audioVars)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun getChannels(request: Request, response: Response) : String {
        return ControllerUtil.objectListToString(channels.keys.toList())
    }

    @Suppress("UNUSED_PARAMETER")
    private fun getChannel(request: Request, response: Response) : String {
        val sessionId = request.params(":id")
        return ControllerUtil.objectToString(channels.get(sessionId))
    }


}