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

import cn.nukkit.entity.item.EntityBoat
import cn.nukkit.entity.mob.EntityEnderDragon
import cn.nukkit.event.entity.EntityDamageEvent
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.actions.ActionFactory
import net.catrainbow.nocheatplus.actions.ActionType
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.magic.MagicVehicle
import net.catrainbow.nocheatplus.feature.wrapper.WrapperDamagePacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent

/**
 * 攻击距离检测
 *
 * @author Catrainbow
 */
class Reach : Check("checks.fight.reach", CheckType.FIGHT_REACH) {

    override fun onCheck(event: WrapperPacketEvent) {
        val packet = event.packet
        val player = event.player
        val data = NoCheatPlus.instance.getPlayerProvider(player).fightData
        val vData = NoCheatPlus.instance.getPlayerProvider(player).getViolationData(this.typeName)

        var revert = false

        if (packet is WrapperDamagePacket) {
            val target = packet.target
            //unexpected attack
            if (player.id != packet.attacker.id) return
            if (packet.cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                if (packet.cause == EntityDamageEvent.DamageCause.PROJECTILE) {
                    val yDistance = target.y - player.y
                    if (yDistance > player.eyeHeight && player.distanceSquared(target) < 1.0) {
                        data.lastDealDamage = true
                        revert = true
                    }
                    if (player.inventory.itemInHand.isSword) {
                        revert = true
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
        }

    }

}