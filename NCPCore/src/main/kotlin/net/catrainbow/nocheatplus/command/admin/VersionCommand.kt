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
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.command.NCPSubCommand

class VersionCommand : NCPSubCommand("version") {
    override fun getDescription(): String {
        return "see the version of NCP"
    }

    override fun getAliases(): Array<String> {
        return arrayOf("version")
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val default = StringBuilder().append("§aNoCheatPlus§r version §a${NoCheatPlus.PLUGIN_VERSION}").append("\n")
            .append("§rDetect and fight the exploitation of various flaws/bugs in Minecraft Bedrock").append("\n")
            .append("§rGithub:§a https://github.com/Physical-Science-Academy/NoCheatPlus/").append("\n")
            .append("§rAuthors:§a Catrainbow§r, §aNoCheatPlus-Nukkit Team")
        sender.sendMessage(default.toString())
        return true
    }
}