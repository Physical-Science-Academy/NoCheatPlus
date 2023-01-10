/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in thCut even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.catrainbow.nocheatplus.checks.moving.util

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author Catrainbow
 */
class MovingUtil {

    companion object {

        /**
         * Round Double
         */
        @JvmStatic
        fun roundDouble(value: Double, scale: Int): Double {
            return BigDecimal(value).setScale(scale, RoundingMode.HALF_UP).toDouble()
        }

    }

}