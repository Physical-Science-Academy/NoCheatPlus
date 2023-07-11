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
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.isInLiquid

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
        fun getUnderBlocks(player: Player): List<Block> {

            val realBB = player.getBoundingBox().clone()
            realBB.maxY = realBB.minY + 0.1
            realBB.minY -= 0.4
            realBB.expand(0.2, 0.0, 0.2)
            val blocksList: ArrayList<Block> = ArrayList()
            val bb = realBB.clone()
            bb.minY -= 0.6
            if (blocksList.isEmpty()) {
                bb.forEach { x, y, z ->
                    blocksList.add(player.level.getBlock(x, y, z))
                }
            }

            return blocksList.toList()
        }

        fun Player.getGroundState(): Boolean {
            val realBB = player.getBoundingBox().clone()
            realBB.maxY = realBB.minY + 0.1
            realBB.minY -= 0.4
            realBB.expand(0.2, 0.0, 0.2)
            getUnderBlocks(player).forEach {
                if (!it.canPassThrough() && it.collidesWithBB(realBB)) {
                    return true
                }
            }
            return false
        }

        fun Player.isAboveBlock(block: Int): Boolean {
            getUnderBlocks(player).forEach {
                if (it.id == block) return true
            }
            return false
        }

        fun Player.isAboveIce(): Boolean {
            getUnderBlocks(player).forEach {
                if (isIce(it)) return true
            }
            return false
        }

        fun Player.isAboveSlab(): Boolean {
            getUnderBlocks(player).forEach {
                if (it is BlockSlab) return true
            }
            return false
        }

        fun Player.isAboveStairs(): Boolean {
            getUnderBlocks(player).forEach {
                if (it is BlockStairs) return true
            }
            return false
        }

        fun Player.isAboveLiquid(): Boolean {
            getUnderBlocks(player).forEach {
                if (it.location.isInLiquid()) return true
            }
            return false
        }

        fun isLiquid(block: Block): Boolean {
            return block.id == 9 || block.id == 11 || block.id == 8 || block.id == 10
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