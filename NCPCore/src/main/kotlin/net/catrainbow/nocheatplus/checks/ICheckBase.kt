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

import net.catrainbow.nocheatplus.components.registry.INCPComponent
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent

/**
 * 检测项目接口
 *
 * @author Catrainbow
 */
interface ICheckBase : INCPComponent {

    val baseName: String
    val typeName: CheckType

    fun onCheck(event: WrapperPacketEvent)

}