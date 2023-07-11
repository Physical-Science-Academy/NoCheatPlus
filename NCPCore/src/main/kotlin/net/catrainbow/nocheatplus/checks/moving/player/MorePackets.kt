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

import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.getRealPing
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.setback
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import kotlin.math.max

/**
 * 数据包数量检测
 *
 * @author Catrainbow
 */

//这个检测能够检测100%的作弊者
//this check catches 100% of cheaters
class MorePackets : Check("checks.moving.morepackets", CheckType.MOVING_MORE_PACKETS) {
    override fun onCheck(event: WrapperPacketEvent) {
        val player = event.player
        val pData = NoCheatPlus.instance.getPlayerProvider(player)

        //如果这个包不应该发送,那么判定它是额外的包
        if (event.isInvalid()) {
            val violation =
                max(0.0, (1000 - player.getRealPing() - (20 - NoCheatPlus.instance.server.ticksPerSecond) * 10) / 500.0)
            if (violation > 0.0) {
                pData.addViolationToBuffer(this.typeName, violation,"INVALID PACKET")
                player.setback(
                    NoCheatPlus.instance.getPlayerProvider(player).movingData.getLastNormalGround(), this.typeName
                )
            }
        }

        //修复一个因为摔落瞬间无运动标签而带来的误判
        if (pData.movingData.isFallHurt()) pData.getViolationData(this.typeName).setCancel()

        pData.getViolationData(typeName).preVL(0.998)
    }

}