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
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.feature.wrapper.WrapperUpdateInventoryPacket

/**
 * 物品检测
 *
 * @author Catrainbow
 */
class Item : Check("checks.inventory.item", CheckType.INVENTORY_ITEM) {

    override fun onCheck(event: WrapperPacketEvent) {
        val packet = event.packet
        if (packet is WrapperUpdateInventoryPacket) {
            val player = packet.player
            val pData = NoCheatPlus.instance.getPlayerProvider(player)
            val vData = pData.getViolationData(this.typeName)

            if (player.inventory.itemInHand.id == 0) return

            for (itemKey in player.inventory.contents.keys) {
                val item = player.inventory.contents[itemKey]
                if (item != null) {
                    //考虑特殊情况
                    if (item.id == 0) continue
                    if (item.getCount() > item.maxStackSize) {
                        packet.isValid = true
                        val diff = item.getCount() - item.maxStackSize
                        item.setCount(item.maxStackSize)
                        vData.addVL(diff * 0.5)
                    }
                }
            }
            vData.preVL(0.998)
        }
    }

}