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
package net.catrainbow.nocheatplus.actions.temples

import kotlin.math.max
import kotlin.math.min

/**
 * Range Temple
 *
 * @author Catrainbow
 */
class ActionRangeTemple(private var min: Double, private var max: Double) {

    fun getAverage(valueA: Int, valueB: Int): Double {
        return (min * valueA + max * valueB) / 2.0
    }

    fun getDefaultTemple(min: Double, max: Double): ActionRangeTemple {
        this.min = min(min, max)
        this.max = max(min, max)
        return this
    }

}