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

class ToggleCommand : NCPSubCommand("toggle") {
    override fun getDescription(): String {
        return getString("command.toggle.description")
    }

    override fun getAliases(): Array<String> {
        return arrayOf("toggle")
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.size == 2) {
            val checkName = args[1]
            if (NoCheatPlus.instance.getComManager().getChecks().containsKey(checkName)) {
                val baseName = NoCheatPlus.instance.getComManager().getChecks()[checkName]!!.baseName
                if (NoCheatPlus.instance.getComManager().isUsedChecks(baseName)) {
                    NoCheatPlus.instance.getComManager().setChecksUsed(baseName, false)
                    sender.sendMessage(getString("command.toggle.off"))
                } else {
                    NoCheatPlus.instance.getComManager().setChecksUsed(baseName, true)
                    sender.sendMessage(getString("command.toggle.on"))
                }
            } else sender.sendMessage(getString("command.toggle.unknownCheck"))
        } else sender.sendMessage(getString("command.general.unknownUsage"))

        return true
    }
}