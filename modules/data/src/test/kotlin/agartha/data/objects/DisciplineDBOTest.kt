package agartha.data.objects

import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * Purpose of this class is to test the DisciplineDBO
 */
class DisciplineDBOTest {
    val discipline = DisciplineDBO("Yoga", "This is Yoga description")

    @Test
    fun discipline_title_yoga() {
        Assertions.assertThat(discipline.title).isEqualTo("Yoga")
    }

    @Test
    fun discipline_description_ThisIsYogaDescription() {
        Assertions.assertThat(discipline.description).isEqualTo("This is Yoga description")
    }

}