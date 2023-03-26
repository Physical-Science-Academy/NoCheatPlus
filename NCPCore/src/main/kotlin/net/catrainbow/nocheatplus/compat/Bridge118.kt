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
package net.catrainbow.nocheatplus.compat

import cn.nukkit.Nukkit
import cn.nukkit.Player
import cn.nukkit.block.Block
import cn.nukkit.block.BlockSlab
import cn.nukkit.block.BlockStairs
import cn.nukkit.level.Location
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.actions.ActionFactory
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil
import net.catrainbow.nocheatplus.compat.nukkit.VersionBridge
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.feature.wrapper.WrapperSetBackPacket

/**
 * 多核心适配和架桥
 *
 * @author Catrainbow
 */
class Bridge118 {

    companion object {

        //服务器是否拥有权威移动发包
        var server_auth_mode = false

        var version_bridge: VersionBridge = VersionBridge.VANILLA

        //验证核心
        fun verifyVersionBridge() {
            if (Nukkit.CODENAME == "PowerNukkitX") {
                version_bridge = VersionBridge.PNX
                return
            }
            version_bridge = try {
                val clazz = Class.forName("cn.nukkit.Nukkit")
                clazz.getField("NUKKIT_PM1E")
                if (NoCheatPlus.instance.server.properties.exists("server-authoritative-block-breaking")) {
                    if (NoCheatPlus.instance.server.getPropertyBoolean("server-authoritative-block-breaking")) VersionBridge.PM1E else VersionBridge.VANILLA
                } else VersionBridge.PM1E
            } catch (exception: Exception) {
                VersionBridge.VANILLA
            }
            if (version_bridge == VersionBridge.PM1E) server_auth_mode = true
        }

        //重写核心拉回算法
        fun Player.setback(location: Location, type: CheckType) {
            val packet = WrapperSetBackPacket(player)
            packet.checkType = type
            packet.player = player
            packet.target = location
            val sendEvent = WrapperPacketEvent()
            sendEvent.player = packet.player
            sendEvent.packet = packet
            dataPacket(sendEvent)
            if (!sendEvent.isInvalid()) {
                if (ActionFactory.actionDataMap.containsKey(type.name)) if (ActionFactory.actionDataMap[type.name]!!.enableCancel) if (NoCheatPlus.instance.hasPlayer(
                        player.name
                    )
                ) if (NoCheatPlus.instance.getPlayerProvider(player).getViolationData(type)
                        .getVL() >= ActionFactory.actionDataMap[type.name]!!.cancel
                ) player.teleport(location)
            }
        }

        //重写核心重生算法
        fun Player.respawn() {
            if (!NoCheatPlus.instance.hasPlayer(player)) return
            val provider = NoCheatPlus.instance.getPlayerProvider(player)
            //更新位置.防止死亡拉回
            provider.movingData.setLastNormalGround(location)
            //更新计时器
            provider.movingData.respawn()
        }

        //蜘蛛网判断
        fun Player.isInWeb(): Boolean {
            return player.levelBlock.id == Block.COBWEB || player.add(
                0.0, -0.25, 0.0
            ).levelBlock.id == Block.COBWEB || player.add(0.0, 1.0, 0.0).levelBlock.id == Block.COBWEB || player.add(
                0.0, 1.5, 0.0
            ).levelBlock.id == Block.COBWEB || player.add(0.0, 2.0, 0.0).levelBlock.id == Block.COBWEB
        }

        //重写核心梯子判断
        fun Player.onClimbedBlock(): Boolean {
            return player.levelBlock.canBeClimbed()
        }

        fun Player.onIce(): Boolean {
            return LocUtil.isIce(player.add(0.0, -1.0, 0.0).levelBlock) || LocUtil.isIce(
                player.add(
                    0.0, -2.0, 0.0
                ).levelBlock
            )
        }

        fun Player.onStair(): Boolean {
            return player.levelBlock is BlockStairs || player.add(
                0.0, -1.0, 0.0
            ).levelBlock is BlockStairs || player.levelBlock.add(0.0, -2.0, 0.0).levelBlock is BlockStairs
        }

        fun Player.onSlab(): Boolean {
            return player.levelBlock is BlockSlab || player.add(
                0.0, -1.0, 0.0
            ).levelBlock is BlockSlab || player.levelBlock.add(0.0, -2.0, 0.0).levelBlock is BlockSlab
        }

        //获取玩家的真实延迟
        fun Player.getRealPing(): Int {
            return this.ping
        }

        //重写位置方块判断
        fun Location.isInLiquid(): Boolean {
            return LocUtil.isLiquid(this.levelBlock)
        }

        fun Location.onGround(): Boolean {
            return !LocUtil.isLiquid(this.levelBlock) && this.levelBlock.id != Block.AIR && !this.levelBlock.canPassThrough()
        }

        fun dataPacket(packet: WrapperPacketEvent) {
            NoCheatPlus.instance.server.pluginManager.callEvent(packet)
        }

    }

}