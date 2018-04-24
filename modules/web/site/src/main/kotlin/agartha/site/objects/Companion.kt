package agartha.site.objects

import agartha.data.objects.SessionDBO

/**
 * Purpose of this file is holding information about practitioners sessions
 *
 * Created by Jorgen Andersson on 2018-04-24.
 *
 */
class Companion(val count : Int) {

    constructor(sessions : List<SessionDBO>) : this(sessions.count())

}