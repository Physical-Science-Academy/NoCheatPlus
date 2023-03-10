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

/**
 * NCP 模块
 *
 * @author Catrainbow
 */
open class NCPComponent : INCPComponent {

    private val ncpRegisterCom: NCPRegisterCom = NCPRegisterCom()
    override fun onEnabled() {
    }

    override fun onDisabled() {
    }

    /**
     * 返回模块信息
     *
     * @return 模块信息
     */
    fun getRegisterCom(): NCPRegisterCom {
        return this.ncpRegisterCom
    }


}