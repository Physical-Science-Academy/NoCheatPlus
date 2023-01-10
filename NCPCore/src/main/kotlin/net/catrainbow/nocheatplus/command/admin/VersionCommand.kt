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
import net.catrainbow.nocheatplus.utilities.i18n.I18N.Companion.getString

class VersionCommand : NCPSubCommand("version") {
    override fun getDescription(): String {
        return getString("command.version.description")
    }

    override fun getAliases(): Array<String> {
        return arrayOf("version")
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        val default = StringBuilder().append(getString("command.version.ncpversion", NoCheatPlus.PLUGIN_VERSION)).append("\n")
            .append(getString("command.version.pluginDescription")).append("\n")
            .append(getString("command.version.githubLink")).append("\n")
            .append(getString("command.version.authors"))
        sender.sendMessage(default.toString())
        return true
    }
}