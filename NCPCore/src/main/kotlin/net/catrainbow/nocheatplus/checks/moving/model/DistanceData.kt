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
package net.catrainbow.nocheatplus.checks.moving.model

import cn.nukkit.level.Position
import net.catrainbow.nocheatplus.components.data.ICheckData
import kotlin.math.abs

/**
 * 处理移动距离
 */
class DistanceData(val from: Position, val to: Position) : ICheckData {

    val toY = to.getY()
    val fromY = from.getY()
    val xDiff = to.x - from.x
    val yDiff = to.x - from.x
    val zDiff = to.z - from.z

}