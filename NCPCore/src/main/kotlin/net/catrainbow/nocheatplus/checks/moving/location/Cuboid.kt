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
package net.catrainbow.nocheatplus.checks.moving.location

import cn.nukkit.block.Block
import cn.nukkit.level.Level
import cn.nukkit.level.Location
import cn.nukkit.math.Vector3

/**
 * AABB
 * @author Catrainbow
 */
class Cuboid {

    var x1: Double
    var x2: Double
    var y1: Double
    var y2: Double
    var z1: Double
    var z2: Double

    constructor(location: Location) {
        this.x1 = location.x
        this.x2 = location.x
        this.y1 = location.y
        this.y2 = location.y
        this.z1 = location.z
        this.z2 = location.z
    }

    constructor(block: Block) {
        this.x1 = block.minX
        this.x2 = block.maxX
        this.y1 = block.minY
        this.y2 = block.maxY
        this.z1 = block.minZ
        this.z2 = block.maxZ
    }

    constructor(x1: Double, x2: Double, y1: Double, y2: Double, z1: Double, z2: Double) {
        this.x1 = x1
        this.x2 = x2
        this.y1 = y1
        this.y2 = y2
        this.z1 = z1
        this.z2 = z2
    }

    fun expand(n: Double, n2: Double, n3: Double): Cuboid {
        this.x1 -= n
        this.x2 += n
        this.y1 -= n2
        this.y2 += n2
        this.z1 -= n3
        this.z2 += n3
        return this
    }

    fun decrease(n: Double, n2: Double, n3: Double): Cuboid {
        this.x1 += n
        this.x2 -= n
        this.y1 += n2
        this.y2 -= n2
        this.z1 += n3
        this.z2 -= n3
        return this
    }

    fun move(n: Double, n2: Double, n3: Double): Cuboid {
        this.x1 += n
        this.x2 += n
        this.y1 += n2
        this.y2 += n2
        this.z1 += n3
        this.z2 += n3
        return this
    }

    fun toLocationMin(level: Level): Location {
        return Location(x1, y1, z1, level)
    }

    fun isVectorInside(vec: Vector3): Boolean {
        return (vec.x > this.x1 && vec.x < this.x2 && vec.z > this.z1 && vec.z < this.z2)
    }

    fun toLocationMax(level: Level): Location {
        return Location(x2, y2, z2, level)
    }


}