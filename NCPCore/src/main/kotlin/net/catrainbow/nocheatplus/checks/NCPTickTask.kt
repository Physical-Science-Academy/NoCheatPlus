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
package net.catrainbow.nocheatplus.checks

import cn.nukkit.Player
import cn.nukkit.level.Position
import cn.nukkit.math.Vector3
import cn.nukkit.scheduler.Task
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import kotlin.math.abs

/**
 * 循坏任务
 *
 * @author Catrainbow
 */
class NCPTickTask : Task() {
    override fun onRun(p0: Int) {
        for (player in NoCheatPlus.instance.server.onlinePlayers.values) {
            this.handleWrapperInputPacket(player)
            this.tickActions(player)
        }
    }

    /**
     * @link WrapperPacketEvent
     */
    private fun handleWrapperInputPacket(player: Player) {
        val wrapperInputPacket = WrapperInputPacket(player)
        wrapperInputPacket.clientOnGround = player.onGround
        wrapperInputPacket.serverOnGround = LocUtil.getUnderBlock(player).id != 0

        if (!NoCheatPlus.instance.hasPlayer(player)) return

        val data = NoCheatPlus.instance.getPlayerProvider(player)

        wrapperInputPacket.from = data.from
        wrapperInputPacket.to = player.location
        wrapperInputPacket.position = Position.fromObject(wrapperInputPacket.to)
        wrapperInputPacket.motion =
            Vector3(data.movingData.getMotionX(), data.movingData.getMotionY(), data.movingData.getMotionZ())
        wrapperInputPacket.speed = data.movingData.getSpeed()

        val yaw = abs(wrapperInputPacket.to.yaw - wrapperInputPacket.from.yaw)
        val pitch = abs(wrapperInputPacket.to.pitch - wrapperInputPacket.from.pitch)
        wrapperInputPacket.rotation = Vector3(pitch, yaw, pitch)
        wrapperInputPacket.inputMode = player.loginChainData.deviceOS
        wrapperInputPacket.clientPlayMode = player.gamemode

        val event = WrapperPacketEvent()
        event.player = player
        event.packet = wrapperInputPacket
        NoCheatPlus.instance.server.pluginManager.callEvent(event)
    }

    /**
     * Tick actions
     *
     * @link ViolationData
     */
    private fun tickActions(player: Player) {
        if (!NoCheatPlus.instance.hasPlayer(player)) return
        val playerData = NoCheatPlus.instance.getPlayerProvider(player)
        for (checkType in CheckType.values()) {
            if (playerData.containCheckType(checkType)) {
                playerData.getViolationData(checkType).update()
            }
        }
    }

}