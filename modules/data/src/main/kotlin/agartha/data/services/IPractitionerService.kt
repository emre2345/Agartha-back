package agartha.data.services

import agartha.data.objects.PractitionerDBO
import java.time.LocalDateTime

/**
 * Purpose of this file is inteface for Practitioner datasource service
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
interface IPractitionerService : IBaseService<PractitionerDBO> {

    /**
     * Get all practitioners with session between sessions
     *
     * @param startDate
     * @param endDate
     */
    fun getPractitionersWithSessionBetween(startDate : LocalDateTime, endDate : LocalDateTime): List<PractitionerDBO>


    /**
     * Function to update a document in database collection
     * @param item to be inserted
     * @return inserted document as object
     */
    fun updatePractitionerWithInvolvedInformation(user: PractitionerDBO, fullName: String, email: String, description: String): PractitionerDBO
}