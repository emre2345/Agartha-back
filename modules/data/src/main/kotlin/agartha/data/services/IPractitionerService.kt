package agartha.data.services

import agartha.data.objects.PractitionerDBO
import agartha.data.objects.SessionDBO
import java.time.LocalDateTime

/**
 * Purpose of this file is inteface for Practitioner datasource service
 *
 * Created by Jorgen Andersson on 2018-04-09.
 */
interface IPractitionerService : IBaseService<PractitionerDBO> {

    /**
     * Get all practitioners with session after a specific datetime
     *
     * @param startDate
     */
    fun getPractitionersWithSessionAfter(startDate : LocalDateTime): List<PractitionerDBO>

    /**
     * Function to update a document in database collection
     * @param item to be inserted
     * @return inserted document as object
     */
    fun updatePractitionerWithInvolvedInformation(user: PractitionerDBO, fullName: String, email: String, description: String): PractitionerDBO
}