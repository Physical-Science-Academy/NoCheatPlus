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
package net.catrainbow.nocheatplus.checks.moving.location.setback

import cn.nukkit.level.Location
import net.catrainbow.nocheatplus.NoCheatPlus

/**
 * Setback
 *
 * @author Catrainbow
 */
class SetBackEntry(location: Location, private var tick: Int) {

    private var worldName: String
    private var x = 0.0
    private var y = 0.0
    private var z = 0.0
    private var pitch = 0.0
    private var yaw = 0.0
    private var msTime = System.currentTimeMillis()
    private var isValid = false

    init {
        this.worldName = location.level.name
        this.x = location.x
        this.y = location.y
        this.z = location.z
        this.pitch = location.pitch
        this.yaw = location.yaw
        this.msTime = System.currentTimeMillis()
    }

    fun isValidAndOlderThan(other: SetBackEntry): Boolean {
        return isValid && this.msTime > other.msTime
    }

    fun setValid(boolean: Boolean) {
        this.isValid = boolean
    }

    fun isValid(): Boolean {
        return this.isValid
    }

    fun toLocation(): Location {
        return Location(x, y, z, pitch, yaw, NoCheatPlus.instance.server.getLevelByName(this.worldName))
    }

    fun canLagBack(): Boolean {
        return true
    }

}