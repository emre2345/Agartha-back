package agartha.site.controllers.mocks

import agartha.data.objects.ImageDBO
import agartha.data.services.IBaseService

/**
 * Purpose of this file is Mocked Service for ImageController
 *
 * Created by Jorgen Andersson on 2018-06-12.
 */
class MockedImageService : IBaseService<ImageDBO> {
    val imageList : MutableList<ImageDBO> = mutableListOf()

    override fun insert(item: ImageDBO): ImageDBO {
        imageList.add(item)
        return item
    }

    override fun getAll(): List<ImageDBO> {
        return imageList
    }

    override fun getById(id: String): ImageDBO? {
        return imageList.find { it._id == id }
    }

    fun clear() {
        imageList.clear()
    }

}