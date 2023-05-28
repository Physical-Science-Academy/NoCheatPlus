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
import net.catrainbow.nocheatplus.utilities.i18n.I18N.Companion.getString

class WarnCommand : NCPSubCommand("warn") {
    override fun getDescription(): String {
        return getString("command.warn.description")
    }

    override fun getAliases(): Array<String> {
        return arrayOf("warn")
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage(getString(""))
            return true
        }
        val target = args[1]
        var online = false
        for (player in NoCheatPlus.instance.server.onlinePlayers.values) {
            if (player.name == target) online = true
        }
        if (online) {
            val messageBuilder = StringBuilder()
            var index = 0
            for (warning in args) {
                index++
                if (index >= 2) messageBuilder.append(warning).append(" ")
            }
            val message = if (messageBuilder.toString().last() == ' ') messageBuilder.substring(
                0, messageBuilder.length - 1
            ) else messageBuilder.toString()

            NoCheatPlus.instance.server.getPlayer(target).sendMessage(
                message.replace("@player", target).replace("@next", "\n").replace("&n", "\n")
            )
        }
        sender.sendMessage(getString("command.warn.success"))
        return true
    }
}