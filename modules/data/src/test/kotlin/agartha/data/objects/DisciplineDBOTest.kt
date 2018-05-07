package agartha.data.objects

import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * Purpose of this class is to test the DisciplineDBO
 */
class DisciplineDBOTest {
    val discipline = DisciplineDBO("Yoga")

    /*********************************
     * Variables - title + practices *
     *********************************/
    @Test
    fun discipline_title_yoga() {
        Assertions.assertThat(discipline.title).isEqualTo("Yoga")
    }

}