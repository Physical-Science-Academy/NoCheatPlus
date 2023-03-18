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

import cn.nukkit.item.Item
import cn.nukkit.item.enchantment.Enchantment
import cn.nukkit.potion.Effect
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperBreakBlockPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max

/**
 * 快速挖掘
 *
 * @author Catrainbow
 */
class FastBreak : Check("checks.blockbreak.fastbreak", CheckType.BLOCK_BREAK_FAST_BREAK) {

    override fun onCheck(event: WrapperPacketEvent) {
        if (event.packet is WrapperBreakBlockPacket) {
            val player = event.player
            if (player.gamemode == 1) return
            val block = (event.packet as WrapperBreakBlockPacket).block
            val pData = NoCheatPlus.instance.getPlayerProvider(player)
            val data = pData.blockBreakData
            val item = player.inventory.itemInHand
            val hasEnchanted = item.hasEnchantment(Enchantment.ID_EFFICIENCY)
            val enchantLevel = if (hasEnchanted) item.getEnchantment(Enchantment.ID_EFFICIENCY).level else 0
            if (enchantLevel >= 6) return
            var expectedTicks = ceil(block.getBreakTime(item, player) * 20.0)
            if (player.hasEffect(Effect.FATIGUE)) {
                expectedTicks *= 1.0 + (0.5 * player.getEffect(Effect.FATIGUE).amplifier + 1)
            }
            if (player.hasEffect(Effect.SWIFTNESS)) {
                if (item.id != Item.AIR && item.isPickaxe) {
                    if (player.getEffect(Effect.SWIFTNESS).amplifier >= 4) return
                    expectedTicks -= expectedTicks * 0.25 * (player.getEffect(Effect.SWIFTNESS).amplifier + 1)
                }
            }
            val balanceTime = (event.packet as WrapperBreakBlockPacket).breakTicks
            //平衡重复行为,精确检测挖掘时间
            val sumUsedTicks = max(0, (event.packet as WrapperBreakBlockPacket).usedTicks - 12)
            val effectiveBooster = if (sumUsedTicks > 0) max(
                0.0, (abs(balanceTime - expectedTicks) - ConfigData.check_fast_break_max) / sumUsedTicks
            )
            else 0.0
            val ignorantTicks = effectiveBooster * 12
            var diff = max(0.0, abs(balanceTime - expectedTicks) - ignorantTicks)
            if (item.isSword) diff /= 2.0

            if (diff > ConfigData.check_fast_break_max || diff < ConfigData.check_fast_break_min) {
                (event.packet as WrapperBreakBlockPacket).isValid = true
                data.setCancelled()
                pData.getViolationData(this.typeName).addVL(diff / 20.0)
            }

            if (ConfigData.logging_debug) {
                player.sendMessage("NCP BlockBreak Check $diff ${(event.packet as WrapperBreakBlockPacket).usedTicks}")
            }

            pData.getViolationData(this.typeName).preVL(0.05)
        }
    }

}