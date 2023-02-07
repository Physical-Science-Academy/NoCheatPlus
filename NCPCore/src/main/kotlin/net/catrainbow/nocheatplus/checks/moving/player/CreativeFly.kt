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
import cn.nukkit.block.Block
import cn.nukkit.level.Location
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.MovingData
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil
import net.catrainbow.nocheatplus.checks.moving.magic.Magic
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.onGround
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.setback
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.players.IPlayerData
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 检测创造/观战模式下飞行时
 * 的速度等运动数据
 *
 * @author Catrainbow
 */
class CreativeFly : Check("checks.moving.creativefly", CheckType.MOVING_CREATIVE_FLY) {

    override fun onCheck(event: WrapperPacketEvent) {
        val packet = event.packet
        val player = event.player
        if (player.gamemode == 0 || player.gamemode == 2) return
        if (player.riding != null) return
        val provider = NoCheatPlus.instance.getPlayerProvider(player)
        if (packet is WrapperInputPacket) this.checkPlayerFly(
            player, packet.from, packet.to, provider.movingData, provider, System.currentTimeMillis()
        )
    }

    /**
     * 检测玩家创造模式下的异常飞行
     *
     * @param player
     * @param from
     * @param to
     * @param data
     * @param pData
     * @param now
     *
     */
    private fun checkPlayerFly(
        player: Player,
        from: Location,
        to: Location,
        data: MovingData,
        pData: IPlayerData,
        now: Long,
    ) {

        val fromOnGround = from.add(0.0, -0.3, 0.0).onGround()
        val toOnGround = to.add(0.0, -0.3, 0.0).onGround()
        val speed = data.getSpeed()
        val yDistance = to.y - from.y

        val isSamePos = speed == 0.0

        //检查创造模式下竖直位移
        if ((fromOnGround || !toOnGround) && !isSamePos) {
            if (data.getLiquidTick() == 0) {
                if (data.getWebTick() == 0) {
                    val vDistAir = this.vDistAir(now, player, from, to, toOnGround, yDistance, data, pData)
                    if (vDistAir[0] > vDistAir[1]) {
                        val violation = min((vDistAir[0] - vDistAir[1]) * 10.0, 5.0)
                        pData.addViolationToBuffer(this.typeName, violation)
                    }
                } else {
                    //It's not necessary to check these players who are in creative mode
                    val vDistWeb = this.vDistWeb()
                    if (vDistWeb[0] > vDistWeb[1])
                        pData.addViolationToBuffer(this.typeName, (vDistWeb[0] - vDistWeb[1]) * 10.0)
                }
            } else {
                /* TODO: Check player in liquid */
            }
        }

        if (ConfigData.logging_debug) {
            player.sendTip("$speed $yDistance")
        }

        pData.getViolationData(this.typeName).preVL(0.998)
    }

    /**
     * 限制y轴变化
     *
     * @param now
     * @param player
     * @param from
     * @param to
     * @param toOnGround
     * @param yDistance
     * @param data
     * @param pData
     *
     * @return In air speed
     */
    private fun vDistAir(
        now: Long,
        player: Player,
        from: Location,
        to: Location,
        toOnGround: Boolean,
        yDistance: Double,
        data: MovingData,
        pData: IPlayerData,
    ): DoubleArray {
        var allowDistance = 0.0
        var limitDistance = 0.0

        //重发操作
        var reset = false
        val lostGround = LocUtil.getUnderBlock(player).id == Block.AIR

        if (!player.isGliding && !player.isSwimming) {
            val aboveLimitDistance = this.vDistHLimited(now, player, from, to, toOnGround, yDistance, data, pData)
            if (!from.add(0.0, -0.3, 0.0).onGround()) {
                aboveLimitDistance[0] = Double.MIN_VALUE
                aboveLimitDistance[1] = Double.MAX_VALUE
            }
            if (aboveLimitDistance[0] > aboveLimitDistance[1]) {
                allowDistance = aboveLimitDistance[0]
                limitDistance = aboveLimitDistance[1]
                if (ConfigData.logging_debug) player.sendMessage("h_diff")
                reset = true
            }
            //motion check
            if (data.getLastMotionY() == 0.0 && !reset) {
                val speed = data.getSpeed()
                if (data.getSlabTick() == 0 && data.getStairTick() == 0) {
                    if (!data.isJump() && lostGround) {
                        val vAirCheck = this.inAirCheck(now, player, from, to, yDistance, data, pData)
                        if (vAirCheck[0] > vAirCheck[1]) return doubleArrayOf(vAirCheck[0], vAirCheck[1])
                    } else if (!data.isJump() && data.getLastInAirTicks() == 0 && from.add(0.0, -0.3, 0.0).onGround()) {
                        //total ground check
                        //在降落的时候可能出现速度变大的问题,单独考虑
                        if (data.getFullAirTick() == 0 && player.inAirTicks == 0 && data.getGroundTick() > 20) {
                            if (speed > Magic.DEFAULT_WALK_SPEED + Magic.DEFAULT_FLY_SPEED) {
                                if (ConfigData.logging_debug) player.sendMessage("short lag $yDistance")
                                allowDistance = speed
                                limitDistance = Magic.DEFAULT_WALK_SPEED + Magic.DEFAULT_FLY_SPEED
                                reset = true
                            }
                        }
                    }
                }
            }
        }

        if (reset) player.setback(data.getLastNormalGround(), this.typeName)

        return doubleArrayOf(allowDistance, limitDistance)
    }

    /**
     * 限制y轴变化
     *
     * @param now
     * @param player
     * @param from
     * @param to
     * @param toOnGround
     * @param yDistance
     * @param data
     * @param pData
     *
     * @return limited yDistance
     */
    private fun vDistHLimited(
        now: Long,
        player: Player,
        from: Location,
        to: Location,
        toOnGround: Boolean,
        yDistance: Double,
        data: MovingData,
        pData: IPlayerData,
    ): DoubleArray {
        var allowDistance = 0.0
        var limitDistance = 0.0
        val vData = pData.getViolationData(this.typeName)

        val speed = from.distance(to)
        val isJump = now - data.getLastJump() < 800 || data.isJump()

        if (speed == 0.0 && yDistance == 0.0) return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)

        //player.sendMessage("$speed $yDistance $isJump")

        if (yDistance > Magic.DEFAULT_JUMP_HEIGHT) {
            allowDistance = yDistance
            limitDistance = Magic.DEFAULT_JUMP_HEIGHT
            if (!isJump) limitDistance = 0.0
        } else if (speed > Magic.BUNNY_TINY_JUMP_MAX + Magic.LIMITED_CLIMB_SPEED * 2.0 + 0.15 && data.getLastMotionY() > 0.0) {
            allowDistance = speed
            limitDistance = min(abs(speed - 0.1), Magic.BUNNY_TINY_JUMP_FRICTION * 1.05)
            //若是平面行走,则额外补偿Violation
            if (toOnGround && player.isSprinting) allowDistance += 0.1
        }

        if (allowDistance > limitDistance) {
            if (vData.getPreVL("h_dist") >= 2) vData.clearPreVL("h_dist")
            else {
                vData.addPreVL("h_dist")
                return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
            }
        } else {
            vData.clearPreVL("h_dist")
            return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
        }

        return doubleArrayOf(allowDistance, limitDistance)
    }

    /**
     * 飞行检测
     *
     * @param now
     * @param player
     * @param from
     * @param to
     * @param yDistance
     * @param data
     * @param pData
     *
     * @return limited speed during flying
     */
    private fun inAirCheck(
        now: Long,
        player: Player,
        from: Location,
        to: Location,
        yDistance: Double,
        data: MovingData,
        pData: IPlayerData,
    ): DoubleArray {
        var allowDistance = 0.0
        var limitDistance = 0.0
        val vData = pData.getViolationData(this.typeName)

        if (ConfigData.logging_debug) player.sendMessage("${now - data.getLastJump()}")

        if (yDistance >= 0.0) {
            val speed = data.getSpeed()
            val verticalSpeed = from.distanceSquared(to)
            if (speed > 1.0 + Magic.DEFAULT_FLY_SPEED || verticalSpeed > 1.0 + Magic.DEFAULT_WALK_SPEED) {
                allowDistance = max(speed, verticalSpeed)
                limitDistance = 1.0 + Magic.DEFAULT_FLY_SPEED
            }
        }

        if (allowDistance > limitDistance) {
            if (vData.getPreVL("vertical_dist") > 2) vData.clearPreVL("vertical_dist")
            else {
                vData.addPreVL("vertical_dist")
                return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
            }
        } else {
            vData.clearPreVL("vertical_dist")
            return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
        }

        return doubleArrayOf(allowDistance, limitDistance)
    }

    private fun vDistWeb(): DoubleArray {
        return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
    }

}