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
package net.catrainbow.nocheatplus.checks.blockbreak

import cn.nukkit.math.Vector3
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.connectWithPC
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.getModX
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.getModY
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.getModZ
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.mathAngleBetween
import net.catrainbow.nocheatplus.feature.wrapper.WrapperBreakBlockPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.utilities.MathAngle

/**
 * 方块破坏方向
 *
 * @author Catrainbow
 */
class Direction : Check("checks.blockbreak.direction", CheckType.BLOCK_BREAK_DIRECTION) {

    override fun onCheck(event: WrapperPacketEvent) {
        if (event.packet is WrapperBreakBlockPacket) {
            val player = event.player
            //it's possible to detect creative mode player
            //if (player.game mode == 1) return
            val block = (event.packet as WrapperBreakBlockPacket).block
            val pData = NoCheatPlus.instance.getPlayerProvider(player)
            val data = pData.blockBreakData
            val faceVector = player.directionVector

            val breakVector = Vector3(faceVector.getModX(), faceVector.getModY(), faceVector.getModZ())
            val breakAngle = MathAngle.asDoubleDegree(block.location.mathAngleBetween(breakVector))

            if (player.connectWithPC()) {
                //TODO: Angle Detection
                //Angle Detection
                if (breakAngle > 90.0 || breakAngle < 0.0) {
                    data.setCancelled()
                    pData.addViolationToBuffer(this.typeName, 1.0, "Angle Detection")
                }
            }

            pData.getViolationData(this.typeName).preVL(0.998)
        }
    }

}