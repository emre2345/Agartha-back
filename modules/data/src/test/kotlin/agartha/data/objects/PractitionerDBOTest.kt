package agartha.data.objects

import org.assertj.core.api.Assertions
import org.junit.Test

class PractitionerDBOTest {
    val expectedFullName = "Rebecca Fransson"
    val expectedEmail = "rebecca@kollektiva.se"
    val expectedDescription = "Jag gillar yoga!"

    /**
     * addInvolvedInformation
     */
    @Test
    fun practitionerInvolvedInformation_fullName_Rebecca() {
        val practitioner = PractitionerDBO()
        practitioner.addInvolvedInformation(expectedFullName, "", "")
        Assertions.assertThat(practitioner.fullName).isEqualTo(expectedFullName)
    }
    /**
     * addInvolvedInformation
     */
    @Test
    fun practitionerInvolvedInformation_email_RebeccaAtKollektivaSe() {
        val practitioner = PractitionerDBO()
        practitioner.addInvolvedInformation("", expectedEmail, "")
        Assertions.assertThat(practitioner.email).isEqualTo(expectedEmail)
    }
    /**
     * addInvolvedInformation
     */
    @Test
    fun practitionerInvolvedInformation_description_JagGillarYoga() {
        val practitioner = PractitionerDBO()
        practitioner.addInvolvedInformation("", "", expectedDescription)
        Assertions.assertThat(practitioner.description).isEqualTo(expectedDescription)
    }

}