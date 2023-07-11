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
import net.catrainbow.nocheatplus.compat.nukkit.EmptyFood
import net.catrainbow.nocheatplus.compat.nukkit.FoodData118
import net.catrainbow.nocheatplus.feature.wrapper.WrapperEatFoodPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent

/**
 * 秒吃检测
 *
 * @author Catrainbow
 */
class InstantEat : Check("checks.inventory.instanteat", CheckType.INVENTORY_INSTANT_EAT) {

    override fun onCheck(event: WrapperPacketEvent) {
        val player = event.player
        val data = NoCheatPlus.instance.getPlayerProvider(player).inventoryData
        val pData = NoCheatPlus.instance.getPlayerProvider(player)

        var cancel = false
        var cancelSaturation = 0F
        var cancelFoodLevel = 0

        if (event.packet is WrapperEatFoodPacket) {
            if (!(event.packet as WrapperEatFoodPacket).eat) data.setEating(true)
            else {
                data.setEating(false)
                val totalTick = NoCheatPlus.instance.getPlayerProvider(player).movingData.getFoodTracker()!!.getCount()
                if (totalTick < FoodData118.DEFAULT_EAT_TICK) {
                    cancel = true
                    cancelSaturation = (event.packet as WrapperEatFoodPacket).food.restoreSaturation
                    cancelFoodLevel = (event.packet as WrapperEatFoodPacket).food.restoreFood
                    if ((event.packet as WrapperEatFoodPacket).food is EmptyFood) cancel = false
                }
            }
        }

        if (cancel) {
            pData.addViolationToBuffer(this.typeName, 1.2,"InstantEat SPEED LIMITED")
            if (player.foodData.level - cancelFoodLevel > 0) {
                val level = player.foodData.level
                player.foodData.setLevel(level - cancelFoodLevel, cancelSaturation)
            }
        }

    }

}