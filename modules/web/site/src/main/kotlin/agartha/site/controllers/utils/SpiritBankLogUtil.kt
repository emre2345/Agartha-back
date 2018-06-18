package agartha.site.controllers.utils

import agartha.data.objects.CircleDBO
import agartha.data.objects.SpiritBankLogItemDBO

/**
 * Purpose of this file is utilities for SpiritBankLog
 *
 * Created by Jorgen Andersson on 2018-06-18.
 */
class SpiritBankLogUtil {

    companion object {

        /**
         * Function for sum up points generated for a circle
         * @param logItems log items for which we points are generated
         * @param circle to determine start and end
         * @return generated number of points
         */
        fun countLogPointsForCircle(logItems: List<SpiritBankLogItemDBO>, circle: CircleDBO): Long {
            return logItems
                    // Filter out those log items created witin the circle active times
                    .filter {
                        it.created.isAfter(circle.startTime) && it.created.isBefore(circle.endTime)
                    }
                    // Map to points
                    .map { it.points }
                    // Sum points
                    .sum()
        }


    }
}