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
package net.catrainbow.nocheatplus.checks.moving

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.event.player.PlayerDeathEvent
import cn.nukkit.event.player.PlayerGameModeChangeEvent
import cn.nukkit.event.player.PlayerRespawnEvent
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.network.protocol.MovePlayerPacket
import cn.nukkit.network.protocol.PlayerAuthInputPacket
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.CheckListener
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.player.SurvivalFly
import net.catrainbow.nocheatplus.compat.Bridge118
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.respawn
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPlaceBlockPacket

/**
 * 子监听器
 *
 * @author Catrainbow
 */
class MovingCheckListener : CheckListener(CheckType.MOVING) {

    override fun onTick(event: Event) {
        if (event is BlockPlaceEvent) {
            val player = event.player
            val packet = WrapperPlaceBlockPacket(player)
            packet.block = event.block
            packet.blockAgainst = event.blockAgainst
            packet.blockReplace = event.blockReplace
            packet.time = System.currentTimeMillis()
            val wrapper = WrapperPacketEvent()
            wrapper.player = player
            wrapper.packet = packet
            NoCheatPlus.instance.server.pluginManager.callEvent(wrapper)
        } else if (event is DataPacketReceiveEvent) {
            //开启权威移动数据包模式
            if (event.packet is PlayerAuthInputPacket && !Bridge118.server_auth_mode) Bridge118.server_auth_mode = true
            val player = event.player
            if (event.packet is MovePlayerPacket || event.packet is PlayerAuthInputPacket) {
                if (!NoCheatPlus.instance.hasPlayer(player)) return
                if (NoCheatPlus.instance.getPlayerProvider(player).movingData.getPacketTracker() == null) return
                val tracker = NoCheatPlus.instance.getPlayerProvider(player).movingData.getPacketTracker()!!
                tracker.onPacketReceive(event.packet)
            }
        } else if (event is PlayerRespawnEvent) {
            val player = event.player
            player.respawn()
            if (NoCheatPlus.instance.hasPlayer(player)) NoCheatPlus.instance.getPlayerProvider(player).movingData.setLive(
                true
            )
        } else if (event is PlayerDeathEvent) {
            val player = event.entity
            if (NoCheatPlus.instance.hasPlayer(player)) NoCheatPlus.instance.getPlayerProvider(player).movingData.setLive(
                false
            )
        } else if (event is EntityDamageEvent) {
            if (event.entity is Player) {
                val player = event.entity as Player
                //虚空行走跳过检测
                if (event.cause == EntityDamageEvent.DamageCause.VOID) if (NoCheatPlus.instance.hasPlayer(player)) NoCheatPlus.instance.getPlayerProvider(
                    player
                ).movingData.setVoidHurt(
                    true
                )
            }
        } else if (event is PlayerGameModeChangeEvent) {
            val player = event.player
            if (NoCheatPlus.instance.hasPlayer(player)) NoCheatPlus.instance.getPlayerProvider(player).movingData.setLastNormalGround(
                player.location
            )
        }
    }

    init {
        this.addCheck(SurvivalFly())
    }

}