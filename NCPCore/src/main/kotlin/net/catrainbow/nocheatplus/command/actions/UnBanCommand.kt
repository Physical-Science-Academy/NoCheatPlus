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

class UnBanCommand : NCPSubCommand("unban") {
    override fun getDescription(): String {
        return "(player) Unban a player"
    }

    override fun getAliases(): Array<String> {
        return arrayOf("unban")
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage("${ConfigData.logging_prefix}Player is not banned!")
        }
        val playerName = args[2]
        if (NoCheatPlus.instance.getNCPBanRecord().exists(playerName)) {
            val config = NoCheatPlus.instance.getNCPBanRecord()
            config.remove(playerName)
            config.save(true)
            sender.sendMessage("${ConfigData.logging_prefix}Unban player successfully!")
        } else sender.sendMessage("${ConfigData.logging_prefix}Player $playerName is not banned!")
        return true
    }
}