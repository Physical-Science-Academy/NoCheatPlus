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
package net.catrainbow.nocheatplus.checks.moving.location

import cn.nukkit.Player
import cn.nukkit.block.Block
import cn.nukkit.level.Location

/**
 * Location Util
 *
 * @author Catrainbow
 */
class LocUtil {

    companion object {

        /**
         * 获得玩家脚下的方块
         *
         * @return Block
         */
        @JvmStatic
        fun getUnderBlock(player: Player): Block {
            var b1 = player.level.getBlock(Location(player.x, player.y - 1, player.z, player.level))
            val list: ArrayList<Block> = ArrayList()
            list.add(b1)
            list.add(player.level.getBlock(Location(player.x + 1, player.y - 1, player.z, player.level)))
            list.add(player.level.getBlock(Location(player.x - 1, player.y - 1, player.z, player.level)))
            list.add(player.level.getBlock(Location(player.x, player.y - 1, player.z + 1, player.level)))
            list.add(player.level.getBlock(Location(player.x, player.y - 1, player.z - 1, player.level)))
            list.add(player.level.getBlock(Location(player.x + 1, player.y - 1, player.z + 1, player.level)))
            list.add(player.level.getBlock(Location(player.x + 1, player.y - 1, player.z - 1, player.level)))
            list.add(player.level.getBlock(Location(player.x - 1, player.y - 1, player.z + 1, player.level)))
            list.add(player.level.getBlock(Location(player.x - 1, player.y - 1, player.z - 1, player.level)))
            var minBlock: Block = b1
            for (block in list) {
                val minX = block.floorX - 0.35
                val maxX = block.floorX + 0.35
                val minZ = block.floorZ - 0.35
                val maxZ = block.floorZ + 0.35
                val bb: Cuboid = Cuboid(minX, maxX, block.y, block.y, minZ, maxZ)
                if (bb.isVectorInside(player)) {
                    minBlock = block
                    break
                }
            }
            return minBlock
        }

    }

}