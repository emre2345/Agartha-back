package agartha.data.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Purpose of this file is test the ImageDBO
 *
 * Created by Jorgen Andersson on 2018-06-11.
 */
class ImageDBOTest {
    val image = ImageDBO(
            _id = "abcdef",
            fileName = "testImage.jpg",
            image = "This is a fake image".toByteArray())

    @Test
    fun image_id_abcdef() {
        assertThat(image._id).isEqualTo("abcdef")
    }

    @Test
    fun image_fileName_testImageJpg() {
        assertThat(image.fileName).isEqualTo("testImage.jpg")
    }

    @Test
    fun image_image_text() {
        assertThat(String(image.image)).isEqualTo("This is a fake image")
    }
}