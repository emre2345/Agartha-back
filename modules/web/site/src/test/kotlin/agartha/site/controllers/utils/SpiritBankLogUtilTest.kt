package agartha.site.controllers.utils

import agartha.data.objects.CircleDBO
import agartha.data.objects.SpiritBankLogItemDBO
import agartha.data.objects.SpiritBankLogItemType
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime

/**
 * Created by Jorgen Andersson on 2018-06-18.
 */
class SpiritBankLogUtilTest {

    private val circle = CircleDBO(
            _id = "c", name = "", description = "", intentions = listOf(), disciplines = listOf(),
            startTime = LocalDateTime.now().minusHours(2),
            endTime = LocalDateTime.now().minusHours(1),
            minimumSpiritContribution = 2)

    @Test
    fun countLogPoints_emptyList_0() {
        val points = SpiritBankLogUtil.countLogPointsForCircle(listOf(), circle)
        assertThat(points).isEqualTo(0L)
    }

    @Test
    fun countLogPoints_noMatch_0() {
        val points = SpiritBankLogUtil.countLogPointsForCircle(listOf(
                SpiritBankLogItemDBO(
                        created = LocalDateTime.now().minusHours(3),
                        type = SpiritBankLogItemType.START,
                        points = 12)
        ), circle)
        assertThat(points).isEqualTo(0L)
    }

    @Test
    fun countLogPoints_twoMatch_24() {
        val points = SpiritBankLogUtil.countLogPointsForCircle(listOf(
                // Before, should be omitted
                SpiritBankLogItemDBO(
                        created = LocalDateTime.now().minusMinutes(125),
                        type = SpiritBankLogItemType.START,
                        points = 50),
                SpiritBankLogItemDBO(
                        created = LocalDateTime.now().minusMinutes(100),
                        type = SpiritBankLogItemType.START,
                        points = 20),
                SpiritBankLogItemDBO(
                        created = LocalDateTime.now().minusMinutes(65),
                        type = SpiritBankLogItemType.START,
                        points = 4),
                // After, should be omitted
                SpiritBankLogItemDBO(
                        created = LocalDateTime.now().minusMinutes(15),
                        type = SpiritBankLogItemType.START,
                        points = 32)
                ), circle)
        assertThat(points).isEqualTo(24L)
    }
}