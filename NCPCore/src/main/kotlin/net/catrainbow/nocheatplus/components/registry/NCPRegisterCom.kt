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
 * NCP注册模块
 *
 * @author Catrainbow
 */
abstract class NCPRegisterCom {

    /**
     * 模块名字
     */
    private var name: String = "NULL"

    /**
     * 模块作者
     */
    private var author: String = "NULL"

    /**
     * 模块版本
     */
    private var version: String = "1.0.0"

    /**
     * 设置模块名字
     *
     * @param name 模块名字
     */
    @JvmName("setComponentName")
    fun setName(name: String) {
        this.name = name
    }

    /**
     * 设置模块作者
     *
     * @param name 作者名字
     */
    @JvmName("setComponentAuthor")
    fun setAuthor(name: String) {
        this.author = name
    }

    /**
     * 设置模块版本
     *
     * @param version 模块版本
     */
    @JvmName("serComponentVersion")
    fun setVersion(version: String) {
        this.version = version
    }

}