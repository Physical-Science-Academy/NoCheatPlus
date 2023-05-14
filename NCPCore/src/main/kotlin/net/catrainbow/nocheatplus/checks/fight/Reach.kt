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
package net.catrainbow.nocheatplus.checks.fight

import cn.nukkit.Player
import cn.nukkit.entity.item.EntityBoat
import cn.nukkit.entity.item.EntityEndCrystal
import cn.nukkit.entity.mob.EntityEnderDragon
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.math.Vector3
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.actions.ActionFactory
import net.catrainbow.nocheatplus.actions.ActionType
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.magic.Magic
import net.catrainbow.nocheatplus.checks.moving.magic.MagicVehicle
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperDamagePacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import kotlin.math.abs
import kotlin.math.hypot

/**
 * 攻击距离检测
 *
 * @author Catrainbow
 */
class Reach : Check("checks.fight.reach", CheckType.FIGHT_REACH) {
    override fun onCheck(event: WrapperPacketEvent) {
        val packet = event.packet
        val player = event.player

        if (player.gamemode == 1) return

        val pData = NoCheatPlus.instance.getPlayerProvider(player)
        val data = pData.fightData
        val vData = pData.getViolationData(this.typeName)

        var revert = false

        if (packet is WrapperDamagePacket) {
            val target = packet.target
            //unexpected attack
            if (player.id != packet.attacker.id) return
            if (pData.movingData.isEatFood()) revert = true
            if (!pData.movingData.isLive()) revert = true
            if (packet.cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                if (packet.cause == EntityDamageEvent.DamageCause.PROJECTILE) {
                    val yDistance = target.y - player.y
                    if (yDistance > player.eyeHeight && player.distanceSquared(target) < 1.0) {
                        data.lastDealDamage = true
                    }
                    if (player.inventory.itemInHand.isSword) {
                        vData.addVL(1.2 * packet.knockBack)
                        vData.addAction(ActionFactory(player, vData, ActionType.SETBACK).build())
                    }
                } else if (target.id == player.id) {
                    //空刀一次,防止给予自己伤害
                    data.lastDealDamage = true
                }
                return
            }
            val baseDistance = player.distance(target)
            var selfSub = player.add(0.0, player.eyeHeight.toDouble(), 0.0)
            //考虑载具中的特殊情况
            if (player.riding != null) {
                val rewriteBox = when (player.riding.networkId) {
                    EntityBoat.NETWORK_ID -> MagicVehicle.VEHICLE_DOWN_BOX + 0.12
                    EntityEnderDragon.NETWORK_ID -> MagicVehicle.VEHICLE_DOWN_BOX - 0.5
                    else -> MagicVehicle.VEHICLE_DOWN_BOX
                }
                selfSub = player.add(0.0, -rewriteBox, 0.0)
            }
            if (target.riding != null) {
                val rewriteBox = when (player.riding.networkId) {
                    EntityBoat.NETWORK_ID -> MagicVehicle.VEHICLE_DOWN_BOX + 0.12
                    EntityEnderDragon.NETWORK_ID -> MagicVehicle.VEHICLE_DOWN_BOX - 0.5
                    else -> MagicVehicle.VEHICLE_DOWN_BOX
                }
                selfSub = player.add(0.0, rewriteBox, 0.0)
            }
            if (target is Player) {
                if (NoCheatPlus.instance.hasPlayer(target)) {
                    val motionY = NoCheatPlus.instance.getPlayerProvider(target).movingData.getMotionY()
                    if (motionY * pData.movingData.getMotionY() < 0) {
                        val directionX = (target.x - player.x) / abs(target.x - player.x)
                        val directionZ = (target.z - player.z) / abs(target.z - player.z)
                        val vectorRay = Vector3(
                            pData.movingData.getMotionX() / 2.0,
                            (motionY - pData.movingData.getMotionY()) / 2.0,
                            pData.movingData.getMotionZ()
                        )
                        val diffPitch = abs(target.pitch - player.pitch)
                        val diffYaw = abs(target.yaw - player.yaw)
                        if (diffPitch < 10 && diffYaw < 22.5) {
                            vectorRay.setX(vectorRay.x * directionX)
                            vectorRay.setZ(vectorRay.z * directionZ)
                            selfSub.add(vectorRay)
                        }
                    }
                }
            }
            if (target is EntityEndCrystal) selfSub.add(1.0, 2.0, 1.0)
            //special situation
            val directionX = target.x - player.x / abs(target.x - player.x)
            val directionZ = target.z - player.z / abs(target.z - player.z)
            if (pData.movingData.getLiquidTick() > 20) {
                when (pData.movingData.getLiquidTick() % 2) {
                    1 -> selfSub.add(0.0, -0.025, 0.0)
                    0 -> selfSub.add(0.0, -0.012, 0.0)
                }
                val motion = pData.movingData.getKnockBackTick() * 0.01 + packet.knockBack * 0.1
                selfSub.add(-directionX * motion, 0.0, -directionZ * motion)
            }
            if (pData.movingData.getFullAirTick() > 13) {
                val horizon = if (directionX > 0) target.y - player.y else 0.0
                val vertical = if (directionZ > 0) hypot(target.z - player.z, target.x - player.x) else 0.0
                val finalHorizon = horizon + (player.inAirTicks / 20) * Magic.TINY_GRAVITY * 0.05
                val finalVertical = vertical + if (player.isSprinting) 0.3 else 0.0
                val finalDistance = hypot(finalHorizon, finalVertical)
                //超出攻击范围
                if (finalVertical / finalDistance < 0) {
                    data.lastDealDamage = true
                    return
                }
                if (baseDistance > ConfigData.check_fight_reach_range.getAverage(
                        1, 0
                    ) * 2.0 && finalDistance > ConfigData.check_fight_reach_range.getAverage(0, 1) * 2.0
                ) {
                    data.lastDealDamage = true
                    val violations = (finalDistance - ConfigData.check_fight_reach_range.getAverage(1, 0) * 2.0) * 5.0
                    vData.addVL(violations)
                    //player.sendMessage("Reach #NCP 2")
                    if (ConfigData.logging_debug) player.sendMessage("NCP Reach $finalDistance")
                    return
                }
            } else {
                if (baseDistance < 0.3) {
                    data.lastDealDamage = false
                    return
                }

                val hitBoxMin = Vector3(player.x - 0.3, player.y, player.z - 0.3)
                val hitBoxMax = Vector3(player.x + 0.3, player.y + player.eyeHeight, player.z + 0.3)
                if (target.x > hitBoxMin.x && target.x < hitBoxMax.x && target.z > hitBoxMin.z && target.z < hitBoxMax.z) {
                    //missed HitBox
                    if (player.horizontalFacing == target.horizontalFacing) {
                        data.lastDealDamage = baseDistance > 0.15
                        revert = false
                    }
                }

                val finalDistance = selfSub.distance(target)
                var posWeight = player.distance(selfSub) / target.distance(selfSub)
                if (posWeight > 1.0) posWeight = 1.0
                val distanceLimit = ConfigData.check_fight_reach_range.getAverage(
                    posWeight, 1.0 / posWeight
                ) - ConfigData.check_fight_reach_offset
                if (finalDistance > distanceLimit) {
                    data.lastDealDamage = true
                    val violations = (finalDistance - distanceLimit) * 5.0
                    vData.addVL(violations)
                    //player.sendMessage("Reach #NCP 1")
                    return
                }

                if (ConfigData.logging_debug) player.sendMessage("NCP Reach Missing HitBox $finalDistance $distanceLimit")
            }

            data.lastDealDamage = revert
            vData.preVL(0.998)
        }

    }

}