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
package net.catrainbow.nocheatplus.checks.moving.player

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.Block
import cn.nukkit.level.Location
import cn.nukkit.level.Position
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.MovingData
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil
import net.catrainbow.nocheatplus.checks.moving.magic.GhostBlockChecker
import net.catrainbow.nocheatplus.checks.moving.magic.Magic
import net.catrainbow.nocheatplus.checks.moving.model.DistanceData
import net.catrainbow.nocheatplus.checks.moving.util.MovingUtil
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPlaceBlockPacket
import net.catrainbow.nocheatplus.players.IPlayerData
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 检测玩家处于生存/冒险状态下,潜行/疾跑/游泳时的
 * 跳跃高度/跳跃距离/空中移动速度,etc.
 *
 * @author Catrainbow
 */
class SurvivalFly : Check("survival fly", CheckType.MOVING_SURVIVAL_FLY) {

    // Tags
    private val tags: ArrayList<String> = ArrayList()

    override fun onCheck(event: WrapperPacketEvent) {
        val player = event.player
        if (player.gamemode == 1 || player.gamemode == 3) return
        val pData = NoCheatPlus.instance.getPlayerProvider(player)
        val data = pData.movingData
        val packet = event.packet
        if (packet is WrapperInputPacket) this.checkPlayerFly(
            player, packet.from, packet.to, data, pData, System.currentTimeMillis()
        ) else if (packet is WrapperPlaceBlockPacket) this.updateGhostBlock(packet, data)
    }

    /**
     * @param player
     * @param from
     * @param to
     * @param data
     * @param pData
     * @param now
     *
     * @return
     */
    private fun checkPlayerFly(
        player: Player,
        from: Location,
        to: Location,
        data: MovingData,
        pData: IPlayerData,
        now: Long,
    ) {

        this.tags.clear()
        val debug = ConfigData.logging_debug

        val isSamePos = to.distance(from) == 0.0
        val distanceData = DistanceData(Position.fromObject(from), Position.fromObject(to))
        val xDistance = distanceData.xDiff
        var yDistance = distanceData.yDiff
        val zDistance = distanceData.zDiff
        var hasHDistance = true

        //Ghost Block Tracker

        if (isSamePos) hasHDistance = false
        else if (xDistance == 0.0 && zDistance == 0.0) {
            yDistance = 0.0
            hasHDistance = false
        } else {
            hasHDistance = true
        }

        val fromOnGround = from.add(0.0,-0.5,0.0).levelBlock.id != 0
        val toOnGround = to.add(0.0,-0.5,0.0).levelBlock.id != 0
        var sprinting = false

        //检测玩家疾跑状态改变时的运动情况
        if (data.getLoseSprintCount() > 0) {
            if (toOnGround && (fromOnGround || yDistance < Magic.WALK_SPEED)) {
                sprinting = data.getLoseSprintCount() < 3
                data.setLoseSprintCount(0)
                tags.add("invalidate_lose_sprint")
            } else {
                sprinting = true
                tags.add("lose_sprint")
                if (data.getLoseSprintCount() < 3 && toOnGround) data.setLoseSprintCount(0)
            }
        }

        val walkSpeed = Magic.WALK_SPEED * (player.movementSpeed / Magic.DEFAULT_WALK_SPEED)
        this.setNextFriction(from, to, data)
        data.getGhostBlockChecker().run()

        //幽灵方块追踪器
        var lagGhostBlock = false
        if (data.getGhostBlockChecker().isLive()) {
            val vDistGhost = this.vDistGhostBlock(data.getGhostBlockChecker())
            if (vDistGhost[0] >= vDistGhost[1]) lagGhostBlock = true
        }

        val distAir = this.vDistAir(now, player, from, to, fromOnGround, toOnGround, yDistance, data, pData)

    }

    /**
     * 空中滞留检测
     *
     * @param now
     * @param player
     * @param from
     * @param to
     * @param fromOnGround
     * @param toOnGround
     * @param yDistance
     * @param data
     * @param pData
     *
     * @return
     */
    private fun vDistAir(
        now: Long,
        player: Player,
        from: Location,
        to: Location,
        fromOnGround: Boolean,
        toOnGround: Boolean,
        yDistance: Double,
        data: MovingData,
        pData: IPlayerData,
    ): DoubleArray {
        if (!toOnGround && !fromOnGround) {
            val vanillaFall = this.onFallingVertical(data) && this.onFallingHorizontal(
                yDistance, data.getLastMotionY(), data.getLastFrictionVertical(), 0.0
            )

            if (!vanillaFall) data.clearListRecord()
            else player.sendMessage("11")
        }
        return doubleArrayOf(0.0, 0.0)
    }

    /**
     * 更新幽灵方块跟踪器
     *
     * @param packet
     */
    private fun updateGhostBlock(packet: WrapperPlaceBlockPacket, data: MovingData) {
        val player = packet.player
        data.setGhostBlockChecker(GhostBlockChecker(player.name, packet.block, 45, player.inventory.itemInHand.id))
    }

    private fun vDistGhostBlock(ghostBlockChecker: GhostBlockChecker): DoubleArray {
        var vAllowDistance = 0.0
        val vLimitDistance = 0.5

        //幽灵方块判断,通过追踪器来阻止它的误判
        ghostBlockChecker.run()
        if (ghostBlockChecker.isLive()) {
            if (ghostBlockChecker.isChangeBlock() && ghostBlockChecker.isLag()) {
                if (ghostBlockChecker.canLag()) {
                    vAllowDistance += 0.25
                    ghostBlockChecker.onLag()
                }
            }
        }
        return doubleArrayOf(vAllowDistance, vLimitDistance)
    }

    /**
     * set next friction
     *
     * @param from
     * @param to
     * @param data
     */
    private fun setNextFriction(from: Location, to: Location, data: MovingData) {
        if (from.levelBlock.id == Block.COBWEB || to.levelBlock.levelBlock.id == Block.COBWEB) {
            data.setNextHorizontalFriction(0.0)
            data.setNextVerticalFriction(0.0)
        } else if (from.levelBlock.canBeClimbed() || to.levelBlock.canBeClimbed()) {
            data.setNextHorizontalFriction(0.0)
            data.setNextVerticalFriction(0.0)
        } else if (LocUtil.isLiquid(from.levelBlock)) {
            if (LocUtil.isLava(from.levelBlock)) {
                data.setNextVerticalFriction(Magic.FRICTION_MEDIUM_LAVA)
                data.setNextHorizontalFriction(Magic.FRICTION_MEDIUM_LAVA)
            }
            if (LocUtil.isWater(from.levelBlock)) {
                data.setNextVerticalFriction(Magic.FRICTION_MEDIUM_WATER)
                data.setNextHorizontalFriction(Magic.FRICTION_MEDIUM_WATER)
            }
        } else if (to.levelBlock.id == 0 && from.levelBlock.id == 0) {
            data.setNextHorizontalFriction(Magic.FRICTION_MEDIUM_AIR)
            data.setNextVerticalFriction(Magic.FRICTION_MEDIUM_AIR)
        } else {
            data.setNextHorizontalFriction(0.0)
            data.setNextVerticalFriction(Magic.FRICTION_MEDIUM_AIR)
        }

    }

    /**
     * on vertical falling
     *
     * @return
     */
    private fun onFallingVertical(data: MovingData,samePos:Boolean): Boolean {

    }

}