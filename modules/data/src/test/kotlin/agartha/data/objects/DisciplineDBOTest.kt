package agartha.data.objects

import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * Purpose of this class is to test the DisciplineDBO
 */
class DisciplineDBOTest {
    val discipline = DisciplineDBO("Yoga", listOf(PracticeDBO("Tantra"), PracticeDBO("Hatha")))

    /*********************************
     * Variables - title + practices *
     *********************************/
    @Test
    fun discipline_title_yoga() {
        Assertions.assertThat(discipline.title).isEqualTo("Yoga")
    }

    /**
     *
     */
    @Test
    fun discipline_practicesSize_2() {
        Assertions.assertThat(discipline.practices.size).isEqualTo(2)
    }

    /**
     *
     */
    @Test
    fun discipline_firstPracticeTitle_Tantra() {
        Assertions.assertThat(discipline.practices[0].title).isEqualTo("Tantra")
    }
    /**
     *
     */
    @Test
    fun discipline_secondPracticeTitle_Hatha() {
        Assertions.assertThat(discipline.practices[1].title).isEqualTo("Hatha")
    }

}