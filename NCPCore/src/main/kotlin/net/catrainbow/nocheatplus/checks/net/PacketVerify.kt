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

import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.network.protocol.AnimatePacket
import cn.nukkit.network.protocol.LoginPacket
import cn.nukkit.network.protocol.MovePlayerPacket
import cn.nukkit.network.protocol.PlayerAuthInputPacket
import cn.nukkit.network.protocol.TextPacket
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil
import net.catrainbow.nocheatplus.compat.Bridge118
import net.catrainbow.nocheatplus.compat.nukkit.VersionBridge
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.round

/**
 * 数据包验证,防止崩服
 *
 * @author Catrainbow
 */
class PacketVerify {

    companion object {

        private val verifyQueue: ArrayList<String> = ArrayList()
        private val playerAnimatePacketMap: HashMap<String, Int> = HashMap()
        private val playerLastUpdatePacket: HashMap<String, Long> = HashMap()
        private val unknown_client_model = arrayOf(
            "EZ4H", "Horion", "Zephyer", "MoonLight"
        )

        fun verifyPacket(event: DataPacketReceiveEvent) {
            val player = event.player
            val packet = event.packet

            if (packet is LoginPacket) {
                //fix the crash of PM1E
                verifyQueue.add(player.name)
                //if (!event.isCancelled) playerLastUpdatePacket[player.name] = System.currentTimeMillis()
            } else if (packet is TextPacket) {
                //Unknown TextPacket
                if (packet.xboxUserId == "0") NoCheatPlus.instance.kickPlayer(player, CheckType.UNKNOWN_PACKET)
                else {
                    val data = NoCheatPlus.instance.getPlayerProvider(player).movingData
                    val now = System.currentTimeMillis()
                    val isLive = data.isLive() && data.isSafeSpawn()
                    val onGround = player.onGround
                    //send a TextPacket when the player is moving
                    if (packet.type == TextPacket.TYPE_CHAT && (data.getSpeed() > 0.2 || data.getMotionY() > 0.5 || now - data.getLastJump() < 10) && isLive && onGround) event.setCancelled()
                }
            } else if (packet is PlayerAuthInputPacket || packet is MovePlayerPacket) {
                if (!playerLastUpdatePacket.containsKey(player.name)) playerLastUpdatePacket[player.name] =
                    System.currentTimeMillis()
                if (System.currentTimeMillis() - playerLastUpdatePacket[player.name]!! > round(((1 / 20) * 1000L).toDouble())) {
                    playerLastUpdatePacket[player.name] = System.currentTimeMillis()
                    playerAnimatePacketMap[player.name] = 0
                }
                if (player.pitch > 90) NoCheatPlus.instance.kickPlayer(player, CheckType.UNKNOWN_PACKET)
            } else if (packet is AnimatePacket) {
                if (packet.action == AnimatePacket.Action.CRITICAL_HIT) {
                    if (!playerAnimatePacketMap.containsKey(player.name)) playerAnimatePacketMap[player.name] = 1
                    playerAnimatePacketMap[player.name] = playerAnimatePacketMap[player.name]!! + 1

                    //flood animate packets try to crash the server
                    if (playerAnimatePacketMap[player.name]!! >= 50) event.setCancelled()
                    NoCheatPlus.instance.kickPlayer(player, CheckType.UNKNOWN_PACKET)
                }
            }

        }

        fun popVerifyQueue(playerName: String) {
            val player = NoCheatPlus.instance.server.getPlayer(playerName)
            //Unknown LoginPacket
            verifyQueue.add(player.name)
            if (player.loginChainData.deviceOS == 1) {
                val model = player.loginChainData.deviceModel.split(" ")
                if (model.isNotEmpty()) if (model[0] != model[0].uppercase(Locale.getDefault())) {
                    NoCheatPlus.instance.kickPlayer(player, CheckType.UNKNOWN_PACKET)
                    return
                }
            }
            //EZ4H LoginPacket
            if (unknown_client_model.contains(player.loginChainData.deviceOS.toString()) || unknown_client_model.contains(
                    player.loginChainData.deviceModel
                )
            ) {
                NoCheatPlus.instance.kickPlayer(player, CheckType.UNKNOWN_PACKET)
                return
            }
            if (Bridge118.version_bridge == VersionBridge.PM1E) {
                //fix the crash of PM1E
                if (verifyQueue.contains(playerName)) {
                    if (NoCheatPlus.instance.hasPlayer(player)) for (item in player.inventory.contents.values) if (item.id == 358) player.inventory.remove(
                        item
                    )
                    verifyQueue.remove(playerName)
                }
            }
            //fix a disabler of NCP
            val height = LocUtil.getPlayerHeight(player)
            if (height >= 1) player.teleport(player.add(0.0, 0.3 - height, 0.0))
        }

    }

}