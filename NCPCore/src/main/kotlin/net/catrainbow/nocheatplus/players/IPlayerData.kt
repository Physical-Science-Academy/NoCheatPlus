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
package net.catrainbow.nocheatplus.players

import cn.nukkit.Player

/**
 * 玩家信息储存接口
 *
 * @author Catrainbow
 */
interface IPlayerData {

    /**
     * 获取玩家名字
     *
     * @return 玩家名字
     */
    fun getPlayerName(): String

    /**
     * 返回玩家对象
     *
     * @return 玩家对象
     */
    fun getPlayer(): Player

}