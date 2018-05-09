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
    MALMO_KOLLEKTIVA(GeolocationDBO(55.6044973,13.005021)),
    MALMO_TRIANGELN(GeolocationDBO(55.5897248,12.992067)),
    MALMO_ARENA(GeolocationDBO(55.5601004,12.9719622)),
    MALMO_EMPORIA(GeolocationDBO(55.5601004,12.9719622)),
    //
    HAVANG(GeolocationDBO(55.7274969,14.1864365)),
    VITEMOLLA(GeolocationDBO(55.6974881,14.2045415)),
    KIVIK(GeolocationDBO(55.6862754,14.2261018)),
    BRANTEVIK(GeolocationDBO(55.5140518,14.3324331)),
    BACKAKRA(GeolocationDBO(55.3782831,14.1580726)),
    KASEBERGA(GeolocationDBO(55.3821812,14.052331)),
    YSTAD_SALTSJOBAD(GeolocationDBO(55.4366198,13.8059124)),
    ABBEKAS(GeolocationDBO(55.3948922,13.6025174)),
    SMYGEHUS(GeolocationDBO(55.364482,13.4339826)),
    BJORNSTORP(GeolocationDBO(55.656372399999995, 13.369866799999999)),
    //
    COPENHAGEN_AQUARIUM(GeolocationDBO(55.6294952,12.6225966)),
    COPENHAGEN_MERMAID(GeolocationDBO(55.6991116,12.544834)),
    //
    STOCKHOLM_WASA(GeolocationDBO(59.3215749,18.0512788)),
    STOCKHOLM_SKANSEN(GeolocationDBO(59.3225629,18.074956)),
    //
    PRAGUE_ASTRO_CLOCK(GeolocationDBO(50.079119,14.4049228)),
    PRAGUE_CHARLES_BRIDGE(GeolocationDBO(50.0863849,14.41355)),
    //
    SYDNEY_OPERA_HOUSE(GeolocationDBO(-33.8632658,151.2285838)),
    SYDNEY_HARBOUR_BRIDGE(GeolocationDBO(-33.870707,151.2383162)),
    //
    NEW_YORK_ESB(GeolocationDBO(40.7493302,-73.9898485)),
    NEW_YORK_BATTERY_PARK(GeolocationDBO(40.7077487,-74.0109985)),
    NEW_YORK_STATUE(GeolocationDBO(40.6894215,-74.0412443)),
    NEW_YORK_BROOKLYN_BRIDGE(GeolocationDBO(40.7119377,-74.0003165)),
    NEW_YORK_MSG(GeolocationDBO(40.7508019,-73.9951283)),
    NEW_YORK_GRAND_CENTRAL(GeolocationDBO(40.7508019,-74.0023005)),
    NEW_YORK_UN_HQ(GeolocationDBO(40.7561215,-73.9902433))
}