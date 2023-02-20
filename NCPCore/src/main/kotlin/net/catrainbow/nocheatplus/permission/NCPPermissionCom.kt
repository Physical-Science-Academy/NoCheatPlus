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
import net.catrainbow.nocheatplus.checks.CheckType
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
    private val bypassPermission: HashMap<String, ArrayList<String>> = HashMap()

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
        for (type in CheckType.values()) {
            if (NoCheatPlus.instance.getNCPConfig().exists("permission.bypass.${type.name}")) {
                bypassPermission[type.name] = this.getBypassPermissionList(type)
            }
        }
    }

    override fun onDisabled() {
        this.permissionAllow.clear()
        this.bypassPermission.clear()
    }
    fun hasPermission(player: Player, command: String): Boolean {
        if (!permissionAllow.containsKey(command)) return true
        for (permission in permissionAllow[command]!!) if (player.hasPermission(permission)) return true
        return false
    }

    fun canBypass(player: Player, type: CheckType): Boolean {
        if (!bypassPermission.containsKey(type.name)) return false
        for (permission in bypassPermission[type.name]!!) if (player.hasPermission(permission)) return true
        return false
    }

    fun createPermission(permission: String, type: CheckType) {
        val config = NoCheatPlus.instance.getNCPConfig()
        val path = "permission.bypass.${type.name}"
        val list = if (config.exists(path)) config.getStringList(path) else ArrayList<String>()
        if (list.contains(permission)) return
        list.add(permission)
        config.set(path, list)
        config.save(true)
    }

    fun removePermission(permission: String, type: CheckType) {
        val config = NoCheatPlus.instance.getNCPConfig()
        val path = "permission.bypass.${type.name}"
        if (config.exists(path)) {
            val list = config.getStringList(path)
            if (!list.contains(permission)) return
            list.remove(permission)
            config.set(path, list)
            config.save(true)
        }
    }

    private fun getPermissionList(): ArrayList<String> {
        return NoCheatPlus.instance.getNCPConfig().getStringList("permission.policy") as ArrayList<String>
    }

    private fun getBypassPermissionList(type: CheckType): ArrayList<String> {
        return NoCheatPlus.instance.getNCPConfig().getStringList("permission.bypass.${type.name}") as ArrayList<String>
    }

}