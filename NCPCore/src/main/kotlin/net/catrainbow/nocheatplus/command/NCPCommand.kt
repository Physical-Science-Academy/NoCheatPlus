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

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParameter
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.components.data.ConfigData

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
        if (args!!.isNotEmpty()) for (subCommand in subCommands) {
            if (args[0] == subCommand.subCommandStr) {
                val inject = if (sender is Player) NoCheatPlus.instance.hasPermission(
                    sender, subCommand.subCommandStr
                ) else true
                return if (inject) subCommand.execute(sender!!, label!!, args) else this.noPermission(sender!!)
            }
        }
        val default = StringBuilder("${ConfigData.logging_prefix}Administrative Commands Overview:")
        var index = 0
        for (subCommand in subCommands) {
            val inject =
                if (sender is Player) NoCheatPlus.instance.hasPermission(sender, subCommand.subCommandStr) else true
            if (inject) {
                index++
                val str2 = StringBuilder()
                for (aliases in subCommand.getAliases()) str2.append("/ncp $aliases: ${subCommand.getDescription()}")
                default.append("\n").append(ConfigData.logging_prefix).append(str2.toString())
            }
        }
        if (index == 0) default.clear().append("§aNoCheatPlus§r version §a${NoCheatPlus.PLUGIN_VERSION}")
            .append("\n").append("Detect and fight the exploitation of various flaws/bugs in Minecraft Bedrock")
            .append("\n").append("Github:§a https://github.com/Physical-Science-Academy/NoCheatPlus/")
            .append("\n").append("Authors:§a Catrainbow§r, §aNoCheatPlus-Nukkit Team")
        sender!!.sendMessage(default.toString())
        return true
    }

    private fun noPermission(player: CommandSender): Boolean {
        player.sendMessage(ConfigData.permission_no_permission)
        return true
    }

}