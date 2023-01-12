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

import cn.nukkit.Player
import cn.nukkit.utils.Config
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.components.registry.NCPComManager
import net.catrainbow.nocheatplus.components.registry.NCPComponent
import net.catrainbow.nocheatplus.logging.NCPLogger
import net.catrainbow.nocheatplus.players.PlayerData

/**
 * NoCheatPlus 开放API类
 *
 * @author Catrainbow
 */
interface NoCheatPlusAPI {

    fun getNCPProvider(): NoCheatPlus

    fun getComManager(): NCPComManager

    fun getAllComponents(): HashMap<String, NCPComponent>

    fun getAllPlayerData(): HashMap<String, PlayerData>
    fun addComponents(components: NCPComponent)

    fun hasPlayer(player: Player): Boolean

    fun hasPlayer(player: String): Boolean

    fun getPlayerProvider(player: Player): PlayerData

    fun getPlayerProvider(player: String): PlayerData

    fun getNCPComponent(comName: String): NCPComponent

    fun getNCPLogger(): NCPLogger

    fun getNCPConfig(): Config

    fun getNCPBanRecord(): Config

    fun isPlayerBan(player: Player): Boolean

    fun kickPlayer(player: Player, type: CheckType)

    fun banPlayer(player: Player, days: Int, hours: Int, minutes: Int)

    fun banPlayer(player: Player, days: Int, hours: Int)

    fun banPlayer(player: Player, days: Int)

    fun getNCPCheck(checkType: CheckType): Check

    fun getNCPCheck(checkType: String): Check

    fun getAllNCPCheck(): HashMap<String, Check>

    fun hasPermission(player: Player, command: String): Boolean

    fun setPlayerCheck(player: Player)

    fun setPlayerBypass(player: Player)

}