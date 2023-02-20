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

import cn.nukkit.AdventureSettings
import cn.nukkit.Player
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.level.Location
import cn.nukkit.potion.Effect
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.MovingData
import net.catrainbow.nocheatplus.checks.moving.magic.Magic
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.onGround
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.players.IPlayerData
import kotlin.math.max

/**
 * 无掉落伤害检测
 *
 * @author Catrainbow
 */
class NoFall : Check("checks.moving.nofall", CheckType.MOVING_NO_FALL) {

    override fun onCheck(event: WrapperPacketEvent) {
        val player = event.player
        if (player.gamemode == 1 || player.gamemode == 3) return
        val pData = NoCheatPlus.instance.getPlayerProvider(player)
        val data = pData.movingData
        if (!data.isSafeSpawn() || !data.isLive()) return
        if (data.getRespawnTick() > 0) return
        if (player.riding != null) return
        if (player.isSleeping) return
        if (data.isVoidHurt()) return
        val packet = event.packet
        if (packet is WrapperInputPacket) {
            if (packet.serverOnGround || packet.clientOnGround) {
                val fromOnGround = packet.from.add(0.0, -0.3, 0.0).onGround()
                val toOnGround = packet.to.add(0.0, -0.3, 0.0).onGround()
                val now = System.currentTimeMillis()
                this.handleOnGround(
                    now, player, packet.from, packet.to, fromOnGround, toOnGround, data.getMotionY(), data, pData
                )
            }
        }
    }

    /**
     * 处理玩家在地面上时的伤害,
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
     */
    private fun handleOnGround(
        now: Long,
        player: Player,
        from: Location,
        to: Location,
        fromOnGround: Boolean,
        toOnGround: Boolean,
        yDistance: Double,
        data: MovingData,
        pData: IPlayerData,
    ) {
        // Damage to be dealt
        val fallDist = data.getFallDist()
        val maxD = this.getDamage(fallDist)
        val vData = pData.getViolationData(this.typeName)
        if (from.distance(to) == 0.0) return
        if (ConfigData.check_no_fall_skip_allow_flight && player.adventureSettings.get(AdventureSettings.Type.ALLOW_FLIGHT)) return
        if ((ConfigData.check_no_fall_reset_vehicle && player.riding != null) || (ConfigData.check_no_fall_reset_on_teleport && now - data.getLastTeleport() < 800)) {
            vData.preVL(0.0)
            return
        }

        if (fromOnGround && toOnGround && maxD > Magic.FALL_DAMAGE_MINIMUM) {
            val deltaHealth = player.health - data.getLastHealth()

            //如果生命值没有变化,则判为NoFall
            if (deltaHealth == 0.0) {
                val correction = this.getApplicableFallHeight(player, data)
                if (correction > 0.0 || now - data.getLastJump() < 800 || yDistance < 0.0) {
                    if (ConfigData.check_no_fall_deal_damage) this.dealFallDamage(player, data, maxD)
                    if (ConfigData.check_no_fall_reset_violation) {
                        vData.preVL(0.0)
                        vData.setCancel()
                    }
                    vData.addVL(max(0.0, player.fallDistance - Magic.FALL_DAMAGE_DIST))
                }
            }

            player.resetFallDistance()
            vData.preVL(0.998)
        }

    }

    /**
     * 修正误差
     *
     * @param player
     * @param data
     *
     * @return damage
     */
    private fun getApplicableFallHeight(player: Player, data: MovingData): Double {
        val yDistance = max(data.getMotionY() - player.fallDistance, 0.0)

        //兼容回弹不误判
        //兼容Jump Boost的motion不误判
        if (yDistance > 0.0 && player.hasEffect(Effect.JUMP_BOOST)) if (player.getEffect(Effect.JUMP_BOOST).amplifier > 0) {
            val correction = if (data.getLastMotionY() > 0.0) max(yDistance - data.getLastMotionY(), 0.0)
            else yDistance
            if (correction > 0.0) return max(player.fallDistance - correction, 0.0)
        }
        return yDistance
    }

    /**
     * 计算伤害
     *
     * @return damage
     */
    private fun getDamage(fallDistance: Double): Double {
        return max(fallDistance - Magic.FALL_DAMAGE_DIST, 0.0)
    }

    /**
     * 回弹伤害
     *
     * @param player
     * @param damage
     * @param data
     */
    private fun dealFallDamage(player: Player, data: MovingData, damage: Double) {
        val event = EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, damage.toFloat())
        NoCheatPlus.instance.server.pluginManager.callEvent(event)
        if (event.isCancelled) {
            //重置血量
            data.setLastHealth(player.health)
        }
        player.resetFallDistance()
    }

}