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
package net.catrainbow.nocheatplus.command.admin

import cn.nukkit.command.CommandSender
import net.catrainbow.nocheatplus.command.NCPSubCommand
import net.catrainbow.nocheatplus.components.data.ConfigData

class DebugCommand : NCPSubCommand("debug") {
    override fun getDescription(): String {
        return "toggle NCP debug mode currently"
    }

    override fun getAliases(): Array<String> {
        return arrayOf("debug")
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (ConfigData.logging_debug) {
            sender.sendMessage("${ConfigData.logging_prefix}Turn off the debug mode")
            ConfigData.logging_debug = false
        } else {
            sender.sendMessage("${ConfigData.logging_prefix}Turn on the debug mode")
            ConfigData.logging_debug = true
        }
        return true
    }
}