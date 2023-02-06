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
import cn.nukkit.level.Location
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.MovingData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.players.IPlayerData

/**
 * 检测创造/观战模式下飞行时
 * 的速度等运动数据
 *
 * @author Catrainbow
 */
class CreativeFly : Check("check.moving.creativefly", CheckType.MOVING_CREATIVE_FLY) {

    override fun onCheck(event: WrapperPacketEvent) {
        val packet = event.packet
        val player = event.player
        if (player.gamemode != 1 && player.gamemode != 3) return
        if (player.riding != null) return
        val provider = NoCheatPlus.instance.getPlayerProvider(player)
        if (packet is WrapperInputPacket) this.checkPlayerFly(
            player, packet.from, packet.to, provider.movingData, provider, System.currentTimeMillis()
        )
    }

    /**
     * 检测玩家创造模式下的异常飞行
     *
     * @@
     */
    private fun checkPlayerFly(
        player: Player,
        from: Location,
        to: Location,
        data: MovingData,
        pData: IPlayerData,
        now: Long,
    ) {


    }

}