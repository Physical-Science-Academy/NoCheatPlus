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
package net.catrainbow.nocheatplus.checks.moving.model

import net.catrainbow.nocheatplus.checks.moving.MovingData
import net.catrainbow.nocheatplus.compat.nukkit.FoodData118
import kotlin.math.abs

/**
 * 食物追踪器
 *
 * @author Catrainbow
 */
class EatPacketTracker(val data: MovingData) {

    private var count = 0
    private var actionCount = 0
    private var isLive = false
    private var eat = false
    private var duringEating = false

    private var ticks = 0
    private var balanceTick = 0

    fun onUpdate() {
        if (this.isLive) {
            this.ticks++
            if (this.actionCount != 0) if (ticks != this.balanceTick) {
                if (abs(ticks - this.balanceTick) > 1) if (count < FoodData118.DEFAULT_EAT_TICK) {
                    this.kill()
                    data.setEatFood(false)
                }
            } else if (actionCount == 4) {
                this.duringEating = true
                data.setEatFood(true)
            } else if (actionCount == 8) {
                this.eat = true
                this.duringEating = false
                data.setEatFood(false)
                this.kill()
            } else if (actionCount < 1 || actionCount > 8) {
                this.kill()
                this.eat = false
                this.duringEating = false
                data.setEatFood(false)
            }
        }
    }

    fun onAction() {
        if (isLive) {
            this.actionCount++
            if (this.actionCount <= 8) this.balanceTick++
        }
    }

    fun addPacket() {
        if (duringEating && isLive) {
            this.count++
            this.balanceTick++
        }
    }

    fun kill() {
        this.isLive = false
    }

    fun run() {
        this.reset()
        this.isLive = true
    }

    fun isDuringEating(): Boolean {
        return this.duringEating
    }

    fun getCount(): Int {
        return if (this.count == 0) FoodData118.DEFAULT_EAT_TICK else this.count
    }

    private fun reset() {
        this.count = 0
        this.eat = false
        this.balanceTick = 0
        this.ticks = 0
        this.duringEating = false
    }

    fun isLive(): Boolean {
        return this.isLive
    }

}