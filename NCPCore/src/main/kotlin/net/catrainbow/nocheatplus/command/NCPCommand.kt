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
package net.catrainbow.nocheatplus.command

import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParameter
import cn.nukkit.utils.TextFormat
import net.catrainbow.nocheatplus.NoCheatPlus

/**
 * NCP命令
 *
 * @author Catrainbow
 */
class NCPCommand : Command("ncp") {

    companion object {
        val subCommands: ArrayList<NCPSubCommand> = ArrayList()
    }

    init {
        this.setDescription("NoCheatPlus Command")
        this.setCommandParameters(object : HashMap<String, Array<CommandParameter>>() {
            init {
                var index = 1
                for (subCommand in subCommands) {
                    val aliasesBuilder = StringBuilder()
                    aliasesBuilder.let { subCommand.getAliases().forEach { aliases -> it.append("$aliases/") } }
                    put(
                        "${index}arg", arrayOf(
                            CommandParameter(
                                "${subCommand.subCommandStr}($aliasesBuilder)", false, subCommand.getAliases()
                            )
                        )
                    )
                    index++
                }
            }
        })
        this.usage = "/ncp [args]"
    }

    override fun execute(sender: CommandSender?, label: String?, args: Array<out String>?): Boolean {
        for (subCommand in subCommands) {
            if ((args?.get(0) ?: "ncp") == subCommand.subCommandStr) {
                return execute(sender, label, args)
            }
        }
        val default = StringBuilder("${TextFormat.YELLOW}NoCheatPlus Build-${NoCheatPlus.PLUGIN_VERSION}")
        for (subCommand in subCommands) {
            val str2 = StringBuilder()
            for (aliases in subCommand.getAliases())
                str2.append("$aliases/")
            default.append("\n").append(str2.toString())
        }
        sender!!.sendMessage(default.toString())
        return true
    }

}