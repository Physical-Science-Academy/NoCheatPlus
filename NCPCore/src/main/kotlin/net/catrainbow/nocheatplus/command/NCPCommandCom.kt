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

import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.command.actions.BanCommand
import net.catrainbow.nocheatplus.command.actions.KickCommand
import net.catrainbow.nocheatplus.command.actions.UnBanCommand
import net.catrainbow.nocheatplus.command.admin.DebugCommand
import net.catrainbow.nocheatplus.command.admin.ReloadCommand
import net.catrainbow.nocheatplus.command.admin.VersionCommand
import net.catrainbow.nocheatplus.components.registry.INCPComponent
import net.catrainbow.nocheatplus.components.registry.NCPComponent

class NCPCommandCom : NCPComponent(), INCPComponent {

    override fun onEnabled() {
        this.getRegisterCom().setName("NCP Command")
        this.getRegisterCom().setAuthor("Catrainbow")
        this.getRegisterCom().setVersion("1.0.0")
        NoCheatPlus.instance.server.commandMap.register("ncp", NCPCommand())
        NCPCommand.subCommands.add(ReloadCommand())
        NCPCommand.subCommands.add(KickCommand())
        NCPCommand.subCommands.add(VersionCommand())
        NCPCommand.subCommands.add(DebugCommand())
        NCPCommand.subCommands.add(BanCommand())
        NCPCommand.subCommands.add(UnBanCommand())
    }

}