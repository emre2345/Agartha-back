package agartha.data.objects

/**
 * Purpose of this file is representing data object for a practice
 *
 * Created by Jorgen Andersson on 2018-04-16.
 */
data class PracticeDBO(
        // Title of practice
        val title: String,
        // Sub-practices
        val practices : List<PracticeDBO> = emptyList())