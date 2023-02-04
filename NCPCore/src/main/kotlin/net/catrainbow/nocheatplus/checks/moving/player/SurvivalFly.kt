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
package net.catrainbow.nocheatplus.checks.moving.player

import cn.nukkit.Player
import cn.nukkit.block.Block
import cn.nukkit.block.BlockSlab
import cn.nukkit.block.BlockStairs
import cn.nukkit.block.BlockThin
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.level.Location
import cn.nukkit.math.BlockFace
import cn.nukkit.potion.Effect
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.MovingData
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil
import net.catrainbow.nocheatplus.checks.moving.magic.GhostBlockChecker
import net.catrainbow.nocheatplus.checks.moving.magic.Magic
import net.catrainbow.nocheatplus.compat.Bridge118
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.setback
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPlaceBlockPacket
import net.catrainbow.nocheatplus.players.IPlayerData
import kotlin.math.*

/**
 * 检测玩家处于生存/冒险状态下,潜行/疾跑/游泳时的
 * 跳跃高度/跳跃距离/空中移动速度,etc.
 *
 * @author Catrainbow
 */
class SurvivalFly : Check("survival fly", CheckType.MOVING_SURVIVAL_FLY) {

    // Tags
    private val tags: ArrayList<String> = ArrayList()

    //bunny jump
    private var bunnyHop = 0

    override fun onCheck(event: WrapperPacketEvent) {
        val player = event.player
        if (player.gamemode == 1 || player.gamemode == 3) return
        val pData = NoCheatPlus.instance.getPlayerProvider(player)
        val data = pData.movingData
        if (!data.isSafeSpawn() || !data.isLive()) return
        if (ConfigData.check_survival_fly_set_back_void_to_void && data.isVoidHurt()) return
        val packet = event.packet
        if (packet is WrapperInputPacket) this.checkPlayerFly(
            player, packet.from, packet.to, data, pData, System.currentTimeMillis()
        ) else if (packet is WrapperPlaceBlockPacket) this.updateGhostBlock(packet, data)
    }

    /**
     * @param player
     * @param from
     * @param to
     * @param data
     * @param pData
     * @param now
     *
     * @return
     */
    private fun checkPlayerFly(
        player: Player,
        from: Location,
        to: Location,
        data: MovingData,
        pData: IPlayerData,
        now: Long,
    ) {

        this.tags.clear()
        this.bunnyHop = 0
        val debug = ConfigData.logging_debug

        val isSamePos = to.distance(from) == 0.0
        val xDistance = data.getMotionX()
        val yDistance = data.getMotionY()
        val zDistance = data.getMotionZ()

        val fromOnGround = from.add(0.0, -0.5, 0.0).levelBlock.id != 0 && LocUtil.getUnderBlock(player).id != 0
        val toOnGround = to.add(0.0, -0.5, 0.0).levelBlock.id != 0 && LocUtil.getUnderBlock(player).id != 0
        var sprinting = false

        //检测玩家疾跑状态改变时的运动情况
        if (data.getLoseSprintCount() > 0) {
            if (toOnGround && (fromOnGround || yDistance < Magic.WALK_SPEED)) {
                sprinting = data.getLoseSprintCount() < 3
                data.setLoseSprintCount(0)
                tags.add("invalidate_lose_sprint")
            } else {
                sprinting = true
                tags.add("lose_sprint")
                if (data.getLoseSprintCount() < 3 && toOnGround) data.setLoseSprintCount(0)
            }
        }

        if (sprinting) this.tags.add("sprint")
        this.setNextFriction(from, to, data)
        data.getGhostBlockChecker().run()

        //幽灵方块追踪器
        var lagGhostBlock = false
        if (data.getGhostBlockChecker().isLive()) {
            val vDistGhost = this.vDistGhostBlock(data.getGhostBlockChecker())
            if (vDistGhost[0] >= vDistGhost[1]) lagGhostBlock = true
        }

        if (data.isJump()) this.bunnyHop += round((now - data.getLastJump()) / 100.0).toInt()

        if (player.hasEffect(Effect.JUMP_BOOST)) this.tags.add("effect_jump")
        if (player.hasEffect(Effect.SPEED)) this.tags.add("effect_speed")

        if (player.foodData.level <= 6) this.tags.add("hunger")

        //处理台阶和板砖的特判问题
        val downB1 = player.add(0.0, -0.25, 0.0).levelBlock
        val downB2 = player.add(0.0, -1.0, 0.0).levelBlock
        if (downB1 is BlockStairs || downB1 is BlockSlab || downB2 is BlockStairs || downB2 is BlockSlab) this.tags.add(
            "stair_slab"
        )

        if (data.getLiquidTick() == 0) {
            val distAir = this.vDistAir(now, player, from, to, fromOnGround, toOnGround, yDistance, data, pData)
            if (distAir[0] > distAir[1]) pData.addViolationToBuffer(
                typeName, (distAir[0] - distAir[1]) * 10.0 + 1.1
            )
            if (!this.tags.contains("flying")) {
                val distFullAir =
                    this.inAirCheck(now, player, from, to, fromOnGround, toOnGround, yDistance, data, pData)
                if (distFullAir[0] > distFullAir[1]) pData.addViolationToBuffer(
                    typeName, (abs(distFullAir[0] - distFullAir[1]) * 10.0)
                )
            } else {
                //不规则的运动情况
                if (this.tags.contains("bunny_hop")) {
                    this.tags.add("air_jump")
                    player.setback(data.getLastNormalGround(), this.typeName)
                    pData.addViolationToBuffer(
                        typeName, (player.inAirTicks / 20 * 5.0)
                    )
                }
            }
            val speedChange =
                xDistance != data.getLastMotionX() || yDistance != data.getLastMotionY() || zDistance != data.getLastMotionZ()
            if (this.tags.contains("ground_walk") && (!isSamePos || speedChange) && this.getTinyHeight(
                    player, ArrayList()
                ) < Magic.BUNNY_TINY_JUMP_MAX / 3.0
            ) {
                val vDistVertical =
                    this.vDistVertical(now, player, from, to, fromOnGround, toOnGround, yDistance, data, pData)
                if (vDistVertical[0] > vDistVertical[1]) {
                    player.setback(data.getLastNormalGround(), this.typeName)
                    pData.addViolationToBuffer(typeName, vDistVertical[0] - vDistVertical[1])
                }
            } else {
                //解决空中攀升问题
                val hasMotion = this.tags.contains("ground_walk") && !this.tags.contains("same_at")
                if (hasMotion) {
                    val vLimitedH =
                        this.setAllowedHDist(now, player, from, to, fromOnGround, toOnGround, yDistance, data, pData)
                    if (vLimitedH[0] > vLimitedH[1]) {
                        player.setback(data.getLastNormalGround(), this.typeName)
                        pData.addViolationToBuffer(typeName, vLimitedH[0] - vLimitedH[1])
                    }
                }
            }
        }

        //滞空时长
        if (data.getFullAirTick() > 7) {
            data.setFullAirTick(0)
            pData.addViolationToBuffer(typeName, (data.getFullAirTick() * 1.3))
            pData.getViolationData(typeName).setLagBack(data.getLastNormalGround())
        }

        if (data.getPacketTracker() != null) {
            val tracker = data.getPacketTracker()!!
            val shortCount = tracker.getCount()
            if (shortCount != 0) {
                val maxCount = tracker.getMaxCount()
                val average = tracker.getAverage()
                if (maxCount > this.countMaxMovementPacket(data) || shortCount > this.countMaxMovementPacket(data)) {
                    //产生先允许后拉回的延迟效果,以减少误判
                    //此检测可能存在误判,待考证
                    if (average > (this.countMaxMovementPacket(data) + 2)) {
                        //重置计算,避免反复拉回
                        tracker.resetSum()
                        player.setback(data.getLastNormalGround(), this.typeName)
                        pData.addViolationToBuffer(
                            typeName, (max(shortCount, maxCount) - this.countMaxMovementPacket(data)) * 0.25
                        )
                    }
                }
            }
            if (!data.getPacketTracker()!!.isLive()) data.getPacketTracker()!!.run()
        }
        if (lagGhostBlock) pData.getViolationData(typeName).setCancel()

        if (debug) {
            val builder = StringBuilder("empty")
            for (tag in this.tags) builder.append(" ").append(tag)
            player.sendPopup(builder.toString())
        }

        if (!pData.getViolationData(typeName)
                .isCheat() && fromOnGround && toOnGround
        ) data.updateNormalLoc(player.location)
        pData.getViolationData(typeName).preVL(0.998)

    }

    /**
     * 空中滞留检测
     *
     * @param now
     * @param player
     * @param from
     * @param to
     * @param fromOnGround
     * @param toOnGround
     * @param yDistance
     * @param data
     * @param pData
     *
     * @return
     */
    private fun vDistAir(
        now: Long,
        player: Player,
        from: Location,
        to: Location,
        fromOnGround: Boolean,
        toOnGround: Boolean,
        yDistance: Double,
        data: MovingData,
        pData: IPlayerData,
    ): DoubleArray {
        val vanillaFall = this.onFallingVertical(
            data, hypot(abs(data.getMotionX()), abs(data.getMotionZ())) <= 0.3
        ) && player.inAirTicks > 15 && yDistance < 0.0
        if (vanillaFall) this.tags.add("fall")
        val flying = !vanillaFall && player.inAirTicks > 15 && yDistance >= 0.0
        var allowDistance = Double.MIN_VALUE
        var limitDistance = Double.MAX_VALUE
        if (!toOnGround && !fromOnGround && flying) this.tags.add("flying")
        val sprint = this.tags.contains("sprint")

        val direction = player.horizontalFacing
        val block = player.getSide(direction).levelBlock
        if (block.id != 0) this.tags.add("face_block")


        val downBlock = player.add(0.0, -1.0, 0.0).levelBlock
        val towardB = player.levelBlock.getSide(BlockFace.DOWN).getSide(player.direction)
        val backDirection = when (player.direction) {
            BlockFace.WEST -> BlockFace.EAST
            BlockFace.EAST -> BlockFace.WEST
            BlockFace.SOUTH -> BlockFace.NORTH
            BlockFace.NORTH -> BlockFace.SOUTH
            else -> {
                BlockFace.DOWN
            }
        }
        val leftDirection = when (player.direction) {
            BlockFace.NORTH -> BlockFace.WEST
            BlockFace.SOUTH -> BlockFace.EAST
            BlockFace.WEST -> BlockFace.SOUTH
            BlockFace.EAST -> BlockFace.NORTH
            else -> BlockFace.DOWN
        }
        val rightDirection = when (player.direction) {
            BlockFace.NORTH -> BlockFace.EAST
            BlockFace.EAST -> BlockFace.SOUTH
            BlockFace.SOUTH -> BlockFace.WEST
            BlockFace.WEST -> BlockFace.NORTH
            else -> BlockFace.DOWN
        }
        val rightB = player.levelBlock.down().getSide(rightDirection)
        val leftB = player.levelBlock.down().getSide(leftDirection)
        val backB = player.levelBlock.getSide(BlockFace.DOWN).getSide(backDirection)

        if (player.getSide(leftDirection).levelBlock.id != 0 || player.getSide(rightDirection).levelBlock.id != 0) this.tags.add(
            "friction_block"
        )

        //重置目标点 并发送拉回操作
        var resetTo = false
        var isBunnyHop = false

        if (data.getMovementTracker() != null) {
            val tracker = data.getMovementTracker()!!
            val height = tracker.getHeight()
            if (height == 0.0) this.tags.add("ground_walk")
            else {
                this.tags.add("bunny_hop")
                if (this.tags.contains("effect_jump")) {
                    val boost = player.getEffect(Effect.JUMP_BOOST).amplifier
                    allowDistance = height
                    limitDistance = if (boost == 0) Magic.JUMP_BOOST_V1_MAX_HEIGHT else Magic.JUMP_BOOST_V2_MAX_HEIGHT
                    if (allowDistance in Magic.BLOCK_BUNNY_BYPASS_V1_MIN..Magic.BLOCK_BUNNY_BYPASS_V1_MAX && boost == 0) limitDistance =
                        allowDistance
                    if (allowDistance in Magic.BLOCK_BUNNY_BYPASS_V2_MIN..Magic.BLOCK_BUNNY_BYPASS_V2_MAX && boost == 1) limitDistance =
                        allowDistance
                } else {
                    allowDistance = height
                    limitDistance = Magic.JUMP_NORMAL_WALK
                }
                val sinceLastBlock = player.add(0.0, -1.0, 0.0).levelBlock.getSide(backDirection)
                val sinceLastBlock2 = sinceLastBlock.getSide(backDirection)
                //跳跃问题
                val down2Block = player.add(0.0, -1.5, 0.0).levelBlock

                //考虑潜行的特殊问题
                if (player.isSneaking) {
                    val verticalSpeed = data.getMovementTracker()!!.getDistanceXZ()
                    if (verticalSpeed > Magic.SNEAK_JUMP_VERTICAL_MAX && height > Magic.JUMP_NORMAL_WALK) {
                        allowDistance = max(verticalSpeed, height)
                        limitDistance = min(Magic.SNEAK_JUMP_VERTICAL_MAX, Magic.JUMP_NORMAL_WALK)
                        resetTo = true
                    }
                }
                val sinceLastSS =
                    sinceLastBlock is BlockStairs || sinceLastBlock is BlockSlab || sinceLastBlock2 is BlockStairs || sinceLastBlock2 is BlockSlab || down2Block is BlockStairs || down2Block is BlockSlab
                //解决台阶和板砖的特殊问题
                if (this.tags.contains("stair_slab")) {
                    allowDistance = 0.0
                    limitDistance = 0.0
                    val verticalSpeed = to.distanceSquared(from)
                    if (yDistance > 0) {
                        if (verticalSpeed < 0.1 && yDistance < 0.3 && height < 0.5) {
                            allowDistance = verticalSpeed + 0.1
                            limitDistance = 0.1
                        }
                    } else if (yDistance < -0.5) {
                        allowDistance = abs(yDistance)
                        limitDistance = 0.5
                    }
                } else if (data.getSlabTick() > 0 || data.getStairTick() > 0 || sinceLastSS) limitDistance =
                    allowDistance

                limitDistance += player.ping * 0.00008 + 0.0001
                if (allowDistance in Magic.BLOCK_BUNNY_MIN..Magic.BLOCK_BUNNY_MAX || this.tags.contains("face_block") || this.tags.contains(
                        "friction_block"
                    ) || this.tags.contains("lose_sprint")
                ) {
                    limitDistance = allowDistance
                }
                if (!this.tags.contains("effect_jump") && !this.tags.contains("stair_slab")) {
                    isBunnyHop = true
                }
                if (allowDistance > limitDistance) {
                    resetTo = true
                    if (ConfigData.logging_debug) player.sendMessage("BunnyHop LagBack $allowDistance/$limitDistance")
                }
            }
        }

        if (sprint && !isBunnyHop) {
            if (bunnyHop in 1..2) {
                allowDistance = data.getSpeed()
                limitDistance = Magic.SPRINT_CHANGE_MAX_SPEED
                if (this.tags.contains("face_block")) limitDistance = Magic.SPRINT_CHANGE_FACE_BLOCK_MAX_SPEED
                if (this.tags.contains("lose_sprint") && player.inAirTicks > 5) limitDistance += Magic.SPRINT_CHANGE_SPEED_ADDITION * player.inAirTicks
                if (player.inAirTicks in 0..5) limitDistance += Magic.SPRINT_CHANGE_SPEED_ADDITION_V2 * player.inAirTicks
                if (data.getLoseSprintCount() >= 2) limitDistance += data.getLoseSprintCount() * Magic.SPRINT_CHANGE_SPEED_BACK_DIRECTION
                if (allowDistance > limitDistance) {
                    resetTo = true
                    if (ConfigData.logging_debug) player.sendMessage("BunnySprint LagBack")
                }
            }
        }

        if (flying) {
            if (player.inAirTicks >= 30 && yDistance == 0.0) if (to.y - data.getLastNormalGround().y >= 1.57) {
                resetTo = true
                allowDistance = player.inAirTicks * 0.15 + (to.y - data.getLastNormalGround().y) * 10
                limitDistance = 0.0
                if (ConfigData.logging_debug) player.sendMessage("Flying LagBack")
            }
        }
        if (player.inAirTicks == data.getLastInAirTicks()) {
            this.tags.add("same_at")
            val rightBack = rightB.getSide(backDirection)
            val rightToward = rightB.getSide(direction)
            val leftBack = leftB.getSide(backDirection)
            val leftToward = leftB.getSide(direction)
            val xzAir = rightBack.id == 0 && rightToward.id == 0 && leftBack.id == 0 && leftToward.id == 0
            if (downBlock.id == 0 && towardB.id == 0 && rightB.id == 0 && leftB.id == 0 && backB.id == 0 && xzAir) {
                this.tags.add("full_air")
                data.onFullAir()
                if (!data.isJump() && flying) resetTo = true
            }
        }
        if (player.inAirTicks >= 15 * 20) {
            this.tags.add("long_fly")
            resetTo = true
        }

        if (isBunnyHop) {
            val bunny = this.onBunnyHop(now, player, from, to, fromOnGround, toOnGround, yDistance, data, pData)
            if (bunny[0] > bunny[1]) {
                allowDistance = bunny[0]
                limitDistance = bunny[1]
                resetTo = true
            } else if (this.tags.contains("hunger")) {
                //饥饿时检测更严格
                val shortBunny =
                    this.vShortBunny(now, player, from, to, fromOnGround, toOnGround, yDistance, data, pData)
                if (shortBunny[0] > shortBunny[1]) {
                    allowDistance = shortBunny[0]
                    limitDistance = shortBunny[1]
                    resetTo = true
                }
            }
        }

        //解决固定问题
        if (player.isSneaking && this.tags.contains("bunny_down")) resetTo = false

        //清除Buffer 防止下一次检测误判
        if (!vanillaFall) data.clearListRecord()

        if (resetTo) {
            val event =
                EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, this.getFallDamage(player).toFloat())
            player.setback(data.getLastNormalGround(), this.typeName)
            if (ConfigData.check_survival_fly_set_back_fall_damage) NoCheatPlus.instance.server.pluginManager.callEvent(
                event
            )
        }

        return doubleArrayOf(allowDistance, limitDistance)
    }

    /**
     * 数据包计数器
     *
     * @return limited count
     */
    private fun countMaxMovementPacket(data: MovingData): Int {
        //默认的最大允许值
        var baseModifier = 2
        if (data.isJump()) baseModifier += 4
        //权威移动方式
        if (Bridge118.server_auth_mode) baseModifier += 18
        //如果玩家使用了自动疾跑,那么它的数量应该再加4,但大部分服主认为它是合法的
        baseModifier += 4

        return baseModifier
    }

    /**
     * 垂直检测
     *
     * @param now
     * @param player
     * @param from
     * @param to
     * @param fromOnGround
     * @param toOnGround
     * @param yDistance
     * @param data
     * @param pData
     *
     * @return limited distance
     */
    private fun setAllowedHDist(
        now: Long,
        player: Player,
        from: Location,
        to: Location,
        fromOnGround: Boolean,
        toOnGround: Boolean,
        yDistance: Double,
        data: MovingData,
        pData: IPlayerData,
    ): DoubleArray {
        var allowDistance = 0.0
        var limitDistance = 0.0
        //平地攀升情况分析
        if (yDistance < 0) return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
        if (from.floorX == to.floorX && from.floorZ == from.floorZ) {
            if (data.getMovementTracker() == null) return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
            if (((fromOnGround && !toOnGround) || (!fromOnGround && !toOnGround)) && yDistance > 0) {
                val height = data.getMovementTracker()!!.getHeight()
                //player.sendMessage("$height $yDistance ${now - data.getLastJump() < 800}")
                val jump = now - data.getLastJump() < 800
                //检测发包跳跃的特征漏洞
                if (height > Magic.BUNNY_TINY_JUMP_MAX && !jump) {
                    allowDistance = height
                    limitDistance = Magic.BUNNY_TINY_JUMP_MAX
                }
            }
        } else {
            if (data.getMovementTracker() == null) return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
            if (data.getPacketTracker() == null) return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
            if (yDistance >= 0.0) {
                //运动轨迹从抛物线突然改变,或突然获得不沿切线方向的速度
                //此情况不可能在作弊条件下触发
                if (ConfigData.logging_debug) {
                    player.sendMessage("${pData.getPlayerName()} y1++ height-> ground")
                }
                //跳过此检查
                allowDistance = Double.MIN_VALUE
                limitDistance = Double.MAX_VALUE
            }
        }


        return doubleArrayOf(allowDistance, limitDistance)
    }

    /**
     *  跳跃检测
     *
     * @param now
     * @param player
     * @param from
     * @param to
     * @param fromOnGround
     * @param toOnGround
     * @param yDistance
     * @param data
     * @param pData
     *
     * @return Bunny Distance
     */
    private fun onBunnyHop(
        now: Long,
        player: Player,
        from: Location,
        to: Location,
        fromOnGround: Boolean,
        toOnGround: Boolean,
        yDistance: Double,
        data: MovingData,
        pData: IPlayerData,
    ): DoubleArray {
        var allowDistance = 0.0
        var limitDistance = 0.0
        val vData = pData.getViolationData(typeName)

        val tinyHeight = this.getTinyHeight(player, ArrayList())
        val speed = from.distance(to)
        if (data.getMovementTracker() == null) return doubleArrayOf(allowDistance, limitDistance)
        if (now - data.getLastJump() <= 100) {

            //冰块单独判断
            if (data.getIceTick() >= 2) {
                this.tags.add("ice_ground")
                allowDistance = 0.0
                limitDistance = 0.0
                if (data.getSpeedTracker() == null) return doubleArrayOf(allowDistance, limitDistance)
                if (!data.getSpeedTracker()!!.isLive()) {
                    allowDistance = data.getSpeedTracker()!!.getMaxSpeed()
                    //重新使追踪器跳动,进行下一次追踪
                    data.getSpeedTracker()!!.run()
                    //头顶跳加速单独考虑
                    val headB1 = player.add(0.0, 1.75, 0.0).levelBlock
                    val headB2 = player.add(0.0, 2.0, 0.0).levelBlock
                    limitDistance = if (headB1.id == 0 && headB2.id == 0) Magic.BUNNY_ICE_GROUND_DEFAULT
                    else Magic.BUNNY_ICE_GROUND_DEFAULT + Magic.BUNNY_UP_BLOCK_ADDITION
                    //处理缓慢情况
                    if (this.tags.contains("hunger")) limitDistance = Double.MAX_VALUE
                }
                if (allowDistance > limitDistance) if (ConfigData.logging_debug) player.sendMessage("ice bunny $allowDistance/$limitDistance")
                return doubleArrayOf(allowDistance, limitDistance)
            }

            //短跳冲刺,完成Fly的第一个过程
            allowDistance = speed
            limitDistance = Magic.BUNNY_HOP_MAX_SPEED
            val absHeight = data.getMovementTracker()!!.getAbsHeight()
            val upBlock = player.add(0.0, 2.0, 0.0).levelBlock
            val upBlock2 = player.add(0.0, 1.75, 0.0).levelBlock
            val downBlock = LocUtil.getUnderBlock(player)
            val downBlock2 = player.add(0.0, -1.5, 0.0).levelBlock
            if (upBlock.id != 0 || upBlock2.id != 0) limitDistance = Magic.BLOCK_BUNNY_MAX
            if (tinyHeight == Magic.BUNNY_HOP_TINY_JUMP_FIRST || tinyHeight == Magic.BUNNY_HOP_TINY_JUMP_SECOND) {
                allowDistance = speed
                limitDistance = Magic.BUNNY_TINY_JUMP_MAX
                if (this.tags.contains("friction_block") || this.tags.contains("face_block")) {
                    limitDistance = Magic.BUNNY_TINY_JUMP_FRICTION
                }
                //解决板类方块误判的问题
                if (downBlock is BlockThin || downBlock2 is BlockThin) {
                    limitDistance = Magic.BUNNY_TINY_THIN_SPEED_MAX
                }
                if (this.tags.contains("effect_speed")) {
                    (if (player.getEffect(Effect.SPEED).amplifier == 0) {
                        Magic.BUNNY_TINY_JUMP_SPEED_BOOST_V1
                    } else {
                        Magic.BUNNY_TINY_JUMP_SPEED_BOOST_V2
                    }).also { limitDistance = it }
                }
                if (allowDistance > limitDistance) {
                    //解决冲刺出去后导致的误判
                    if (absHeight > 0.1 && data.getIceTick() == 0) {
                        vData.addPreVL("tiny_bunny")
                        if (ConfigData.logging_debug) {
                            player.sendMessage("TinyBunny LagBack $absHeight")
                        }
                    }
                } else vData.clearPreVL("tiny_bunny")
                if (vData.getPreVL("tiny_bunny") > 5) {
                    this.tags.add("bad_tinny")
                    vData.clearPreVL("tiny_bunny")
                } else {
                    allowDistance = Double.MIN_VALUE
                    limitDistance = Double.MAX_VALUE
                }
                return doubleArrayOf(allowDistance, limitDistance)
            }
            if (this.tags.contains("bad_tinny")) {
                if (this.tags.contains("friction_block") || this.tags.contains("face_block")) limitDistance =
                    Magic.BUNNY_TINY_JUMP_FRICTION
            }
            //解决边角问题
            if (to.x != from.x && to.z != from.z) {
                if (tinyHeight < Magic.BUNNY_TINY_DIRECTION_HEIGHT && speed > Magic.BUNNY_TINY_JUMP_MAX) {
                    if (yDistance < 0.0 && !fromOnGround && !toOnGround && data.getIceTick() == 0) {
                        if (ConfigData.logging_debug) {
                            player.sendMessage("BunnyBHop $speed $yDistance  $tinyHeight  $absHeight")
                        }
                        return doubleArrayOf(speed, Magic.BUNNY_TINY_JUMP_MAX)
                    }
                }
            }
            if (tinyHeight < Magic.DEFAULT_JUMP_HEIGHT) return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
            if (allowDistance > limitDistance) {
                this.tags.add("bad_bunny_hop")
                vData.addPreVL("bad_bunny_hop")
                limitDistance = Magic.BUNNY_HOP_MAX_SPEED_MAX
                if (this.tags.contains("effect_speed")) {
                    limitDistance = if (player.getEffect(Effect.SPEED).amplifier == 0) {
                        Magic.SPEED_BOOST_V1_BUNNY_HOP
                    } else Magic.SPEED_BOOST_V2_BUNNY_HOP
                }
                if (downBlock is BlockThin || downBlock2 is BlockThin) {
                    allowDistance = speed
                    limitDistance = Magic.JUMP_NORMAL_WALK
                }
                if (allowDistance > limitDistance) {
                    vData.addPreVL("bad_bunny_hop")
                }
                if (vData.getPreVL("bad_bunny_hop") > 3) {
                    if (ConfigData.logging_debug) player.sendMessage("Bad Bunny All S=$speed/$limitDistance $yDistance  $absHeight")
                    vData.clearPreVL("bad_bunny_hop")
                } else {
                    allowDistance = Double.MIN_VALUE
                    limitDistance = Double.MAX_VALUE
                }
            } else vData.clearPreVL("bad_bunny_hop")
        } else {
            val vDistVertical =
                this.vDistVertical(now, player, from, to, fromOnGround, toOnGround, yDistance, data, pData)
            return doubleArrayOf(vDistVertical[0], vDistVertical[1])
        }

        //解决常规的速度问题
        if (allowDistance > limitDistance) {
            val diff = allowDistance - limitDistance - 0.75
            if (diff > 0) return doubleArrayOf(allowDistance, limitDistance)
        }
        return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
    }

    /**
     * 更新幽灵方块跟踪器
     *
     * @param packet
     */
    private fun updateGhostBlock(packet: WrapperPlaceBlockPacket, data: MovingData) {
        val player = packet.player
        data.setGhostBlockChecker(GhostBlockChecker(player.name, packet.block, 45, player.inventory.itemInHand.id))
    }

    /**
     *  空中飞行检测
     *
     *  @param now
     *  @param player
     *  @param from
     *  @param to
     *  @param fromOnGround
     *  @param toOnGround
     *  @param yDistance
     *  @param data
     *  @param pData
     *
     *  @return
     */
    private fun inAirCheck(
        now: Long,
        player: Player,
        from: Location,
        to: Location,
        fromOnGround: Boolean,
        toOnGround: Boolean,
        yDistance: Double,
        data: MovingData,
        pData: IPlayerData,
    ): DoubleArray {
        var allowDistance = 0.0
        var limitDistance = 0.0

        var resetTo = false

        val speed = to.distance(from)

        //异常的移动情况
        if (this.tags.contains("same_at") && this.tags.contains("full_air")) {
            pData.getViolationData(this.typeName).addPreVL("spoof_ground")
        } else pData.getViolationData(this.typeName).clearPreVL("spoof_ground")
        val limitAT = if (this.tags.contains("hunger")) 13 else 7
        if (pData.getViolationData(this.typeName).getPreVL("spoof_ground") > limitAT) {
            allowDistance = speed
            limitDistance = if (speed > Magic.DEFAULT_FLY_SPEED) Magic.DEFAULT_FLY_SPEED else 0.0
            resetTo = true
        }

        if (!fromOnGround && !toOnGround) {
            val friction = data.getNextVerticalFriction()
            val hAllowDistance =
                if (abs(data.getLastMotionY() - 0.08) * friction < 0.005) -0.08 * friction else (data.getLastMotionY() - 0.08) * friction
            if (abs(yDistance - hAllowDistance) == Magic.VANILLA_Y_DIFF && yDistance == Magic.VANILLA_Y_DELTA) {
                allowDistance = max(yDistance, hAllowDistance)
                limitDistance = min(yDistance, hAllowDistance)
                resetTo = true
            } else if (yDistance == data.getLastMotionY() && !this.tags.contains("same_at") && data.getAcc() == 0.0) {
                //检测节发的BlinkFly
                if (abs(yDistance - hAllowDistance) == Magic.VANILLA_Y_DIFF_V2) resetTo = false
                //非惯性运动情况单独考
                if (this.tags.contains("fall") && player.inAirTicks > this.countMaxAirTick(
                        player, data, now
                    ) && yDistance > -0.31
                ) {
                    allowDistance = player.inAirTicks / 15 * (speed - Magic.DEFAULT_FLY_SPEED)
                    limitDistance = 0.0
                    resetTo = true
                }
            }
        }

        if (resetTo) {
            val event =
                EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, this.getFallDamage(player).toFloat())
            player.setback(data.getLastNormalGround(), this.typeName)
            if (ConfigData.check_survival_fly_set_back_fall_damage) NoCheatPlus.instance.server.pluginManager.callEvent(
                event
            )
        }

        return doubleArrayOf(allowDistance, limitDistance)
    }

    private fun vDistGhostBlock(ghostBlockChecker: GhostBlockChecker): DoubleArray {
        var vAllowDistance = 0.0
        val vLimitDistance = 0.5

        //幽灵方块判断,通过追踪器来阻止它的误判
        ghostBlockChecker.run()
        if (ghostBlockChecker.isLive()) {
            if (ghostBlockChecker.isChangeBlock() && ghostBlockChecker.isLag()) {
                if (ghostBlockChecker.canLag()) {
                    vAllowDistance += 0.25
                    ghostBlockChecker.onLag()
                }
            }
        }
        return doubleArrayOf(vAllowDistance, vLimitDistance)
    }

    /**
     * 空中位移检测
     *
     * @param now
     * @param player
     * @param from
     * @param to
     * @param fromOnGround
     * @param toOnGround
     * @param yDistance
     * @param data
     * @param pData
     *
     * @return
     */
    private fun vDistVertical(
        now: Long,
        player: Player,
        from: Location,
        to: Location,
        fromOnGround: Boolean,
        toOnGround: Boolean,
        yDistance: Double,
        data: MovingData,
        pData: IPlayerData,
    ): DoubleArray {
        var allowDistance = 0.0
        var limitDistance = 0.0

        if (data.getMovementTracker() == null) return doubleArrayOf(allowDistance, limitDistance)
        val verticalSpeed = data.getMovementTracker()!!.getDistanceXZ()

        //跳跃检测
        val bunny = this.tags.contains("bunny_hop")
        if (bunny && !this.tags.contains("hunger")) {
            if (yDistance < 0) {
                allowDistance = verticalSpeed
                limitDistance = Double.MAX_VALUE
                this.tags.add("bunny_down")
                if (verticalSpeed > Magic.BUNNY_DOWN_MAX_SPEED) {
                    limitDistance =
                        if (yDistance < -0.1) Magic.BUNNY_DOWN_MAX_SPEED else Magic.BUNNY_DOWN_MAX_SPEED + 0.45
                    //处理起跳问题
                    if (fromOnGround && toOnGround) limitDistance = Magic.BUNNY_DOWN_MAX_SPEED
                }
            } else if (yDistance > 0) {
                allowDistance = verticalSpeed
                limitDistance = Double.MAX_VALUE
                this.tags.add("bunny_up")
                if (verticalSpeed > Magic.BUNNY_UP_MAX_SPEED && yDistance < 0.01) {
                    limitDistance = Magic.BUNNY_UP_MAX_SPEED
                    //处理特殊情况
                    if (player.add(0.0, 1.5, 0.0).levelBlock.id != 0) limitDistance += Magic.BUNNY_UP_BLOCK_ADDITION
                }
            }
            //忽略此检查
            if (this.tags.contains("stair_slab")) {
                allowDistance = Double.MIN_VALUE
                limitDistance = Double.MAX_VALUE
            }
            if (allowDistance > limitDistance) {
                if (ConfigData.logging_debug) player.sendMessage("bunny vertical ${now - data.getLastJump()}")
                pData.getViolationData(this.typeName).addPreVL("bunny_vertical")
                if (pData.getViolationData(this.typeName).getPreVL("bunny_vertical") > 12) {
                    pData.getViolationData(this.typeName).clearPreVL("bunny_vertical")
                } else {
                    allowDistance = Double.MIN_VALUE
                    limitDistance = Double.MAX_VALUE
                }
            } else pData.getViolationData(this.typeName).clearPreVL("bunny_vertical")
        } else if (this.tags.contains("ground_walk") && !this.tags.contains("hunger")) {
            if (now - data.getLastJump() < 800) return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
            if ((fromOnGround && toOnGround) || (!fromOnGround && !toOnGround)) {
                val speed = to.distance(from)
                var walkSpeed = Magic.WALK_SPEED * (player.movementSpeed / Magic.DEFAULT_WALK_SPEED)
                //解决加速药水问题
                if (this.tags.contains("effect_speed")) {
                    walkSpeed *= (1 + 0.2 * (player.getEffect(Effect.SPEED).amplifier + 1))
                    //结局冰面匀加速问题
                    if (data.getIceTick() > 0) walkSpeed += (0.4 * (1 + 0.2 * (player.getEffect(Effect.SPEED).amplifier + 1))) / 0.6 * (data.getIceTick() * 0.98)
                } else if (data.getIceTick() > 0) walkSpeed += (0.4 / 0.6 * (data.getIceTick() * 0.98))
                //水平运动时若突然改变y轴因夹角导致获得斜方向额外加速度的问题单独分析
                if (data.getSinceLastYChange() > 2) walkSpeed += (data.getLastFrictionVertical() + data.getNextHorizontalFriction()) / 2.0 * 0.4 * 0.98
                //水平位移粗检查
                if (data.getLoseSprintCount() < 1 && player.isSprinting && (yDistance > 0.0 || data.getSinceLastYChange() > 5)) {
                    //可忽略的误差分析
                    if (speed - walkSpeed * 2.0 < 0.1) return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
                    if (speed > walkSpeed * 2.0 || walkSpeed < 0.1436) {
                        pData.getViolationData(this.typeName).addPreVL("ground_vertical")
                        if (pData.getViolationData(this.typeName).getPreVL("ground_vertical") > 5) {
                            pData.getViolationData(this.typeName).clearPreVL("ground_vertical")
                            allowDistance = max(speed, walkSpeed * 2.0)
                            limitDistance = min(speed, walkSpeed * 2.0)
                        } else {
                            allowDistance = Double.MIN_VALUE
                            limitDistance = Double.MAX_VALUE
                        }
                    } else pData.getViolationData(this.typeName).clearPreVL("ground_vertical")
                }
            }
        }

        return doubleArrayOf(allowDistance, limitDistance)
    }

    /**
     * 短跳跃检测
     * 在饥饿的状态下
     *
     * @param now
     * @param player
     * @param from
     * @param to
     * @param fromOnGround
     * @param toOnGround
     * @param yDistance
     * @param data
     * @param pData
     *
     * @return
     */
    private fun vShortBunny(
        now: Long,
        player: Player,
        from: Location,
        to: Location,
        fromOnGround: Boolean,
        toOnGround: Boolean,
        yDistance: Double,
        data: MovingData,
        pData: IPlayerData,
    ): DoubleArray {
        var allowDistance = 0.0
        var limitDistance = 0.0
        val speed = to.distance(from)
        if (data.getMovementTracker() == null) return doubleArrayOf(allowDistance, limitDistance)
        val verticalSpeed = data.getMovementTracker()!!.getDistanceXZ()
        val iceDown =
            LocUtil.isIce(LocUtil.getUnderBlock(player)) || LocUtil.isIce(player.add(0.0, 1.25, 0.0).levelBlock)
        if (now - data.getLastJump() < 100) {
            //冰面特殊问题
            if (this.tags.contains("ice_ground") || iceDown || data.getIceTick() != 0) return doubleArrayOf(
                allowDistance, Magic.BUNNY_ICE_SHORT_BUNNY_SPEED_MAX
            )
            val validYDist = yDistance < Magic.HUNGER_BUNNY_Y_MIN || yDistance > Magic.HUNGER_BUNNY_Y_MAX
            if (validYDist) if (speed > Magic.HUNGER_BUNNY_MAX_SPEED) {
                allowDistance = speed
                limitDistance = Magic.HUNGER_BUNNY_MAX_SPEED
                if (ConfigData.logging_debug) if (allowDistance > limitDistance) player.sendMessage("Short Bunny Y Dist $allowDistance/$limitDistance")
            } else if (verticalSpeed > Magic.HUNGER_BUNNY_VERTICAL_MAX_SHORT) {
                allowDistance = verticalSpeed
                limitDistance = Magic.HUNGER_BUNNY_VERTICAL_MAX_SHORT
                if (ConfigData.logging_debug) if (allowDistance > limitDistance) player.sendMessage("Short Bunny vertical speed $allowDistance/$limitDistance")
            }
            //处理特殊情况
            if (this.tags.contains("friction_block") || this.tags.contains("face_block") || this.tags.contains("stair_slab")) return doubleArrayOf(
                Double.MIN_VALUE, Double.MAX_VALUE
            )
        } else {
            if (this.tags.contains("ice_ground") || iceDown || data.getIceTick() != 0) return doubleArrayOf(
                verticalSpeed, Magic.BUNNY_ICE_SHORT_BUNNY_SPEED_MAX
            )
            if (verticalSpeed > Magic.HUNGER_BUNNY_VERTICAL_MAX_LONG) {
                //解决方块边缘化问题
                val block = LocUtil.getUnderBlock(player).getSide(player.direction)
                if (block.id == 0) {
                    val friction = Magic.BUNNY_ICE_SHORT_BUNNY_SPEED_MAX
                    if (yDistance in -1.0..2.75 && verticalSpeed > friction) pData.getViolationData(this.typeName)
                        .addPreVL("hunger_bad_bunny")
                } else {
                    if (fromOnGround && !toOnGround) pData.getViolationData(this.typeName).addPreVL("hunger_bad_bunny")
                    if (!fromOnGround && !toOnGround) pData.getViolationData(this.typeName).addPreVL("hunger_bad_bunny")
                    if (fromOnGround && toOnGround) pData.getViolationData(this.typeName).addPreVL("hunger_bad_bunny")
                }
            } else pData.getViolationData(this.typeName).clearPreVL("hunger_bad_bunny")
            if (pData.getViolationData(this.typeName).getPreVL("hunger_bad_bunny") > 10) {
                allowDistance = verticalSpeed
                limitDistance = Magic.HUNGER_BUNNY_VERTICAL_MAX_LONG
            } else return doubleArrayOf(Double.MIN_VALUE, Double.MAX_VALUE)
        }

        return doubleArrayOf(allowDistance, limitDistance)
    }

    /**
     * 预测空中滞留单位时间的最大值
     *
     * @param player
     * @param data
     * @param now
     *
     * @return 最大值
     */
    private fun countMaxAirTick(player: Player, data: MovingData, now: Long): Double {
        //默认限制值,为完成一个完整的平面斜抛运动所用的时间
        var limitBasicTick = 13.0

        //考虑药水情况
        if (this.tags.contains("effect_jump")) limitBasicTick *= (player.getEffect(Effect.JUMP_BOOST).amplifier + 1) * 3
        if (now - data.getLastJump() <= 500) limitBasicTick += 15
        if (LocUtil.getPlayerHeight(player) < 3) limitBasicTick += 5

        //落体运动的情况考虑
        limitBasicTick += (LocUtil.getPlayerHeight(player) / 10 * 0.5)
        return limitBasicTick
    }

    /**
     * set next friction
     *
     * @param from
     * @param to
     * @param data
     */
    private fun setNextFriction(from: Location, to: Location, data: MovingData) {
        if (from.levelBlock.id == Block.COBWEB || to.levelBlock.levelBlock.id == Block.COBWEB) {
            data.setNextHorizontalFriction(0.0)
            data.setNextVerticalFriction(0.0)
        } else if (from.levelBlock.canBeClimbed() || to.levelBlock.canBeClimbed()) {
            data.setNextHorizontalFriction(0.0)
            data.setNextVerticalFriction(0.0)
        } else if (LocUtil.isLiquid(from.levelBlock)) {
            if (LocUtil.isLava(from.levelBlock)) {
                data.setNextVerticalFriction(Magic.FRICTION_MEDIUM_LAVA)
                data.setNextHorizontalFriction(Magic.FRICTION_MEDIUM_LAVA)
            }
            if (LocUtil.isWater(from.levelBlock)) {
                data.setNextVerticalFriction(Magic.FRICTION_MEDIUM_WATER)
                data.setNextHorizontalFriction(Magic.FRICTION_MEDIUM_WATER)
            }
        } else if (to.levelBlock.id == 0 && from.levelBlock.id == 0) {
            data.setNextHorizontalFriction(Magic.FRICTION_MEDIUM_AIR)
            data.setNextVerticalFriction(Magic.FRICTION_MEDIUM_AIR)
        } else {
            data.setNextHorizontalFriction(0.0)
            data.setNextVerticalFriction(Magic.FRICTION_MEDIUM_AIR)
        }

    }

    /**
     * on vertical falling
     *
     * @return
     */
    private fun onFallingVertical(data: MovingData, samePos: Boolean): Boolean {
        var maxSpeed = 0.0
        var lagTick = 0
        //玩家跳跃时跳过此检测
        if (data.isJump()) return true

        //强制判断结果
        if (samePos) {
            for (speed in data.getSpeedList()) {
                if (speed >= maxSpeed) {
                    maxSpeed = speed
                } else lagTick++
            }
            var deltaY = 0.0
            for (delta in data.getMotionYList()) {
                val preY = (deltaY - 0.08) * Magic.TINY_GRAVITY
                val diff = abs(delta - preY)
                if (diff > 0.017 && abs(preY) > 0.005) lagTick++
                deltaY = delta
            }
            return lagTick <= 8
        } else {
            var tick = 0
            lagTick = 0
            val g = -Magic.TINY_GRAVITY
            for (delta in data.getMotionYList()) {
                val locationList = data.getLocationList()
                val speedList = data.getSpeedList()
                if (locationList.size < 2 || speedList.size < 2) break
                if (locationList.size <= tick || speedList.size <= tick) break
                val x0 = locationList[0].x
                val y0 = locationList[0].y
                val z0 = locationList[0].z
                val x1 = locationList[1].x
                val z1 = locationList[1].z
                val motionDeltaX = sqrt((x0 - x1).pow(2) + (z0 - z1).pow(2))
                val speed = speedList[tick]
                val x = locationList[tick].x
                val y = locationList[tick].y
                val z = locationList[tick].z
                val motionX = sqrt(x.pow(2) + z.pow(2))
                val deltaXZ = motionX - motionDeltaX
                val locDeltaY = y - y0
                val mathSpeed = (g * deltaXZ.pow(2) / (2 * locDeltaY) + 2 * g * locDeltaY).pow(0.5)
                val mathDeltaXZ = motionDeltaX * tick
                if (speed > mathSpeed || (sqrt((x - x0).pow(2) + (z - z0).pow(2)) > mathDeltaXZ * 1.5) || locDeltaY > 0) {
                    lagTick++
                }
                tick++
            }
        }
        return lagTick <= 10
    }

    /**
     * 获取精确高度
     *
     * @param player
     * @param expect
     *
     * @return Height
     */
    private fun getTinyHeight(player: Player, expect: List<Int>): Double {
        return if (LocUtil.getPlayerHeight(player) <= 1) {
            val b1 = player.levelBlock
            val b2 = LocUtil.getUnderBlock(player)
            if (expect.contains(b1.id)) {
                player.y - b2.maxY
            } else player.y - b1.minY
        } else 0.0
    }

    /**
     * 获取掉落伤害
     * 此公式来自 Spigot
     *
     * @param player
     *
     * @return damage
     */
    private fun getFallDamage(player: Player): Double {
        return if (player.fallDistance > 3) player.fallDistance - 3.0 else 0.0
    }

}