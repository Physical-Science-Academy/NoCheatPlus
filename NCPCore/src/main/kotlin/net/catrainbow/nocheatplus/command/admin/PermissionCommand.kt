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
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.command.NCPSubCommand
import net.catrainbow.nocheatplus.utilities.i18n.I18N.Companion.getString

class PermissionCommand : NCPSubCommand("permission") {
    override fun getDescription(): String {
        return getString("command.permission.description")
    }

    override fun getAliases(): Array<String> {
        return arrayOf("permission", "per")
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {

        if (args.size == 4) {
            val typeName = args[2]
            if (CheckType.getTypeByName(typeName) == CheckType.ALL) {
                sender.sendMessage(getString("command.permission.unknownType"))
                return true
            }
            val permission = args[3]
            when (args[1]) {
                "c", "create", "cre", "add", "input", "a" -> {
                    sender.sendMessage(getString("command.permission.create", typeName, permission))
                    NoCheatPlus.instance.createBypassPermission(permission, CheckType.getTypeByName(typeName))
                    return true
                }
                "d", "del", "remove", "re", "r" -> {
                    sender.sendMessage(getString("command.permission.remove", typeName, permission))
                    NoCheatPlus.instance.removeBypassPermission(permission, CheckType.getTypeByName(typeName))
                    return true
                }
                else -> {
                    sender.sendMessage(getString("command.permission.usage"))
                    return true
                }
            }
        } else sender.sendMessage(getString("command.general.unknownUsage"))

        return true
    }
}