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
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.command.NCPSubCommand
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.utilities.i18n.I18N
import net.catrainbow.nocheatplus.utilities.i18n.I18N.Companion.getString

class KickCommand : NCPSubCommand("kick") {
    override fun getDescription(): String {
        return getString("command.kick.description")
    }

    override fun getAliases(): Array<String> {
        return arrayOf("kick")
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage("${ConfigData.logging_prefix}${I18N.getString("command.general.playerOffline")}")
            return true
        }
        val target = args[1]
        var online = false
        for (player in NoCheatPlus.instance.server.onlinePlayers.values) {
            if (player.name == target) online = true
        }
        if (online) {
            NoCheatPlus.instance.kickPlayer(NoCheatPlus.instance.server.getPlayer(target), CheckType.STAFF)
            sender.sendMessage("${ConfigData.logging_prefix}${getString("command.kick.success")}")
            NoCheatPlus.instance.getNCPLogger().info(getString("command.kick.message", target, sender.name))
        } else {
            sender.sendMessage("${ConfigData.logging_prefix}${getString("command.general.playerOffline")}")
        }
        return true
    }
}