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
package net.catrainbow.nocheatplus.feature

import cn.nukkit.event.Event
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerQuitEvent
import cn.nukkit.plugin.Plugin
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.players.PlayerData

/**
 * 注册初始监听器
 *
 * @author Catrainbow
 */
class NCPListener : Listener {

    companion object {
        val listeners: ArrayList<ITickListener> = ArrayList()

        @JvmStatic
        fun <T : Event> registerEvent(
            listener: Listener,
            plugin: Plugin,
            clazz: Class<T>,
            executor: (T) -> Unit,
            ignoreCancelled: Boolean = false,
            priority: EventPriority = EventPriority.NORMAL,
        ) {
            plugin.server.pluginManager.registerEvent(
                clazz,
                listener,
                priority,
                { _: Listener?, event: Event? -> executor(event as T) },
                plugin,
                ignoreCancelled
            )
        }

    }

    init {
        registerEvent(
            this, NoCheatPlus.instance, PlayerJoinEvent::class.java, { playerJoins(it) }, true,
            EventPriority.HIGHEST
        )
        registerEvent(
            this, NoCheatPlus.instance, PlayerQuitEvent::class.java, { playerLeave(it) }, true,
            EventPriority.HIGHEST
        )
    }

    @EventHandler
    private fun playerJoins(event: PlayerJoinEvent) {
        val data = PlayerData(event.player)
        PlayerData.allPlayersData[data.getPlayerName()] = data
    }

    @EventHandler
    private fun playerLeave(event: PlayerQuitEvent) {
        val name = event.player.name
        PlayerData.allPlayersData.remove(name)
    }

}