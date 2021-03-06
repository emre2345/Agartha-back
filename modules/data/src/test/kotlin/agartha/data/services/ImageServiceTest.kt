package agartha.data.services

import agartha.data.objects.ImageDBO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Purpose of this file is to test the ImageService
 *
 * Created by Jorgen Andersson on 2018-06-11.
 */
class ImageServiceTest : DatabaseHandler() {

    @Before
    fun setupBeforeFunctions() {
        dropCollection(CollectionNames.IMAGE_SERVICE)
    }

    @Test
    fun insertImage_responseObject_isSame() {
        val image = ImageService().insert(ImageDBO(_id = "abc123", fileName = "abc.jpg", image = "abc".toByteArray()))
        assertThat(image._id).isEqualTo("abc123")
    }

    @Test
    fun insertImage_dbObject_isSame() {
        val item = ImageService().insert(ImageDBO(_id = "abc123", fileName = "abc.jpg", image = "abc".toByteArray()))
        val db = ImageService().getById(item._id)
        assertThat(item.fileName).isEqualTo(db?.fileName ?: "")
    }

    @Test
    fun updateImage_responseObjectFileNameUpdated_defJpg() {
        ImageService().insert(ImageDBO(_id = "abc123", fileName = "abc.jpg", image = "abc".toByteArray()))
        val update = ImageService().insert(ImageDBO(_id = "abc123", fileName = "def.jpg", image = "def".toByteArray()))
        assertThat(update.fileName).isEqualTo("def.jpg")
    }

    @Test
    fun updateImage_dbObjectFileNameUpdated_defJpg() {
        ImageService().insert(ImageDBO(_id = "abc123", fileName = "abc.jpg", image = "abc".toByteArray()))
        ImageService().insert(ImageDBO(_id = "abc123", fileName = "def.jpg", image = "def".toByteArray()))
        val db = ImageService().getById("abc123")
        assertThat(db!!.fileName).isEqualTo("def.jpg")
    }

    @Test
    fun getById_existing_abcJpg() {
        ImageService().insert(ImageDBO(_id = "abc123", fileName = "abc.jpg", image = "abc".toByteArray()))
        val db = ImageService().getById("abc123")
        assertThat(db!!.fileName).isEqualTo("abc.jpg")
    }

    @Test
    fun getById_nonExisting_null() {
        val image = ImageService().getById("abc")
        assertThat(image).isNull()
    }

    /**
     * Get all should always return empty list
     * Needed to exist since we want to implement all functions in Interface and not to throw exception
     */
    @Test
    fun getAll_response_empty() {
        ImageService().insert(ImageDBO(_id = "abc123", fileName = "abc.jpg", image = "abc".toByteArray()))
        val list = ImageService().getAll()
        assertThat(list.size).isEqualTo(0)
    }
}