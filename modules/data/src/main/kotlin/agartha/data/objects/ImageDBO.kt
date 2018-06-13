package agartha.data.objects

/**
 * Purpose of this file is handling image uploaded to database
 *
 * Created by Jorgen Andersson on 2018-06-11.
 */
data class ImageDBO(
        // Database Id
        val _id: String,
        // Original file name
        val fileName: String,
        // The Image
        val image: ByteArray)