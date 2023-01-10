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

import cn.nukkit.Player
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil

/**
 * 运动跟踪器
 *
 * @author Catrainbow
 */
class MoveTracker(player: Player) {

    private var isJump = false
    private var maxHeight = 0.0
    private var startJumpY = 0.0
    private var lastY = 0.0
    private var isLive = true
    private var name = player.name
    private var tick = 0
    private var onGround = true


    fun onUpdate(now: Long) {
        this.tick++
        val player = this.getPlayer()
        val data = NoCheatPlus.instance.getPlayerProvider(player).movingData
        if (this.isLive) {
            if (!this.isJump && now - data.getLastJump() <= 200 && this.onGround) {
                this.isJump = true
                this.onGround = false
                this.startJumpY = player.add(0.0, -0.1, 0.0).y
            }
            if (this.isJump) {
                if (player.y >= this.lastY) this.lastY = player.y
                else {
                    this.maxHeight = this.lastY - startJumpY
                    this.isLive = false
                }
            }
            this.lastY = player.y
        }
        if (now - data.getLastJump() >= 500) {
            if (LocUtil.getUnderBlock(player).id != 0 && (LocUtil.isLiquid(LocUtil.getUnderBlock(player)) || LocUtil.getUnderBlock(
                    player
                ).isSolid)
            ) {
                this.onGround = true
                this.reset()
            }
        }
    }

    private fun canReturnResult(): Boolean {
        return !isLive
    }

    fun getHeight(): Double {
        return if (canReturnResult() && this.maxHeight >= 0.0) this.maxHeight else 0.0
    }

    private fun reset() {
        this.maxHeight = 0.0
        this.startJumpY = 0.0
        this.lastY = 0.0
        this.isJump = false
        this.tick = 0
        this.isLive = true
    }

    fun close() {
        this.isLive = false
    }

    private fun getPlayer(): Player {
        return NoCheatPlus.instance.server.getPlayer(name)
    }

}