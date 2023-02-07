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
package net.catrainbow.nocheatplus.feature

import cn.nukkit.event.Event
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.event.player.PlayerCommandPreprocessEvent
import cn.nukkit.event.player.PlayerDeathEvent
import cn.nukkit.event.player.PlayerEatFoodEvent
import cn.nukkit.event.player.PlayerGameModeChangeEvent
import cn.nukkit.event.player.PlayerInteractEvent
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerJumpEvent
import cn.nukkit.event.player.PlayerMoveEvent
import cn.nukkit.event.player.PlayerQuitEvent
import cn.nukkit.event.player.PlayerRespawnEvent
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.network.protocol.DisconnectPacket
import cn.nukkit.plugin.Plugin
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.CheckListener
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.net.PacketVerify
import net.catrainbow.nocheatplus.compat.BridgeWaterDog
import net.catrainbow.nocheatplus.components.config.NCPBanConfig
import net.catrainbow.nocheatplus.feature.chat.ChatTickListener
import net.catrainbow.nocheatplus.feature.moving.MovingDataListener
import net.catrainbow.nocheatplus.feature.moving.MovingListener
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.players.PlayerData

/**
 * 注册初始监听器
 *
 * @author Catrainbow
 */
class NCPListener : Listener {

    companion object {
        val listeners: ArrayList<ITickListener> = ArrayList()

        @JvmStatic
        fun <T : Event> registerEvent(
            listener: Listener,
            plugin: Plugin,
            clazz: Class<T>,
            executor: (T) -> Unit,
            ignoreCancelled: Boolean = false,
            priority: EventPriority = EventPriority.NORMAL,
        ) {
            try {
                plugin.server.pluginManager.registerEvent(
                    clazz,
                    listener,
                    priority,
                    { _: Listener?, event: Event? -> executor(event as T) },
                    plugin,
                    ignoreCancelled
                )
            } catch (ignore: Exception) {
                //忽略Kotlin事件转换错误
                //可能是KotlinLib的问题
            }
        }

    }

    init {
        registerEvent(
            this, NoCheatPlus.instance, PlayerJoinEvent::class.java, { playerJoins(it) }, true, EventPriority.HIGHEST
        )
        registerEvent(
            this, NoCheatPlus.instance, PlayerQuitEvent::class.java, { playerLeave(it) }, true, EventPriority.HIGHEST
        )
        registerEvent(
            this,
            NoCheatPlus.instance,
            WrapperPacketEvent::class.java,
            { playerInputs(it) },
            true,
            EventPriority.HIGHEST
        )
        registerEvent(
            this, NoCheatPlus.instance, PlayerMoveEvent::class.java, { playerMoves(it) }, true, EventPriority.HIGHEST
        )
        registerEvent(
            this,
            NoCheatPlus.instance,
            PlayerCommandPreprocessEvent::class.java,
            { playerOnCommands(it) },
            true,
            EventPriority.HIGHEST
        )
        registerEvent(
            this, NoCheatPlus.instance, BlockPlaceEvent::class.java, { playerPlaces(it) }, true, EventPriority.HIGHEST
        )
        registerEvent(
            this, NoCheatPlus.instance, PlayerJumpEvent::class.java, { playerJumps(it) }, true, EventPriority.HIGHEST
        )
        registerEvent(
            this,
            NoCheatPlus.instance,
            DataPacketReceiveEvent::class.java,
            { playerPacketReceive(it) },
            true,
            EventPriority.HIGHEST
        )
        registerEvent(
            this,
            NoCheatPlus.instance,
            PlayerRespawnEvent::class.java,
            { playerSpawns(it) },
            true,
            EventPriority.HIGHEST
        )
        registerEvent(
            this, NoCheatPlus.instance, PlayerDeathEvent::class.java, { playerDies(it) }, true, EventPriority.HIGHEST
        )
        registerEvent(
            this,
            NoCheatPlus.instance,
            EntityDamageEvent::class.java,
            { playerDamages(it) },
            true,
            EventPriority.HIGHEST
        )
        registerEvent(
            this,
            NoCheatPlus.instance,
            PlayerGameModeChangeEvent::class.java,
            { playerChangesGameMode(it) },
            true,
            EventPriority.HIGHEST
        )
        registerEvent(
            this,
            NoCheatPlus.instance,
            PlayerEatFoodEvent::class.java,
            { playerEatsFood(it) },
            true,
            EventPriority.HIGHEST
        )
        registerEvent(this, NoCheatPlus.instance, PlayerInteractEvent::class.java, {
            playerInteracts(it)
        }, true, EventPriority.HIGHEST)
        registerTickListener()
    }

    private fun registerTickListener() {
        listeners.add(MovingListener())
        listeners.add(MovingDataListener())
        listeners.add(ChatTickListener())
        listeners.add(CheckListener(CheckType.ALL).prepareDefault())
    }

    private fun checkEvent(listener: ITickListener, event: Event) {
        listener.onTick(event)
    }

    @EventHandler
    private fun playerJoins(event: PlayerJoinEvent) {
        //to prevent the server from crashing
        PacketVerify.popVerifyQueue(event.player.name)

        val data = PlayerData(event.player)
        PlayerData.allPlayersData[data.getPlayerName()] = data
        if (NoCheatPlus.instance.isPlayerBan(event.player)) {
            val disconnectPacket = DisconnectPacket()
            disconnectPacket.hideDisconnectionScreen = false
            disconnectPacket.message =
                (NoCheatPlus.instance.getNCPComponent("NCP AutoBan") as NCPBanConfig).formatMessage(event.player)
            event.player.dataPacket(disconnectPacket)
        }

        //水狗模式,从WaterDog返回真实的延迟
        if (event.player.address == "127.0.0.1" && !BridgeWaterDog.waterDog_ping) BridgeWaterDog.waterDog_ping = true
    }

    @EventHandler
    private fun playerInputs(event: WrapperPacketEvent) {
        for (listener in listeners) checkEvent(listener, event)
    }

    @EventHandler
    private fun playerMoves(event: PlayerMoveEvent) {
        for (listener in listeners) checkEvent(listener, event)
    }

    @EventHandler
    private fun playerLeave(event: PlayerQuitEvent) {
        val name = event.player.name
        PlayerData.allPlayersData.remove(name)
    }

    @EventHandler
    private fun playerJumps(event: PlayerJumpEvent) {
        for (listener in listeners) checkEvent(listener, event)
    }

    @EventHandler
    private fun playerPlaces(event: BlockPlaceEvent) {
        for (listener in listeners) checkEvent(listener, event)
    }

    @EventHandler
    private fun playerPacketReceive(event: DataPacketReceiveEvent) {
        //验证数据包,防止服务器受损
        PacketVerify.verifyPacket(event)

        for (listener in listeners) checkEvent(listener, event)
    }

    @EventHandler
    private fun playerSpawns(event: PlayerRespawnEvent) {
        for (listener in listeners) checkEvent(listener, event)
    }

    @EventHandler
    private fun playerDies(event: PlayerDeathEvent) {
        for (listener in listeners) checkEvent(listener, event)
    }

    @EventHandler
    private fun playerDamages(event: EntityDamageEvent) {
        for (listener in listeners) checkEvent(listener, event)
    }

    @EventHandler
    private fun playerChangesGameMode(event: PlayerGameModeChangeEvent) {
        for (listener in listeners) checkEvent(listener, event)
    }

    @EventHandler
    private fun playerOnCommands(event: PlayerCommandPreprocessEvent) {
        for (listener in listeners) checkEvent(listener, event)
    }

    @EventHandler
    private fun playerEatsFood(event: PlayerEatFoodEvent) {
        for (listener in listeners) checkEvent(listener, event)
    }

    @EventHandler
    private fun playerInteracts(event: PlayerInteractEvent) {
        for (listener in listeners) checkEvent(listener, event)
    }

}