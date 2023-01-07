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
package net.catrainbow.nocheatplus.feature.chat

import cn.nukkit.event.Event
import cn.nukkit.event.player.PlayerCommandPreprocessEvent
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.feature.ITickListener
import net.catrainbow.nocheatplus.feature.wrapper.WrapperCommandPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent

/**
 * Chat Listener
 *
 * @author Catrainbow
 */
class ChatTickListener : ITickListener {

    override fun onTick(event: Event) {
        if (event is PlayerCommandPreprocessEvent) {
            val player = event.player
            var message = event.message
            if (message.contains(" ")) message = message.split(" ")[0]
            if (ConfigData.protection_command_hide_active)
                if (ConfigData.protection_command_commands.contains(message)) {
                    event.setCancelled()
                    player.sendMessage(ConfigData.protection_command_hide_message)
                }
            this.handleCommandPacket(event)
        }
    }

    private fun handleCommandPacket(event: PlayerCommandPreprocessEvent) {
        val callEvent = WrapperPacketEvent()
        val wrapperCommandPacket = WrapperCommandPacket(event.player)
        wrapperCommandPacket.message = event.message
        callEvent.packet = wrapperCommandPacket
        NoCheatPlus.instance.server.pluginManager.callEvent(callEvent)
    }

    override fun onEnabled() {

    }

    override fun onDisabled() {

    }
}