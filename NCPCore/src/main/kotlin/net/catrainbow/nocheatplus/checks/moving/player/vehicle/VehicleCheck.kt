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
package net.catrainbow.nocheatplus.checks.moving.player.vehicle

import cn.nukkit.Player
import cn.nukkit.level.Location
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.MovingData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.players.IPlayerData

/**
 * 乘骑检测
 * 检测玩家在矿车和船等载具上的的速度等数值
 *
 * @author Catrainbow
 */
class VehicleCheck : Check("checks.moving.vehicle", CheckType.MOVING_VEHICLE) {

    override fun onCheck(event: WrapperPacketEvent) {
        val player = event.player
        val packet = event.packet
        val pData = NoCheatPlus.instance.getPlayerProvider(player)
        val data = pData.movingData
        //catch VehicleMoveEvent
        if (packet is WrapperInputPacket) {
            val vehicle = player.riding ?: return
            if (!vehicle.isAlive || !vehicle.isValid) return
            if (data.getSpeed() != 0.0) this.onVehicleMove(
                player, packet.from, packet.to, data, pData, System.currentTimeMillis()
            )
        }
    }

    /**
     * 载具移动更新
     *
     * @param player
     * @param from
     * @param to
     * @param data
     * @param pData
     * @param now
     */
    private fun onVehicleMove(
        player: Player,
        from: Location,
        to: Location,
        data: MovingData,
        pData: IPlayerData,
        now: Long,
    ) {
        var speed = to.distance(from)
        val maxSpeed = data.getSpeedTracker()!!.getMaxSpeed()
        if (speed < maxSpeed) speed = maxSpeed
        //the max speed is 0.52 and lag for 3 times
    }

}