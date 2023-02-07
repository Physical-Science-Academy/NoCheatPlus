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

import cn.nukkit.event.Event
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.CheckListener
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.feature.wrapper.WrapperEatFoodPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent

/**
 * 子监听器
 *
 * @author Catrainbow
 */
class InventoryListener : CheckListener(CheckType.INVENTORY) {

    override fun onTick(event: Event) {
        if (event is WrapperPacketEvent) {
            val packet = event.packet
            val player = event.player
            val data = NoCheatPlus.instance.getPlayerProvider(player).inventoryData
            if (packet is WrapperEatFoodPacket) {
                if (!data.isEating() && packet.eat) {
                    data.setEating(true)
                } else data.setEating(false)
            }
            data.onUpdate()
        }
    }

    init {
        this.addCheck(InstantEat())
    }

}