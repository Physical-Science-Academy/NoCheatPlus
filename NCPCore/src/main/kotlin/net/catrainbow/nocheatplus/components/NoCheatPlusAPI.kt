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

package net.catrainbow.nocheatplus.components

import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.components.registry.NCPComManager
import net.catrainbow.nocheatplus.components.registry.NCPComponent

/**
 * NoCheatPlus 开放API类
 *
 * @author Catrainbow
 */
interface NoCheatPlusAPI {

    fun getNCPProvider(): NoCheatPlus

    fun getComManager(): NCPComManager

    fun getAllComponents(): HashMap<String, NCPComponent>

}