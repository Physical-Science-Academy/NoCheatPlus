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
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.network.protocol.AnimatePacket
import cn.nukkit.network.protocol.LevelSoundEventPacket
import cn.nukkit.network.protocol.PlayerActionPacket
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.CheckListener
import net.catrainbow.nocheatplus.checks.CheckType
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
            if (packet is PlayerActionPacket) {
                when (packet.action) {
                    PlayerActionPacket.ACTION_START_BREAK, PlayerActionPacket.ACTION_CONTINUE_BREAK, PlayerActionPacket.ACTION_CONTINUE_DESTROY_BLOCK -> {
                        NoCheatPlus.instance.getPlayerProvider(event.player).blockBreakData.setBreakingStatus(true)
                    }
                    PlayerActionPacket.ACTION_STOP_BREAK, PlayerActionPacket.ACTION_ABORT_BREAK -> {
                        NoCheatPlus.instance.getPlayerProvider(event.player).blockBreakData.setBreakingStatus(false)
                    }
                }
            }
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
                //空刀机制
                if (NoCheatPlus.instance.getPlayerProvider(player).fightData.lastDealDamage) {
                    NoCheatPlus.instance.getPlayerProvider(player).fightData.lastDealDamage = false
                    event.setCancelled()
                }
                if (!event.isCancelled) NoCheatPlus.instance.getPlayerProvider(player).fightData.lastDamageBoost =
                    System.currentTimeMillis()
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
        addCheck(Speed())
    }

}