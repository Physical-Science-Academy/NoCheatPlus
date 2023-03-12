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

import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 攻速检测
 *
 * @author Catrainbow
 */
class Speed : Check("checks.fight.speed", CheckType.FIGHT_SPEED) {

    override fun onCheck(event: WrapperPacketEvent) {
        val player = event.player
        val packet = event.packet
        val pData = NoCheatPlus.instance.getPlayerProvider(player)
        val data = pData.fightData

        if (packet is WrapperInputPacket) {

            if (data.speedShortTermTick > 5) data.speedShortTermTick = 0

            if (data.getClickPerSecond() > ConfigData.check_fight_max_speed) {
                data.speedShortTermTick += (data.getClickPerSecond() - ConfigData.check_fight_max_speed).toInt()
                if (data.speedShortTermTick >= 5) {
                    val speedVL = max(
                        0.0, (data.getClickPerSecond() - ConfigData.check_fight_max_speed) * (min(
                            1.0, abs(1.0 - data.getClickPerSecondVariance())
                        )) * data.speedShortTermTick
                    )
                    if (data.getClickPerSecondVariance() <= ConfigData.check_fight_deal_variance) pData.addViolationToBuffer(
                        this.typeName, speedVL
                    )
                    if (ConfigData.check_no_fall_deal_damage) data.lastDealDamage = true
                }
            }

            if (ConfigData.logging_debug) {
                if (data.getClickPerSecond() > 0.0 && data.getClickPerSecondVariance() > 0.0) player.sendMessage("clickPerSecond ${data.getClickPerSecond()} variance ${data.getClickPerSecondVariance()}")
            }

            pData.getViolationData(this.typeName).preVL(0.998)
        }
    }

}