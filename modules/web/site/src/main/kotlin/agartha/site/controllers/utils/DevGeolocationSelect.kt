package agartha.site.controllers.utils

import agartha.data.objects.GeolocationDBO

/**
 * Purpose of this file is to have some predefined GeoLocations for generating
 * Dev and Demo people
 * Easiest way of finding geolocations is to use maps.google.com, click on the map and copy
 * positions after the @ sign in address bar
 *
 * Created by Jorgen Andersson on 2018-05-04.
 */
enum class DevGeolocationSelect(val geolocationDBO: GeolocationDBO) {
    // location for Kollektiva Office in Malmö (Baltzarsgatan)
    MALMO_KOLLEKTIVA(GeolocationDBO(55.6044973,13.005021)),
    // location for Triangeln Malmö
    MALMO_TRIANGELN(GeolocationDBO(55.5897248,12.992067)),
    // location for Jorgen Home Office
    BJORNSTORP(GeolocationDBO(55.656372399999995, 13.369866799999999)),
    // location for Sydney Opera House, Australia
    SYDNEY_OPERA_HOUSE(GeolocationDBO(-33.8632658,151.2285838)),
    // location for Empire State Building, New York City, USA
    NEW_YORK_ESB(GeolocationDBO(40.7493302,-73.9898485))
}