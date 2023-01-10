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
package net.catrainbow.nocheatplus.permission

import cn.nukkit.Player
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.components.registry.INCPComponent
import net.catrainbow.nocheatplus.components.registry.NCPComponent

/**
 * Permission
 *
 * @author Catrainbow
 */
class NCPPermissionCom : NCPComponent(), INCPComponent {

    // Permission map
    private val permissionAllow: HashMap<String, ArrayList<String>> = HashMap()

    override fun onEnabled() {
        this.getRegisterCom().setName("NCP Permission")
        this.getRegisterCom().setVersion("1.0.0")
        this.getRegisterCom().setAuthor("Catrainbow")
        for (permissionStr in this.getPermissionList()) {
            val permission = permissionStr.split(":")[0]
            val commands = permissionStr.split(":")[1].split(",")
            for (command in commands) {
                if (!permissionAllow.containsKey(command)) permissionAllow[command] = ArrayList()
                permissionAllow[command]!!.add(permission)
            }
        }
    }

    fun hasPermission(player: Player, command: String): Boolean {
        if (!permissionAllow.containsKey(command)) return true
        for (permission in permissionAllow[command]!!) if (player.hasPermission(permission)) return true
        return false
    }

    private fun getPermissionList(): ArrayList<String> {
        return NoCheatPlus.instance.getNCPConfig().getStringList("permission.policy") as ArrayList<String>
    }

}