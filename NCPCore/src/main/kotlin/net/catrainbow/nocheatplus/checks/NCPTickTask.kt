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
import cn.nukkit.scheduler.Task
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket

/**
 * 循坏任务
 *
 * @author Catrainbow
 */
class NCPTickTask : Task() {
    override fun onRun(p0: Int) {
        for (player in NoCheatPlus.instance.server.onlinePlayers.values) {
            this.handleWrapperInputPacket(player)
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
    }

}