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

import cn.nukkit.math.Vector3
import net.catrainbow.nocheatplus.NoCheatPlus

/**
 * Ghost Block Checker
 *
 * @author Catrainbow
 */
class GhostBlockChecker(player: String, vector3: Vector3, life: Int, id: Int) {

    private var delay: Int = 0
    private var life: Int = 0
    private var vec: Vector3
    private val player: String
    private var start: Boolean
    private var id: Int
    private var lagTick = 0

    fun onLag() {
        lagTick++
    }

    fun canLag(): Boolean {
        return lagTick < 100
    }

    fun isLive(): Boolean {
        return start
    }

    init {
        this.vec = vector3
        this.player = player
        this.life = life
        this.start = true
        this.id = id
    }

    fun isChangeBlock(): Boolean {
        return if (onlinePlayer()) {
            NoCheatPlus.instance.server.getPlayer(player).getLevel().getBlock(vec).id != this.id
        } else false
    }

    fun run() {
        if (delay >= life) {
            this.start = false
            return
        } else delay++
    }

    private fun onlinePlayer(): Boolean {
        for (p in NoCheatPlus.instance.server.onlinePlayers.values) {
            if (p.name.equals(player))
                return true
        }
        return false
    }

    fun getName(): String {
        return this.player
    }

    fun isLag(): Boolean {
        return if (start) {
            if (onlinePlayer()) {
                NoCheatPlus.instance.server.getPlayer(player).distance(vec) <= 10.0
            } else false
        } else false
    }

}