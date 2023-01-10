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
package net.catrainbow.nocheatplus.checks

import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.components.registry.INCPComponent
import net.catrainbow.nocheatplus.components.registry.NCPComponent
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent

/**
 * Check
 * 所有检测的父类
 *
 * @author Catrainbow
 */
abstract class Check(override val baseName: String, override val typeName: CheckType) : ICheckBase, INCPComponent,
    NCPComponent() {

    /**
     * 入口方法
     *
     * 检测将从这里进行
     * @link WrapperPacketEvent
     */
    override fun onCheck(event: WrapperPacketEvent) {
    }

    override fun onEnabled() {
        this.getRegisterCom().setName(typeName.name)
        this.getRegisterCom().setVersion("1.0.0")
        this.getRegisterCom().setAuthor("Catrainbow")
    }

    override fun onDisabled() {
        NoCheatPlus.instance.getComManager().getChecks().remove(this.typeName.name)
    }

}