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
package net.catrainbow.nocheatplus.checks.access

import cn.nukkit.Player

/**
 * 离线玩家数据记录
 *
 * @author Catrainbow
 */
class ACheckData {

    companion object {
        private val offlineAccess: HashMap<String, Int> = HashMap()

        fun plus(player: Player) {
            plus(player.name)
        }

        fun plus(player: String) {
            if (!offlineAccess.containsKey(player)) offlineAccess[player] = 0
            offlineAccess[player] = offlineAccess[player]!! + 1
        }

        fun getBufferCount(player: Player): Int {
            return this.getBufferCount(player.name)
        }

        fun clear(player: Player) {
            clear(player.name)
        }

        private fun clear(player: String) {
            offlineAccess[player] = 0
        }

        private fun getBufferCount(player: String): Int {
            if (!offlineAccess.containsKey(player)) offlineAccess[player] = 0
            return offlineAccess[player]!!
        }

    }

}