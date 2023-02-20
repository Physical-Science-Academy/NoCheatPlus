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
import cn.nukkit.block.BlockSlab
import cn.nukkit.block.BlockStairs
import cn.nukkit.level.Location
import cn.nukkit.math.BlockFace

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
            val b1 = player.level.getBlock(Location(player.x, player.y - 1, player.z, player.level))
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
            //性能问题
            for (block in list) {
                val minX = block.floorX - 0.35
                val maxX = block.floorX + 0.35
                val minZ = block.floorZ - 0.35
                val maxZ = block.floorZ + 0.35
                val bb = Cuboid(minX, maxX, block.y, block.y, minZ, maxZ)
                if (bb.isVectorInside(player)) {
                    minBlock = block
                    break
                }
            }
            return minBlock
        }

        fun isLiquid(block: Block): Boolean {
            return block.id == 9 || block.id == 11
                    || block.id == 8 || block.id == 10
        }

        fun isWater(block: Block): Boolean {
            return block.id == 8 || block.id == 9
        }

        fun isIce(block: Block): Boolean {
            return block.id == Block.ICE || block.id == Block.ICE_FROSTED || block.id == Block.PACKED_ICE
        }

        fun isLava(block: Block): Boolean {
            return block.id == 10 || block.id == 1
        }

        fun getPlayerHeight(player: Player): Int {
            if (player.floorY <= 0) {
                return 0
            } else {
                for (y in 0 until 256) {
                    val level = player.getLevel()
                    val block = level.getBlock(player.floorX, (player.floorY - y), player.floorZ)
                    if (block.id != 0) {
                        return if ((player.floorY - block.floorY) - 1 < 0) {
                            0
                        } else (player.floorY - block.floorY) - 1
                    }
                }
            }
            return player.floorY
        }

    }

}