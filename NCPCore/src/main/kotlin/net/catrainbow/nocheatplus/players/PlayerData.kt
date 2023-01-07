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

package net.catrainbow.nocheatplus.players

import cn.nukkit.Player
import cn.nukkit.level.Location
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.moving.MovingData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket

/**
 * 玩家数据
 *
 * @author Catrainbow
 */
open class PlayerData(player: Player) : IPlayerData {

    companion object {
        val allPlayersData: HashMap<String, PlayerData> = HashMap()
    }

    private val name = player.name
    var from: Location = player.location
    val movingData: MovingData = MovingData()

    fun update(packet: WrapperInputPacket) {
        this.from = packet.to
    }

    override fun getPlayerName(): String {
        return this.name
    }

    override fun getPlayer(): Player {
        return NoCheatPlus.instance.server.getPlayer(this.getPlayerName())
    }
}