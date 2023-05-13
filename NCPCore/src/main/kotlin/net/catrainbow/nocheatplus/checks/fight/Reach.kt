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
import net.catrainbow.nocheatplus.feature.wrapper.WrapperDamagePacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent

/**
 * 攻击距离检测
 *
 * @author Catrainbow
 */
class Reach : Check("checks.fight.reach", CheckType.FIGHT_REACH) {

    override fun onCheck(event: WrapperPacketEvent) {
        val packet = event.packet
        val player = event.player
        val data = NoCheatPlus.instance.getPlayerProvider(player).fightData

        if (packet is WrapperDamagePacket) {
            val target = packet.target
            //unexpected attack
            if (player.id != packet.attacker.id) return
            val baseDistance = player.distance(target)
        }

    }

}