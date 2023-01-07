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
import net.catrainbow.nocheatplus.command.NCPCommandCom
import net.catrainbow.nocheatplus.components.config.NCPConfigCom
import net.catrainbow.nocheatplus.components.task.NCPTaskCom
import net.catrainbow.nocheatplus.logging.NCPLoggerCom

/**
 * NCP模块管理器
 *
 * @author Catrainbow
 */
class NCPComManager {

    private val components: HashMap<String, NCPComponent> = HashMap()

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
        NoCheatPlus.instance.getNCPLogger().info("加载模块 " + component.getRegisterCom().getName() + " 成功")
    }

    /**
     * 启用时注册默认模块
     */
    fun onEnabled() {
        NoCheatPlus.instance.getNCPLogger().info("NoCheatPlus 已启用!")
        this.registerCom(NCPConfigCom())
        this.registerCom(NCPTaskCom())
        this.registerCom(NCPCommandCom())
        this.registerCom(NCPLoggerCom())
    }

    fun onDisabled() {
        for (com in this.components.values) {
            com.onDisabled()
        }
    }

    fun getComponents(): HashMap<String, NCPComponent> {
        return this.components
    }

}