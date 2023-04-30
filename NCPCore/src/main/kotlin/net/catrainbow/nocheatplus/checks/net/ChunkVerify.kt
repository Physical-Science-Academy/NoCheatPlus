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
package net.catrainbow.nocheatplus.checks.net

import cn.nukkit.blockentity.BlockEntitySpawnable
import cn.nukkit.event.player.PlayerChunkRequestEvent
import cn.nukkit.level.format.anvil.Chunk
import cn.nukkit.nbt.NBTIO
import cn.nukkit.nbt.tag.CompoundTag
import cn.nukkit.utils.BinaryStream
import cn.nukkit.utils.ThreadCache
import net.catrainbow.nocheatplus.components.data.ConfigData
import java.io.IOException
import java.nio.ByteOrder
import java.util.ArrayList

/**
 * 区块检测
 *
 * @author Catrainbow
 */
class ChunkVerify {

    companion object {

        fun verifyEvent(event: PlayerChunkRequestEvent) {
            if (!ConfigData.protection_net_chunk) return
            val player = event.player
            val level = player.level
            event.setCancelled()

            //by FENGBerd https://github.com/fengberd/FHiddenMine-Nukkit
            val chunk = level.getChunk(event.chunkX, event.chunkZ, false) as Chunk
            var blockEntities = ByteArray(0)
            var scanHeight = ConfigData.protection_net_chunk_scan_height
            val filter = ConfigData.protection_net_chunk_filter
            val ores = ConfigData.protection_net_chunk_ores

            if (chunk.blockEntities.isNotEmpty()) {
                val tagList = ArrayList<CompoundTag>()

                for (blockEntity in chunk.blockEntities.values) {
                    if (blockEntity is BlockEntitySpawnable) {
                        tagList.add(blockEntity.spawnCompound)
                    }
                }

                try {
                    blockEntities = NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }

            }

            val extra = chunk.blockExtraDataArray
            val extraData: BinaryStream?
            if (extra.isNotEmpty()) {
                extraData = BinaryStream()
                extraData.putVarInt(extra.size)
                for ((key, value) in extra) {
                    extraData.putVarInt(key)
                    extraData.putLShort(value)
                }
            } else {
                extraData = null
            }

            val stream = ThreadCache.binaryStream.get()
            stream.reset()
            var count = 0
            val sections = chunk.sections
            for (i in sections.indices.reversed()) {
                if (!sections[i].isEmpty) {
                    count = i + 1
                    break
                }
            }
            if (ConfigData.protection_net_chunk_dynamic_scan && player.onGround) scanHeight = player.y
            stream.putByte(count.toByte())
            for (i in 0 until count) {
                val section = sections[i]
                var fillIndex = 0
                val blocks = ByteArray(4096)
                val data = ByteArray(2048)
                for (x in 0..15) {
                    for (z in 0..15) {
                        val index = x shl 7 or (z shl 3)
                        var y = 0
                        while (y < 16) {
                            var b1 = 0
                            var b2 = 0
                            var tmpId: Int
                            val x2 = chunk.x * 16
                            var y2 = section.y * 16
                            val z2 = chunk.z * 16
                            val bIndex = (x shl 8) + (z shl 4) + y
                            if (y2 < scanHeight && !filter.contains(
                                    level.getBlockIdAt(
                                        x2 + x + 1,
                                        y2 + y,
                                        z2 + z
                                    )
                                ) && !filter.contains(level.getBlockIdAt(x2 + x - 1, y2 + y, z2 + z)) &&
                                !filter.contains(level.getBlockIdAt(x2 + x, y2 + y + 1, z2 + z)) && !filter.contains(
                                    level.getBlockIdAt(x2 + x, y2 + y - 1, z2 + z)
                                ) &&
                                !filter.contains(level.getBlockIdAt(x2 + x, y2 + y, z2 + z + 1)) && !filter.contains(
                                    level.getBlockIdAt(x2 + x, y2 + y, z2 + z - 1)
                                )
                            ) {
                                tmpId = section.getBlockId(x, y, z)
                                if (tmpId == 1) {
                                    blocks[bIndex] = (ores[++fillIndex % ores.size]).toByte()
                                } else {
                                    blocks[bIndex] = tmpId.toByte()
                                    b1 = section.getBlockData(x, y, z)
                                }
                            } else {
                                blocks[bIndex] = section.getBlockId(x, y, z).toByte()
                                b1 = section.getBlockData(x, y, z)
                            }
                            ++y2
                            if (y2 < scanHeight && !filter.contains(
                                    level.getBlockIdAt(
                                        x2 + x + 1,
                                        y2 + y,
                                        z2 + z
                                    )
                                ) && !filter.contains(level.getBlockIdAt(x2 + x - 1, y2 + y, z2 + z)) &&
                                !filter.contains(level.getBlockIdAt(x2 + x, y2 + y + 1, z2 + z)) && !filter.contains(
                                    level.getBlockIdAt(x2 + x, y2 + y - 1, z2 + z)
                                ) &&
                                !filter.contains(level.getBlockIdAt(x2 + x, y2 + y, z2 + z + 1)) && !filter.contains(
                                    level.getBlockIdAt(x2 + x, y2 + y, z2 + z - 1)
                                )
                            ) {
                                tmpId = section.getBlockId(x, y, z)
                                if (tmpId == 1) {
                                    blocks[bIndex + 1] = (ores[++fillIndex % ores.size]).toByte()
                                } else {
                                    blocks[bIndex + 1] = tmpId.toByte()
                                    b2 = section.getBlockData(x, y + 1, z)
                                }
                            } else {
                                blocks[bIndex + 1] = section.getBlockId(x, y + 1, z).toByte()
                                b1 = section.getBlockData(x, y + 1, z)
                            }
                            data[index or (y shr 1)] = (b2 shl 4 or b1).toByte()
                            y += 2
                        }
                    }
                }
                stream.putByte(0.toByte())
                stream.put(blocks)
                stream.put(data)
            }
            for (height in chunk.heightMapArray) {
                stream.putByte(height)
            }
            stream.put(ByteArray(256))
            stream.put(chunk.biomeIdArray)
            stream.putByte(0.toByte())
            if (extraData != null) {
                stream.put(extraData.buffer)
            } else {
                stream.putVarInt(0)
            }
            stream.put(blockEntities)
            for (i in sections.indices.reversed()) {
                if (!sections[i].isEmpty) {
                    count = i + 1
                    break
                }
            }
            stream.putByte(count.toByte())
            player.sendChunk(chunk.x, chunk.z, count, stream.buffer)

        }

    }

}