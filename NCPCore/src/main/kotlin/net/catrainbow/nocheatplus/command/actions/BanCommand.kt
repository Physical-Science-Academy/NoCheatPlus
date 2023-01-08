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
package net.catrainbow.nocheatplus.command.actions

import cn.nukkit.command.CommandSender
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.command.NCPSubCommand
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.utilities.i18n.I18N.Companion.getString

class BanCommand : NCPSubCommand("ban") {
    override fun getDescription(): String {
        return getString("command.ban.description")
    }

    override fun getAliases(): Array<String> {
        return arrayOf("ban")
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage("${ConfigData.logging_prefix}${getString("command.general.unknownUsage")}")
            return true
        }
        val target = args[1]
        var online = false
        for (player in NoCheatPlus.instance.server.onlinePlayers.values) {
            if (player.name == target) online = true
        }
        if (!online) {
            sender.sendMessage("${ConfigData.logging_prefix}${getString("command.general.playerOffline")}")
            return true
        }
        val player = NoCheatPlus.instance.server.getPlayer(target)
        when (args.size) {
            5 -> {
                val days = args[2].toInt()
                val hours = args[3].toInt()
                val minutes = args[4].toInt()
                NoCheatPlus.instance.banPlayer(player, days, hours, minutes)
                sender.sendMessage("${ConfigData.logging_prefix}${getString("command.ban.success")}")
            }
            4 -> {
                val days = args[2].toInt()
                val hours = args[3].toInt()
                NoCheatPlus.instance.banPlayer(player, days, hours)
                sender.sendMessage("${ConfigData.logging_prefix}${getString("command.ban.success")}")
            }
            3 -> {
                val days = args[2].toInt()
                NoCheatPlus.instance.banPlayer(player, days)
                sender.sendMessage("${ConfigData.logging_prefix}${getString("command.ban.success")}")
            }
            else -> sender.sendMessage("${ConfigData.logging_prefix}${getString("command.general.unknownUsage")}")
        }
        return true
    }
}