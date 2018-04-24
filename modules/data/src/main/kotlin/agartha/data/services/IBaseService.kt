package agartha.data.services

/**
 * Purpose of this file is ...
 *
 * Created by Jorgen Andersson on 2018-04-06.
 */
/**
 * Purpose of this file is interface for the BaseService which is needed by all object reading from/writing to database
 *
 * Created by Jorgen Andersson on 2018-03-20.
 */
interface IBaseService<T : Any> {
    /**
     * Function to insert a document into database collection
     * @param object to be inserted
     * @return inserted document as object
     */
    fun insert(item: T): T

    /**
     * Function to get list of all documents as object in collection
     * @return all objects in collection
     */
    fun getAll(): List<T>

    /**
     * Function to get one item by its id
     * @param id database id as string for item to retrieve
     * @return object if id exists or null
     */
    fun getById(id: String): T?
}