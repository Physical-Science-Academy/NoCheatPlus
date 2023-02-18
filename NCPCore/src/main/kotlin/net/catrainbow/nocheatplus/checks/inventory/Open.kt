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
package net.catrainbow.nocheatplus.checks.inventory

import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.setback
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInventoryPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent

/**
 * 检测异常背包打开
 *
 * @author Catrainbow
 */
class Open : Check("checks.inventory.open", CheckType.INVENTORY_OPEN) {

    override fun onCheck(event: WrapperPacketEvent) {
        if (event.packet is WrapperInventoryPacket) {
            val player = event.player
            val pData = NoCheatPlus.instance.getPlayerProvider(player)
            val moveData = pData.movingData
            val data = pData.inventoryData
            val vData = pData.getViolationData(this.typeName)

            if (data.getToggleInventory())
                if (!moveData.isLive()) {
                    vData.addVL(1.0)
                    player.setback(moveData.getLastNormalGround(), this.typeName)
                }

            vData.preVL(0.998)
        }
    }

}