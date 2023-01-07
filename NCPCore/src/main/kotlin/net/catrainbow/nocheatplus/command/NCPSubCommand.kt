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

import cn.nukkit.command.CommandSender

abstract class NCPSubCommand(val subCommandStr: String) {

    abstract fun getDescription(): String

    abstract fun getAliases(): Array<String>

    abstract fun execute(sender: CommandSender, label: String, args: Array<out String>)

}