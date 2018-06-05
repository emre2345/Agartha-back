package agartha.data.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Created by Jorgen Andersson on 2018-06-05.
 */
class GeolocationDBOTest {

    // Geolocation Kollektiva office Malmö
    val geolocation = GeolocationDBO(55.6044973, 13.005021)

    @Test
    fun geolocation_latitude_55() {
        assertThat(geolocation.latitude).isEqualTo(55.6044973)
    }

    @Test
    fun geolocation_longitude_13() {
        assertThat(geolocation.longitude).isEqualTo(13.005021)
    }

}
