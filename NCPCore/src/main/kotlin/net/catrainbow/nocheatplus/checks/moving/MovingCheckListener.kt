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
package net.catrainbow.nocheatplus.checks.moving

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.event.entity.EntityInventoryChangeEvent
import cn.nukkit.event.player.PlayerDeathEvent
import cn.nukkit.event.player.PlayerEatFoodEvent
import cn.nukkit.event.player.PlayerGameModeChangeEvent
import cn.nukkit.event.player.PlayerInteractEvent
import cn.nukkit.event.player.PlayerRespawnEvent
import cn.nukkit.event.player.PlayerTeleportEvent
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.item.Item
import cn.nukkit.network.protocol.EntityEventPacket
import cn.nukkit.network.protocol.InventoryTransactionPacket
import cn.nukkit.network.protocol.MovePlayerPacket
import cn.nukkit.network.protocol.PlayerActionPacket
import cn.nukkit.network.protocol.PlayerAuthInputPacket
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.CheckListener
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.player.CreativeFly
import net.catrainbow.nocheatplus.checks.moving.player.MorePackets
import net.catrainbow.nocheatplus.checks.moving.player.NoFall
import net.catrainbow.nocheatplus.checks.moving.player.Speed
import net.catrainbow.nocheatplus.checks.moving.player.SurvivalFly
import net.catrainbow.nocheatplus.checks.moving.player.vehicle.VehicleCheck
import net.catrainbow.nocheatplus.compat.Bridge118
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.dataPacket
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.respawn
import net.catrainbow.nocheatplus.compat.nukkit.EmptyFood
import net.catrainbow.nocheatplus.compat.nukkit.FoodData118
import net.catrainbow.nocheatplus.feature.wrapper.WrapperEatFoodPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPlaceBlockPacket

/**
 * 子监听器
 *
 * @author Catrainbow
 */
class MovingCheckListener : CheckListener(CheckType.MOVING) {

    override fun onTick(event: Event) {
        if (event is BlockPlaceEvent) {
            val player = event.player
            val packet = WrapperPlaceBlockPacket(player)
            packet.block = event.block
            packet.blockAgainst = event.blockAgainst
            packet.blockReplace = event.blockReplace
            packet.time = System.currentTimeMillis()
            val wrapper = WrapperPacketEvent()
            wrapper.player = player
            wrapper.packet = packet
            dataPacket(wrapper)
        } else if (event is DataPacketReceiveEvent) {
            //开启权威移动数据包模式
            if (event.packet is PlayerAuthInputPacket && !Bridge118.server_auth_mode) Bridge118.server_auth_mode = true
            val player = event.player
            when (event.packet) {
                is MovePlayerPacket, is PlayerAuthInputPacket -> {
                    if (!NoCheatPlus.instance.hasPlayer(player)) return
                    if (NoCheatPlus.instance.getPlayerProvider(player).movingData.getPacketTracker() == null) return
                    val tracker = NoCheatPlus.instance.getPlayerProvider(player).movingData.getPacketTracker()!!
                    tracker.onPacketReceive(event.packet)
                }
                is PlayerActionPacket -> {
                    when ((event.packet as PlayerActionPacket).action) {
                        PlayerActionPacket.ACTION_START_SWIMMING, PlayerActionPacket.ACTION_STOP_SWIMMING -> {
                            NoCheatPlus.instance.getPlayerProvider(player).movingData.loseSwim()
                        }
                        PlayerActionPacket.ACTION_START_GLIDE, PlayerActionPacket.ACTION_STOP_GLIDE -> {
                            NoCheatPlus.instance.getPlayerProvider(player).movingData.loseGlide()
                        }
                    }
                }
                is InventoryTransactionPacket -> {
                    if (FoodData118.isFood(player.inventory.itemInHand.id)) {
                        val data = NoCheatPlus.instance.getPlayerProvider(player).movingData
                        if (data.getFoodTracker() != null) {
                            if (!data.getFoodTracker()!!.isLive()) data.getFoodTracker()!!.run()
                            data.getFoodTracker()!!.onAction()
                            if (!data.getFoodTracker()!!.isDuringEating() && !data.isEatFood()) {
                                val foodEvent = WrapperPacketEvent()
                                foodEvent.player = event.player
                                val packet = WrapperEatFoodPacket(foodEvent.player)
                                //empty food?
                                packet.food = EmptyFood()
                                packet.eat = false
                                foodEvent.packet = packet
                                dataPacket(foodEvent)
                            }
                        }
                    }
                }
                is EntityEventPacket -> {
                    if ((event.packet as EntityEventPacket).event == EntityEventPacket.EATING_ITEM || FoodData118.isFood(
                            player.inventory.itemInHand.id
                        )
                    ) {
                        val data = NoCheatPlus.instance.getPlayerProvider(player).movingData
                        if (data.getFoodTracker() != null) {
                            data.getFoodTracker()!!.addPacket()
                        }
                    }
                }
            }
        } else if (event is PlayerRespawnEvent) {
            val player = event.player
            player.respawn()
            if (NoCheatPlus.instance.hasPlayer(player)) NoCheatPlus.instance.getPlayerProvider(player).movingData.setLive(
                true
            )
        } else if (event is PlayerDeathEvent) {
            val player = event.entity
            if (NoCheatPlus.instance.hasPlayer(player)) NoCheatPlus.instance.getPlayerProvider(player).movingData.setLive(
                false
            )
        } else if (event is EntityDamageEvent) {
            if (event.entity is Player) {
                val player = event.entity as Player
                //虚空行走跳过检测
                if (NoCheatPlus.instance.hasPlayer(player)) {
                    if (event.cause == EntityDamageEvent.DamageCause.VOID) NoCheatPlus.instance.getPlayerProvider(
                        player
                    ).movingData.setVoidHurt(
                        true
                    )

                    if (event.cause == EntityDamageEvent.DamageCause.FALL) NoCheatPlus.instance.getPlayerProvider(player).movingData.setFallHurt(
                        true
                    )
                    //重新计算冷却
                    NoCheatPlus.instance.getPlayerProvider(player).movingData.resetKnockBackTick()
                }
            }
        } else if (event is PlayerGameModeChangeEvent) {
            val player = event.player
            if (NoCheatPlus.instance.hasPlayer(player)) NoCheatPlus.instance.getPlayerProvider(player).movingData.setLastNormalGround(
                player.location
            )
        } else if (event is EntityInventoryChangeEvent) {
            if (event.entity is Player) {
                val player = event.entity as Player
                //额外速度计算
                if (event.oldItem.id == Item.FIREWORKS) NoCheatPlus.instance.getPlayerProvider(player).movingData.onGlideBooster()
            }
        } else if (event is PlayerEatFoodEvent) {
            val foodEvent = WrapperPacketEvent()
            foodEvent.player = event.player
            val packet = WrapperEatFoodPacket(foodEvent.player)
            packet.food = event.food
            packet.eat = true
            foodEvent.packet = packet
            dataPacket(foodEvent)
        } else if (event is PlayerInteractEvent) {
            val player = event.player
            if (FoodData118.isFood(player.inventory.itemInHand.id)) if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) NoCheatPlus.instance.getPlayerProvider(
                player
            ).movingData.consumeFoodInteract()
        } else if (event is PlayerTeleportEvent) {
            val data = NoCheatPlus.instance.getPlayerProvider(event.player)
            data.movingData.setTeleport()
        }
    }

    init {
        this.addCheck(SurvivalFly())
        this.addCheck(MorePackets())
        this.addCheck(CreativeFly())
        this.addCheck(NoFall())
        this.addCheck(VehicleCheck())
        this.addCheck(Speed())
    }

}