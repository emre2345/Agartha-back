package agartha.site.controllers

import agartha.data.objects.ImageDBO
import agartha.data.services.IBaseService
import agartha.site.controllers.utils.ReqArgument
import spark.Request
import spark.Response
import spark.Spark
import spark.kotlin.halt
import java.io.File
import javax.servlet.MultipartConfigElement


/**
 * Purpose of this file is to upload an image
 * Form example:
 * <form id="uploadForm" method="post" action="" enctype="multipart/form-data">
 *     <input type="file" name="file" />
 *     <input type="submit" class="button" value="Upload" />
 * </form>
 *
 * Created by Jorgen Andersson on 2018-06-11.
 */
class ImageController(private val mService: IBaseService<ImageDBO>) {

    init {
        Spark.path("/image") {
            Spark.before("/${ReqArgument.IMAGE_ID.value}", ::validateStuff)
            // Read the image from database
            Spark.get("/${ReqArgument.IMAGE_ID.value}", ::getImage)
            // Write image to database
            Spark.post("/${ReqArgument.IMAGE_ID.value}", "multipart/form-data", ::setImage)
        }
    }

    private fun validateStuff(request: Request, response: Response) {

        if (request.requestMethod() == "POST") {
            // getPart("file") where file matches the name in HTML <input type="file" name="" />
            val uploadedFile = request.raw().getPart("file")
            if (uploadedFile == null) {
                halt(400, "Image missing or not of type jpg, jpeg or png")
            } else {
                if (uploadedFile.submittedFileName.endsWith(".jpg") ||
                        uploadedFile.submittedFileName.endsWith(".jpeg") ||
                        uploadedFile.submittedFileName.endsWith(".png")) {
                } else {
                    halt(400, "Image missing or not of type jpg, jpeg or png")
                }
            }
        }

        if (request.requestMethod() == "GET") {
            // Get image ID from API path
            val imageId: String = request.params(ReqArgument.IMAGE_ID.value)
            // Get image from database
            val image = mService.getById(imageId)
            if (image == null) {
                halt(404)
            }
        }
    }

    /**
     * Read an image from database
     */
    private fun getImage(request: Request, response: Response): String {
        // Get image ID from API path
        val imageId: String = request.params(ReqArgument.IMAGE_ID.value)
        // Get image from database
        val image = mService.getById(imageId)
        // If exists from database
        if (image != null) {
            val raw = response.raw()
            response.header("Content-Disposition", "attachment; filename=${image.fileName}");
            response.type("application/force-download");
            raw.getOutputStream().write(image.image);
            raw.getOutputStream().flush();
            raw.getOutputStream().close();
        }
        return ""
    }

    /**
     * Upload image
     * Code borrowed with style from following page:
     * https://stackoverflow.com/questions/34746900/sparkjava-upload-file-didt-work-in-spark-java-framework
     */
    private fun setImage(request: Request, response: Response): String {
        // Get image ID from API path
        val imageId: String = request.params(ReqArgument.IMAGE_ID.value)
        //
        val location = "image"                  // the directory location where files will be stored
        val maxFileSize: Long = 1_000_000       // the maximum size allowed for uploaded files
        val maxRequestSize: Long = 1_000_000    // the maximum size allowed for multipart/form-data requests
        val fileSizeThreshold = 1024            // the size threshold after which files will be written to disk
        //
        val multipartConfigElement = MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold)
        request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement)

        // getPart("file") where file matches the name in HTML <input type="file" name="" />
        val uploadedFile = request.raw().getPart("file")

        if (uploadedFile != null) {
            // Insert/Update database
            mService.insert(
                    ImageDBO(
                            _id = imageId,
                            fileName = uploadedFile.submittedFileName,
                            image = uploadedFile.inputStream.readBytes()))

            // Return path to image
            return request.pathInfo()
        }

        return ""

    }
}