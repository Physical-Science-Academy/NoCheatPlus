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
import cn.nukkit.block.Block
import cn.nukkit.block.BlockSlab
import cn.nukkit.block.BlockStairs
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil.Companion.isAboveBlock
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil.Companion.isAboveLiquid
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.isInLiquid
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.isInWeb
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.onClimbedBlock
import kotlin.math.abs
import kotlin.math.max

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
    private var maxAbsHeight = 0.0
    private var distanceXZ = 0.0
    private var useLocation = player.location


    fun onUpdate(now: Long) {
        this.tick++
        val player = this.getPlayer()
        val data = NoCheatPlus.instance.getPlayerProvider(player).movingData
        if (this.isLive) {
            //解决特殊方块的问题
            val block = player.add(0.0, -0.5, 0.0).levelBlock
            val subBlock = player.add(0.0, -0.4, 0.0).levelBlock
            if (block is BlockSlab || block is BlockStairs) {
                this.maxHeight = player.y - block.maxY
                this.distanceXZ = player.distance(this.useLocation)
                this.isLive = false
            } else if (subBlock is BlockSlab || subBlock is BlockStairs) {
                this.maxHeight = player.y - subBlock.maxY
                this.distanceXZ = player.distance(this.useLocation)
                this.isLive = false
            } else if (player.onClimbedBlock() || player.isInWeb() || player.isInLiquid()) {
                this.maxHeight = abs(max(player.y - player.levelBlock.minY, 0.0))
                this.distanceXZ = player.distance(this.useLocation)
                this.isLive = false
            }
            //初速夹角忽略计算
            if (!this.isJump && now - data.getLastJump() <= 200 && this.onGround) {
                this.isJump = true
                this.onGround = false
                this.startJumpY = player.add(0.0, -0.1, 0.0).y
                this.useLocation = player.location
            }
            if (this.isJump) {
                val h = abs(player.y - startJumpY) + LocUtil.getPlayerHeight(player)
                if (h > this.maxAbsHeight) this.maxAbsHeight = h
                if (player.y >= this.lastY) this.lastY = player.y
                else {
                    this.maxHeight = this.lastY - startJumpY
                    this.distanceXZ = player.distance(this.useLocation)
                    this.isLive = false
                }
            }
            this.lastY = player.y
        }
        if (now - data.getLastJump() >= 500) {
            if (!player.isAboveBlock(Block.AIR) && (player.isAboveLiquid() || player.add(
                    0.0, -0.5, 0.0
                ).levelBlock.isSolid)
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

    fun getAbsHeight(): Double {
        return if (canReturnResult() && this.maxAbsHeight >= 0.0) this.maxAbsHeight else 0.0
    }

    fun getDistanceXZ(): Double {
        return if (canReturnResult() && this.distanceXZ >= 0.0) this.distanceXZ else 0.0
    }

    private fun reset() {
        this.maxHeight = 0.0
        this.startJumpY = 0.0
        this.lastY = 0.0
        this.isJump = false
        this.tick = 0
        this.maxAbsHeight = 0.0
        this.distanceXZ = 0.0
        this.useLocation = this.getPlayer().location
        this.isLive = true
    }

    fun close() {
        this.isLive = false
    }

    private fun getPlayer(): Player {
        return NoCheatPlus.instance.server.getPlayer(name)
    }

}