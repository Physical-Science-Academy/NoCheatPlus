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

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.inventory.InventoryClickEvent
import cn.nukkit.event.inventory.InventoryCloseEvent
import cn.nukkit.event.inventory.InventoryEvent
import cn.nukkit.event.inventory.InventoryOpenEvent
import cn.nukkit.event.inventory.InventoryTransactionEvent
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.CheckListener
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.dataPacket
import net.catrainbow.nocheatplus.feature.inventory.InventoryAction
import net.catrainbow.nocheatplus.feature.wrapper.WrapperEatFoodPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInventoryPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.feature.wrapper.WrapperUpdateInventoryPacket

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
            } else if (packet is WrapperInventoryPacket) {
                when (packet.type) {
                    InventoryAction.OPEN -> data.onOpen()
                    InventoryAction.CLOSED -> data.onClosed()
                    InventoryAction.CLICK -> data.onClicked()
                    else -> data.onClosed()
                }
            }
            data.onUpdate()
        } else if (event is InventoryEvent) {
            var player: Player? = null
            val action = when (event) {
                is InventoryClickEvent -> {
                    player = event.player
                    InventoryAction.CLICK
                }
                is InventoryOpenEvent -> {
                    player = event.player
                    InventoryAction.OPEN
                }
                is InventoryCloseEvent -> {
                    player = event.player
                    InventoryAction.CLOSED
                }
                else -> InventoryAction.UNKNOWN
            }
            if (player != null) {
                val packet = WrapperInventoryPacket(player)
                packet.player = player
                packet.time = System.currentTimeMillis()
                packet.type = action
                val callEvent = WrapperPacketEvent()
                callEvent.player = packet.player
                callEvent.packet = packet
                dataPacket(callEvent)
            }

        } else if (event is InventoryTransactionEvent) {
            val player = event.transaction.source
            val packet = WrapperUpdateInventoryPacket(player)
            packet.inventory = player.inventory
            packet.item = player.inventory.itemInHand
            val sendEvent = WrapperPacketEvent()
            sendEvent.packet = packet
            sendEvent.player = player
            dataPacket(sendEvent)
            if ((sendEvent.packet as WrapperUpdateInventoryPacket).isValid) event.setCancelled()
        }
    }

    init {
        this.addCheck(InstantEat())
        this.addCheck(InventoryMove())
        this.addCheck(Open())
        this.addCheck(Item())
    }

}