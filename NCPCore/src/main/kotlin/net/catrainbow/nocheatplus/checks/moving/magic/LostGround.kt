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
package net.catrainbow.nocheatplus.checks.moving.magic

import cn.nukkit.Player
import cn.nukkit.level.Location
import net.catrainbow.nocheatplus.checks.moving.MovingData
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.onGround

/**
 * LostGround
 *
 * @author Catrainbow
 */
class LostGround(val player: Player, val data: MovingData) {

    var usedLocation: Location = player.location
    var lastTags: ArrayList<String> = ArrayList()
    var isLive = false
    var lastDist = 0.0
    private var motionDirection = 0.0
    private var lastMotionDirection = 0.0
    private var vYDist: ArrayList<Double> = ArrayList()
    private var setClear = false

    fun lostGround(location: Location) {
        this.usedLocation = location
        this.isLive = true
    }

    fun onUpdate() {
        this.motionDirection = data.getMotionY()
        if (this.player.onGround() && data.getGroundTick() > 20) {
            this.usedLocation = player.location
            this.lastDist = 0.0
        }
        if (setClear) {
            this.vYDist.clear()
            this.setClear = false
            this.isLive = false
        }
        if (motionDirection <= 0.0 && lastMotionDirection > 0.0) {
            vYDist.add(player.location.y)
        }
        this.lastMotionDirection = motionDirection
    }

    fun setClear() {
        this.setClear = true
    }

    fun compareUsedLocation(): Boolean {
        return if (vYDist.size > 0) {
            val lastPosition = vYDist[vYDist.size - 1]
            lastPosition < usedLocation.y
        } else true
    }

    fun getVYDist(): ArrayList<Double> {
        return this.vYDist
    }

    override fun toString(): String {
        val builder = StringBuilder("default: ${usedLocation.y}")
        for (y in vYDist) {
            builder.append("\n").append("->$y")
        }
        return builder.toString()
    }

}