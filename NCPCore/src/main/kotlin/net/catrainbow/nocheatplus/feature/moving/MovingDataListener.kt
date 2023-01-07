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
package net.catrainbow.nocheatplus.feature.moving

import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerMoveEvent
import cn.nukkit.level.Position
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.moving.model.DistanceData
import net.catrainbow.nocheatplus.feature.ITickListener

/**
 * 移动数据处理、
 *
 * @author Catrainbow
 */
class MovingDataListener : ITickListener {

    override fun onTick(event: Event) {

        //更新数据
        if (event is PlayerMoveEvent) {
            val player = event.player
            val distanceData = DistanceData(Position.fromObject(event.from), Position.fromObject(event.to))
            NoCheatPlus.instance.getPlayerProvider(player).movingData.handleMovingData(
                player, event.from, event.to, distanceData
            )
        }
    }

    override fun onEnabled() {

    }

    override fun onDisabled() {
    }
}