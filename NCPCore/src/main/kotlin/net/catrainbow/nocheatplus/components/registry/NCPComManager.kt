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
package net.catrainbow.nocheatplus.components.registry

import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.actions.ActionCom
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.command.NCPCommandCom
import net.catrainbow.nocheatplus.components.config.NCPBanConfig
import net.catrainbow.nocheatplus.components.config.NCPConfigCom
import net.catrainbow.nocheatplus.components.task.NCPTaskCom
import net.catrainbow.nocheatplus.logging.NCPLoggerCom
import net.catrainbow.nocheatplus.permission.NCPPermissionCom

/**
 * NCP模块管理器
 *
 * @author Catrainbow
 */
class NCPComManager {

    private val components: HashMap<String, NCPComponent> = HashMap()
    private val checks: HashMap<String, Check> = HashMap()

    /**
     * 注册模块
     *
     * @param component 模块
     */
    fun registerCom(component: NCPComponent) {
        component.onEnabled()
        NoCheatPlus.instance.logger.info(
            "Loading Module: ${
                component.getRegisterCom().getName()
            } ${component.getRegisterCom().getVersion()}"
        )
        this.components[component.getRegisterCom().getName()] = component
        if (component is Check) this.checks[component.getRegisterCom().getName()] = component
    }

    /**
     * 启用时注册默认模块
     */
    fun onEnabled() {
        this.registerCom(NCPConfigCom())
        this.registerCom(NCPBanConfig())
        this.registerCom(NCPLoggerCom())
        this.registerCom(NCPTaskCom())
        this.registerCom(NCPCommandCom())
        this.registerCom(ActionCom())
        this.registerCom(NCPPermissionCom())
        NoCheatPlus.instance.getNCPLogger().info("NoCheatPlus 已启用!")
    }

    fun onDisabled() {
        for (com in this.components.values) {
            com.onDisabled()
        }
    }

    fun getComponents(): HashMap<String, NCPComponent> {
        return this.components
    }

    fun getChecks(): HashMap<String, Check> {
        return this.checks
    }

}