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
import cn.nukkit.event.Event
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.event.player.PlayerInteractEvent
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.network.protocol.AnimatePacket
import cn.nukkit.network.protocol.LevelSoundEventPacket
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.CheckListener
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.dataPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperDamagePacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent

/**
 * 子监听器
 *
 * @author Catrainbow
 */
class FightListener : CheckListener(CheckType.FIGHT) {

    override fun onTick(event: Event) {
        if (event is DataPacketReceiveEvent) {
            val packet = event.packet
            if (packet is LevelSoundEventPacket) {
                if (packet.sound == LevelSoundEventPacket.SOUND_HIT) {
                    NoCheatPlus.instance.getPlayerProvider(event.player).fightData.lastInteractBoost.add(System.currentTimeMillis())
                    return
                }
            }
            var handle = false
            if ((packet is LevelSoundEventPacket && (packet.sound == LevelSoundEventPacket.SOUND_ATTACK || packet.sound == LevelSoundEventPacket.SOUND_ATTACK_STRONG || packet.sound == LevelSoundEventPacket.SOUND_ATTACK_NODAMAGE)) || (packet is AnimatePacket && packet.action == AnimatePacket.Action.SWING_ARM)) {
                handle = true
                if (packet is LevelSoundEventPacket) NoCheatPlus.instance.getPlayerProvider(event.player).fightData.attackSoundBoost =
                    System.currentTimeMillis()
                if (packet is AnimatePacket) if (System.currentTimeMillis() - NoCheatPlus.instance.getPlayerProvider(
                        event.player
                    ).fightData.attackSoundBoost < 1000
                ) handle = false
            }
            if (handle) this.handleNormalSwing(event.player)
        } else if (event is WrapperPacketEvent) {
            val player = event.player
            val data = NoCheatPlus.instance.getPlayerProvider(player)
            if (data.blockBreakData.isBreaking()) data.fightData.setClickPerSecondInteract(0) else data.fightData.setClickPerSecondInteract(
                1
            )
            data.fightData.onUpdate()
        } else if (event is EntityDamageByEntityEvent) {
            if (event.damager is Player) {
                val player = event.damager as Player
                val packet = WrapperDamagePacket(player)
                packet.knockBack = event.knockBack
                packet.attacker = player
                packet.target = event.entity
                packet.cause = event.cause
                val wrapperEvent = WrapperPacketEvent()
                wrapperEvent.player = packet.player
                wrapperEvent.packet = packet
                dataPacket(wrapperEvent)
                if (!NoCheatPlus.instance.getPlayerProvider(player).movingData.isLive()) {
                    event.setCancelled()
                    NoCheatPlus.instance.kickPlayer(player, CheckType.UNKNOWN_PACKET)
                    return
                }
                //空刀机制
                if (NoCheatPlus.instance.getPlayerProvider(player).fightData.lastDealDamage) {
                    NoCheatPlus.instance.getPlayerProvider(player).fightData.lastDealDamage = false
                    event.setCancelled()
                }
                //排除远程攻击机制
                if (!event.isCancelled && event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) NoCheatPlus.instance.getPlayerProvider(
                    player
                ).fightData.lastDamageBoost = System.currentTimeMillis()
            }
        } else if (event is PlayerInteractEvent) {
            if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                val player = event.player
                NoCheatPlus.instance.getPlayerProvider(player).fightData.lastInteractBoost.add(System.currentTimeMillis())
            }
        }
    }

    private fun handleNormalSwing(player: Player) {
        val pData = NoCheatPlus.instance.getPlayerProvider(player)
        val data = pData.fightData

        val queue = data.getSwingQueue()
        queue.add(System.currentTimeMillis())
        data.updateSwingQueue(queue)
    }

    init {
        addCheck(Reach())
        addCheck(Speed())
    }

}