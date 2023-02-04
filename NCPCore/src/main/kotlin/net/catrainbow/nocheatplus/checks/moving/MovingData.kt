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
import net.catrainbow.nocheatplus.checks.moving.magic.GhostBlockChecker
import net.catrainbow.nocheatplus.checks.moving.model.DistanceData
import net.catrainbow.nocheatplus.checks.moving.model.MoveTracker
import net.catrainbow.nocheatplus.checks.moving.model.SpeedTracker
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
    private var fullAirTick = 0
    private var liquidTick = 0
    private var iceTick = 0
    private var slabTick = 0
    private var stairTick = 0
    private var acc = 0.0
    private var sinceLastYChange = 0

    private var motionYList: ArrayList<Double> = ArrayList()
    private var locationList: ArrayList<Location> = ArrayList()
    private var speedList: ArrayList<Double> = ArrayList()
    private var ghostBlockChecker: GhostBlockChecker = GhostBlockChecker("NCP", Vector3(0.0, 0.0, 0.0), 0, 0)

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
        if (LocUtil.isLiquid(LocUtil.getUnderBlock(player)) || LocUtil.isLiquid(player.levelBlock)) this.liquidTick++ else if (this.liquidTick in 1..50) this.liquidTick-- else this.liquidTick =
            0
        if (LocUtil.isIce(LocUtil.getUnderBlock(player))) this.iceTick++
        else if (this.iceTick in 1..50) this.iceTick-- else this.iceTick = 0
        if (LocUtil.getUnderBlock(player) is BlockSlab) this.slabTick++ else if (this.slabTick in 1..50) this.slabTick-- else this.slabTick =
            0
        if (LocUtil.getUnderBlock(player) is BlockStairs) this.stairTick++ else if (this.stairTick in 1..50) this.stairTick-- else this.stairTick =
            0
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

    fun getIceTick(): Int {
        return this.iceTick
    }

    fun getSpeedTracker(): SpeedTracker? {
        return this.speedTracker
    }

    fun getStairTick(): Int {
        return this.stairTick
    }

    fun getSlabTick(): Int {
        return this.slabTick
    }

}