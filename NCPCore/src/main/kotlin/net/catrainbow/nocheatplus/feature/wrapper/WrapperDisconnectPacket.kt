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
package net.catrainbow.nocheatplus.feature.wrapper

import cn.nukkit.Player
import net.catrainbow.nocheatplus.actions.ActionType
import net.catrainbow.nocheatplus.checks.CheckType

class WrapperDisconnectPacket(player: Player) : WrapperPacket(player) {

    private var cancelled = false
    var type: ActionType = ActionType.DEFAULT
    var reason: CheckType = CheckType.ALL

    fun setCancelled() {
        this.cancelled = true
    }

    fun isCancelled(): Boolean {
        return this.cancelled
    }
}