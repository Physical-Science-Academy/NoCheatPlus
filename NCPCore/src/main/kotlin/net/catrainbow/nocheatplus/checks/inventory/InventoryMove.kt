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
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent

/**
 * 背包状态移动检测
 *
 * @author Catrainbow
 */
class InventoryMove : Check("checks.inventory.move", CheckType.INVENTORY_MOVE) {

    override fun onCheck(event: WrapperPacketEvent) {
        if (event.packet is WrapperInputPacket) {
            val player = event.player
            val data = NoCheatPlus.instance.getPlayerProvider(player).inventoryData
            val moveData = NoCheatPlus.instance.getPlayerProvider(player).movingData
            val vData = NoCheatPlus.instance.getPlayerProvider(player)
            val pData = vData.getViolationData(this.typeName)
            //检测背包状态
            if (!(event.packet as WrapperInputPacket).serverOnGround) return
            if (player.riding != null) return

            val speed = moveData.getSpeed()
            if (data.getToggleInventory()) if (speed >= 0.2) {
                pData.addVL((speed - 0.2) * 10.0)
                player.setback(moveData.getLastNormalGround(), this.typeName)
            }

            pData.preVL(0.998)
        }
    }

}