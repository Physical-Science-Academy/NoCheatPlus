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

import cn.nukkit.event.block.BlockBreakEvent
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.dataPacket
import net.catrainbow.nocheatplus.components.data.ICheckData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperBreakBlockPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import java.util.LinkedList

/**
 * 方块破坏数据
 *
 * @author Catrainbow
 */
class BlockBreakData : ICheckData {

    private var interactQueue: LinkedList<Long> = LinkedList()
    private var breakQueue: LinkedList<Long> = LinkedList()
    private var totalUsedTicks = 0
    private var usedBalanceTime = System.currentTimeMillis() - 1500L
    private var cancellable = false

    fun onUpdate() {
        if (breakQueue.size > 0) {
            interactQueue.clear()
            breakQueue.clear()
            usedBalanceTime = System.currentTimeMillis() - 1500L
        } else {
            this.totalUsedTicks = interactQueue.size
        }
    }

    fun onBreak(event: BlockBreakEvent) {
        val packet = WrapperBreakBlockPacket(event.player)
        packet.player = event.player
        var mathTicks = (System.currentTimeMillis() - usedBalanceTime).toDouble() / 1000.0 * 20.0
        mathTicks += this.totalUsedTicks * 5
        packet.breakTicks = mathTicks.toInt()
        packet.usedTicks = this.totalUsedTicks
        packet.block = event.block
        val sendEvent = WrapperPacketEvent()
        sendEvent.player = packet.player
        sendEvent.packet = packet
        dataPacket(sendEvent)
        this.breakQueue.add(System.currentTimeMillis() - 100L)
        if ((sendEvent.packet as WrapperBreakBlockPacket).isValid) {
            if (this.cancellable) {
                event.setCancelled()
                this.cancellable = false
            }
        }
    }

    fun setCancelled() {
        this.cancellable = true
    }

    fun onTicked() {
        this.usedBalanceTime = System.currentTimeMillis()
        interactQueue.add((this.usedBalanceTime + System.currentTimeMillis()) / 2L)
    }

}