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
import cn.nukkit.level.Position
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.MovingData
import net.catrainbow.nocheatplus.checks.moving.model.DistanceData
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.players.IPlayerData

/**
 * 检测玩家处于生存/冒险状态下,潜行/疾跑/游泳时的
 * 跳跃高度/跳跃距离/空中移动速度,etc.
 *
 * @author Catrainbow
 */
class SurvivalFly : Check("survival fly", CheckType.MOVING_SURVIVAL_FLY) {

    // Tags
    private val tags: ArrayList<String> = ArrayList()

    override fun onCheck(event: WrapperPacketEvent) {
        val player = event.player
        val pData = NoCheatPlus.instance.getPlayerProvider(player)
        val data = pData.movingData
        val packet = event.packet
        if (packet is WrapperInputPacket) this.checkPlayerFly(
            player, packet.from, packet.to, data, pData, System.currentTimeMillis()
        )
    }

    /**
     * @param player
     * @param from
     * @param to
     * @param data
     * @param pData
     * @param now
     *
     * @return
     */
    private fun checkPlayerFly(
        player: Player,
        from: Location,
        to: Location,
        data: MovingData,
        pData: IPlayerData,
        now: Long,
    ) {

        this.tags.clear()
        val debug = ConfigData.logging_debug

        val isSamePos = to.distance(from) == 0.0
        val distanceData = DistanceData(Position.fromObject(from), Position.fromObject(to))
        val xDistance = distanceData.xDiff
        var yDistance = distanceData.yDiff
        val zDistance = distanceData.zDiff
        var hasHDistance = true

        if (isSamePos) hasHDistance = false
        else if (xDistance == 0.0 && zDistance == 0.0) {
            yDistance = 0.0
            hasHDistance = false
        } else {
            hasHDistance = true
        }

        val fromOnGround = from.levelBlock.id == 0
        val toOnGround = to.levelBlock.id == 0
        val sprinting = false

        //检测玩家疾跑状态改变时的运动情况
        if (data.getLoseSprintCount() > 0) {
            
        }
    }

}