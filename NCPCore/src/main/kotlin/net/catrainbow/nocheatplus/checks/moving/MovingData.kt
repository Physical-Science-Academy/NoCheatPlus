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
import cn.nukkit.Server
import cn.nukkit.block.BlockSlab
import cn.nukkit.block.BlockStairs
import cn.nukkit.level.Location
import cn.nukkit.level.Position
import cn.nukkit.math.Vector3
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil.Companion.isAboveIce
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil.Companion.isAboveSlab
import net.catrainbow.nocheatplus.checks.moving.location.LocUtil.Companion.isAboveStairs
import net.catrainbow.nocheatplus.checks.moving.magic.GhostBlockChecker
import net.catrainbow.nocheatplus.checks.moving.magic.LostGround
import net.catrainbow.nocheatplus.checks.moving.model.*
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.isInLiquid
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.isInWeb
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.onClimbedBlock
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.onGround
import net.catrainbow.nocheatplus.compat.nukkit.FoodData118
import net.catrainbow.nocheatplus.components.data.ICheckData
import kotlin.math.abs

/**
 * 移动数据储存
 *
 * @author Catrainbow
 */
class MovingData : ICheckData {

    /**
     * Moving Data History
     */
    private var lastOnGround = false
    private var onGround = false
    private var lastLocation: Location = Location.fromObject(NoCheatPlus.instance.server.defaultLevel.spawnLocation)
    private var location: Location = Location.fromObject(NoCheatPlus.instance.server.defaultLevel.spawnLocation)
    private var distanceData: DistanceData = DistanceData(Position(0.0, 0.0, 0.0), Position(0.0, 0.0, 0.0))
    private var lastMotionX = 0.0
    private var lastMotionY = 0.0
    private var lastMotionZ = 0.0
    private var lastSpeed = 0.0
    private var lastSprint = false
    private var lastFrictionHorizontal = 0.0
    private var lastFrictionVertical = 0.0
    private var lastInAirTick = 0
    private var lastPlayerJump = System.currentTimeMillis()
    private var moveTracker: MoveTracker? = null
    private var speedTracker: SpeedTracker? = null
    private var packetTracker: PacketTracker? = null
    private var foodTracker: EatPacketTracker? = null
    private var safeSpawn = false
    private var voidHurt = false
    private var fallHurt = false
    private var lastChangeSwimAction = System.currentTimeMillis()
    private var lastChangeGlideAction = System.currentTimeMillis()
    private var lastGlideBooster = System.currentTimeMillis()
    private var lastConsumeFood = System.currentTimeMillis()
    private var beforeLastConsumeFood = System.currentTimeMillis()
    private var lastTeleport = System.currentTimeMillis()
    private var firstGagApple = false
    private var lastHealth = 0.0

    /**
     * Current Moving Data
     */
    private var from: Location = Location.fromObject(NoCheatPlus.instance.server.defaultLevel.spawnLocation)
    private var to: Location = Location.fromObject(NoCheatPlus.instance.server.defaultLevel.spawnLocation)
    private var motionX = 0.0
    private var motionY = 0.0
    private var motionZ = 0.0
    private var speed = 0.0
    private var loseSprintCount = 0
    private var sprint = false
    private var inAirTick = 0
    private var groundTick = 0
    private var fullAirTick = 0
    private var liquidTick = 0
    private var iceTick = 0
    private var slabTick = 0
    private var stairTick = 0
    private var webTick = 0
    private var ladderTick = 0
    private var swimTick = 0
    private var loseSwimTick = 0
    private var loseLiquidTick = 0
    private var acc = 0.0
    private var sinceLastYChange = 0
    private var live = true
    private var respawnTick = 0
    private var knockBackHurtTick = 100
    private var eatFood = false
    private var eatFoodTick = 0
    private var fallDist = 0.0
    private var onSlimeBump = false
    private var slimeTick = 0

    //Timer Clock
    private var timeBalance = System.currentTimeMillis() - 5000L

    private var motionYList: ArrayList<Double> = ArrayList()
    private var locationList: ArrayList<Location> = ArrayList()
    private var speedList: ArrayList<Double> = ArrayList()
    private var ghostBlockChecker: GhostBlockChecker = GhostBlockChecker("NCP", Vector3(0.0, 0.0, 0.0), 0, 0)
    private var lostGround: LostGround? = null

    private var normalGround = Location(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Server.getInstance().defaultLevel)

    /**
     * Next MovingData
     */
    private var nextFrictionHorizontal = 0.0
    private var nextFrictionVertical = 0.0

    /**
     * 处理数据
     */
    fun handleMovingData(player: Player, from: Location, to: Location, data: DistanceData) {
        //保证进服出生在虚空不会被误判
        val serverOnGround = player.location.onGround()
        if (!safeSpawn) if (this.lastSpeed != 0.0 || serverOnGround || player.onGround || player.isInLiquid() || player.gamemode == 1 || player.gamemode == 3) this.safeSpawn =
            true
        if (voidHurt) if (this.lastSpeed != 0.0 || serverOnGround || player.onGround || player.isInLiquid() || player.gamemode == 1 || player.gamemode == 3) this.voidHurt =
            false

        if (player.gamemode == 1) this.normalGround = player.location
        if (this.knockBackHurtTick < 100) this.knockBackHurtTick++

        this.lastOnGround = onGround
        this.lastLocation = location
        this.lastSpeed = speed
        this.lastMotionX = motionX
        this.lastMotionY = motionY
        this.lastMotionZ = motionZ
        this.lastSprint = this.sprint
        this.lastInAirTick = this.inAirTick

        if (this.moveTracker == null) {
            this.moveTracker = MoveTracker(player)
            this.moveTracker!!.close()
        } else {
            this.moveTracker!!.onUpdate(System.currentTimeMillis())
        }

        if (this.speedTracker == null) {
            this.speedTracker = SpeedTracker(this)
            this.speedTracker!!.kill()
        } else {
            this.speedTracker!!.onUpdate()
        }

        if (this.packetTracker == null) {
            this.packetTracker = PacketTracker(this)
            this.packetTracker!!.kill()
        } else {
            this.packetTracker!!.onUpdate()
        }

        if (this.foodTracker == null) {
            this.foodTracker = EatPacketTracker(this)
            //重新启动一次追踪器使数据初始化
            this.foodTracker!!.kill()
            this.foodTracker!!.run()
        } else {
            this.foodTracker!!.onUpdate()
        }

        if (this.ghostBlockChecker.getName() == "NCP") {
            this.ghostBlockChecker = GhostBlockChecker(player.name, Vector3(0.0, 0.0, 0.0), 0, 0)
        }
        if (normalGround.x == 0.0 && normalGround.y == 0.0 && normalGround.z == 0.0) this.normalGround = player.location
        this.location = player.location
        this.onGround = player.onGround
        this.from = from
        this.to = to
        this.distanceData = data
        this.motionX = to.x - from.x
        this.motionY = to.y - from.y
        this.motionZ = to.z - from.z
        this.fallDist = player.fallDistance.toDouble()
        this.inAirTick = player.inAirTicks
        if (this.motionY == 0.0 && this.sinceLastYChange < 50) this.sinceLastYChange++ else this.sinceLastYChange = 0
        this.speed = to.distance(from)
        this.motionYList.add(to.y - from.y)
        this.locationList.add(player.location)
        this.speedList.add(this.speed)
        this.sprint = player.isSprinting
        this.acc = abs(this.motionY - this.lastMotionY)
        if (loseSprintCount == 0) {
            if (this.lastSprint) {
                if (!player.isSprinting) this.loseSprintCount++
            }
        } else this.loseSprintCount++
        if (this.loseSprintCount > 5) this.loseSprintCount = 0
        if (this.onGround) fullAirTick = 0
        if (this.respawnTick > 0) this.respawnTick--
        if (this.onGround) this.groundTick++ else this.groundTick = 0
        if (player.isInLiquid()) {
            this.liquidTick++
            this.loseLiquidTick = 0
        } else if (this.liquidTick in 1..200) {
            this.liquidTick--
            this.loseLiquidTick++
        } else this.liquidTick = 0
        if (player.isAboveIce()) this.iceTick++
        else if (this.iceTick in 1..200) this.iceTick-- else this.iceTick = 0
        if (player.isAboveSlab()) this.slabTick++ else if (this.slabTick in 1..200) this.slabTick-- else this.slabTick =
            0
        if (player.isAboveStairs()) this.stairTick++ else if (this.stairTick in 1..200) this.stairTick-- else this.stairTick =
            0
        if (player.isInWeb()) this.webTick++
        else if (this.webTick in 1..200) this.webTick-- else this.webTick = 0
        if (player.onClimbedBlock()) this.ladderTick++
        else if (this.ladderTick in 1..200) this.ladderTick-- else this.ladderTick = 0
        if (player.isSwimming) {
            this.swimTick++
            this.loseSwimTick = 0
        } else if (this.swimTick in 1..200) {
            this.swimTick--
            this.loseSwimTick++
        } else this.swimTick = 0
        if (loseSwimTick > 5) {
            loseSwimTick = 0
            this.swimTick = 0
        }
        if (loseSwimTick < 0) loseSwimTick = 0
        if (loseLiquidTick < 0) loseLiquidTick = 0
        if (groundTick > 10) {
            this.liquidTick = 0
            this.loseSwimTick = 0
            this.loseLiquidTick = 0
        }
        if (eatFood) {
            this.eatFoodTick++
        } else eatFoodTick = 0
        if (this.eatFoodTick > FoodData118.DEFAULT_EAT_TICK) {
            this.eatFood = false
            this.eatFoodTick = 0
            this.foodTracker!!.kill()
        }
        if (System.currentTimeMillis() - this.lastConsumeFood > 100 && this.firstGagApple) this.firstGagApple = false
        this.lastHealth = player.health.toDouble()
        if (this.onSlimeBump) this.slimeTick++ else this.slimeTick = 0
    }

    //进入服务器初始化数据
    fun initData(player: Player) {
        this.setLastNormalGround(player.location)
        this.from = player.location
        this.to = player.location
        this.location = player.location
        this.lastLocation = player.location
    }

    fun getLiquidTick(): Int {
        return this.liquidTick
    }

    fun getMotionX(): Double {
        return this.motionX
    }

    fun getMotionY(): Double {
        return this.motionY
    }

    fun getMotionZ(): Double {
        return this.motionZ
    }

    fun getLastMotionX(): Double {
        return this.lastMotionX
    }

    fun getLastMotionZ(): Double {
        return this.lastMotionZ
    }

    fun getLoseSprintCount(): Int {
        return this.loseSprintCount
    }

    fun setLoseSprintCount(count: Int) {
        this.loseSprintCount = count
    }

    fun getNextHorizontalFriction(): Double {
        return this.nextFrictionHorizontal
    }

    fun getNextVerticalFriction(): Double {
        return this.nextFrictionVertical
    }

    fun setNextHorizontalFriction(value: Double) {
        this.lastFrictionHorizontal = this.nextFrictionHorizontal
        this.nextFrictionHorizontal = value
    }

    fun setNextVerticalFriction(value: Double) {
        this.lastFrictionVertical = this.nextFrictionVertical
        this.nextFrictionVertical = value
    }

    fun getSpeed(): Double {
        return this.speed
    }

    fun clearListRecord() {
        this.motionYList.clear()
        this.locationList.clear()
        this.speedList.clear()
    }

    fun getSpeedList(): ArrayList<Double> {
        return this.speedList
    }

    fun balance() {
        this.timeBalance = System.currentTimeMillis()
    }

    fun isBalance(): Boolean {
        this.timeBalance -= 1000
        return System.currentTimeMillis() - timeBalance >= 5000
    }

    fun resetKnockBackTick() {
        this.knockBackHurtTick = 0
    }

    fun getKnockBackTick(): Int {
        return this.knockBackHurtTick
    }

    fun getMotionYList(): ArrayList<Double> {
        return this.motionYList
    }

    fun getLocationList(): ArrayList<Location> {
        return this.locationList
    }

    fun getGhostBlockChecker(): GhostBlockChecker {
        return this.ghostBlockChecker
    }

    fun getLastMotionY(): Double {
        return this.lastMotionY
    }

    fun getSinceLastYChange(): Int {
        return this.sinceLastYChange
    }

    fun getLastFrictionVertical(): Double {
        return this.lastFrictionVertical
    }

    fun getAcc(): Double {
        return this.acc
    }

    fun setGhostBlockChecker(checker: GhostBlockChecker) {
        this.ghostBlockChecker = checker
    }

    fun onJump() {
        this.lastPlayerJump = System.currentTimeMillis()
    }

    fun getPacketTracker(): PacketTracker? {
        return this.packetTracker
    }

    fun isJump(): Boolean {
        return System.currentTimeMillis() - this.lastPlayerJump <= 800
    }

    fun getLastJump(): Long {
        return this.lastPlayerJump
    }

    fun updateNormalLoc(location: Location) {
        this.normalGround = location
    }

    fun getLastInAirTicks(): Int {
        return this.lastInAirTick
    }

    fun getMovementTracker(): MoveTracker? {
        return this.moveTracker
    }

    fun onFullAir() {
        this.fullAirTick++
    }

    fun getFullAirTick(): Int {
        return this.fullAirTick
    }

    fun setFullAirTick(tick: Int) {
        this.fullAirTick = tick
    }

    fun getLastNormalGround(): Location {
        return this.normalGround
    }

    fun setLastNormalGround(location: Location) {
        this.normalGround = location
    }

    fun getIceTick(): Int {
        return this.iceTick
    }

    fun getSpeedTracker(): SpeedTracker? {
        return this.speedTracker
    }

    fun getStairTick(): Int {
        return this.stairTick
    }

    fun respawn() {
        this.respawnTick = 3
    }

    fun getRespawnTick(): Int {
        return this.respawnTick
    }

    fun setLive(live: Boolean) {
        this.live = live
    }

    fun isLive(): Boolean {
        return this.live
    }

    fun isSafeSpawn(): Boolean {
        return this.safeSpawn
    }

    fun setVoidHurt(boolean: Boolean) {
        this.voidHurt = boolean
    }

    fun isVoidHurt(): Boolean {
        return this.voidHurt
    }

    fun getSlabTick(): Int {
        return this.slabTick
    }

    fun getWebTick(): Int {
        return this.webTick
    }

    fun getSwimTick(): Int {
        return this.swimTick
    }

    fun getLoseSwimTick(): Int {
        return this.loseSwimTick
    }

    fun loseSwim() {
        this.lastChangeSwimAction = System.currentTimeMillis()
    }

    fun getLastToggleSwim(): Long {
        return this.lastChangeSwimAction
    }

    fun getLoseLiquidTick(): Int {
        return this.loseLiquidTick
    }

    fun onGlideBooster() {
        this.lastGlideBooster = System.currentTimeMillis()
    }

    fun loseGlide() {
        this.lastChangeGlideAction = System.currentTimeMillis()
    }

    fun getToggleEatingTick(): Int {
        return this.eatFoodTick
    }

    fun consumeFoodInteract() {
        this.beforeLastConsumeFood = System.currentTimeMillis() - this.lastConsumeFood
        this.lastConsumeFood = System.currentTimeMillis()
    }

    fun getBeforeLastConsumeFood(): Long {
        return this.beforeLastConsumeFood
    }

    fun isFirstGagApple(): Boolean {
        return this.firstGagApple
    }

    fun setFirstGagApple(boolean: Boolean) {
        this.firstGagApple = boolean
    }

    fun getLastConsumeFood(): Long {
        return this.lastConsumeFood
    }

    fun getLastGlideBooster(): Long {
        return this.lastGlideBooster
    }

    fun getLadderTick(): Int {
        return this.ladderTick
    }

    fun getGroundTick(): Int {
        return this.groundTick
    }

    fun isEatFood(): Boolean {
        return this.eatFood
    }

    fun setEatFood(boolean: Boolean) {
        this.eatFood = boolean
    }

    fun getLastHealth(): Double {
        return this.lastHealth
    }

    fun setLastHealth(health: Float) {
        this.lastHealth = health.toDouble()
    }

    fun getFoodTracker(): EatPacketTracker? {
        return this.foodTracker
    }

    fun getLastTeleport(): Long {
        return this.lastTeleport
    }

    fun setTeleport() {
        this.lastTeleport = System.currentTimeMillis()
    }

    fun getFallDist(): Double {
        return this.fallDist
    }

    fun setSlimeBump(boolean: Boolean) {
        this.onSlimeBump = boolean
    }

    fun isOnSlimeBump(): Boolean {
        return this.onSlimeBump
    }

    fun getSlimeTick(): Double {
        return this.slimeTick / 20.0
    }

    fun getLostGround(): LostGround? {
        return this.lostGround
    }

    fun initLostGround(lostGround: LostGround) {
        this.lostGround = lostGround
    }

    fun isFallHurt(): Boolean {
        return this.fallHurt
    }

    fun setFallHurt(boolean: Boolean) {
        this.fallHurt = boolean
    }

}