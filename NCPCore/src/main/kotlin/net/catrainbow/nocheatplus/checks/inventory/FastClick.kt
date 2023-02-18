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
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.getRealPing
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.setback
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.feature.inventory.InventoryAction
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInventoryPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import kotlin.math.max

/**
 * 自动拿箱子检测
 *
 * @author Catrainbow
 */
class FastClick : Check("checks.inventory.fastclick", CheckType.INVENTORY_FAST_CLICK) {

    override fun onCheck(event: WrapperPacketEvent) {
        if (event.packet is WrapperInventoryPacket) {
            if ((event.packet as WrapperInventoryPacket).type != InventoryAction.CLICK) return

            val player = event.player
            val pData = NoCheatPlus.instance.getPlayerProvider(player)
            val vData = pData.getViolationData(this.typeName)
            val data = pData.inventoryData

            val normal = System.currentTimeMillis() - data.getLastClicked()
            val balance = normal + max(player.getRealPing() - 50.0, 0.0) * 10

            if (balance < ConfigData.check_inventory_fast_click_delay) {
                vData.addVL((ConfigData.check_inventory_fast_click_delay - balance) * 0.1)
                player.setback(pData.movingData.getLastNormalGround(), this.typeName)
            }

            vData.preVL(0.998)
        }
    }

}