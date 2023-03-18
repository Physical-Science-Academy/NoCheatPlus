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
package net.catrainbow.nocheatplus.checks.fight

import net.catrainbow.nocheatplus.components.data.ICheckData
import kotlin.math.max
import kotlin.math.pow


/**
 * 战斗数据
 *
 * @author Catrainbow
 */
class FightData : ICheckData {

    private var swingQueue = ArrayList<Long>()
    private var clickPerSecond = 0
    private var lastClickPerSecond = 0
    private var clickPerSecondList = ArrayList<Double>()
    private var clickPerSecondVariance = 1.0
    private var clickPerSecondInteract = 1

    var speedShortTermTick = 0
    var attackSoundBoost = System.currentTimeMillis() - 1000L
    var lastDamageBoost = System.currentTimeMillis() - 1000L
    var lastInteractBoost = ArrayList<Long>()
    var lastDealDamage = false

    fun onUpdate() {
        swingQueue.removeIf { l: Long -> l < System.currentTimeMillis() - 1000 }
        lastInteractBoost.removeIf { l: Long -> l < System.currentTimeMillis() - 1000 }
        this.lastClickPerSecond = clickPerSecond
        this.clickPerSecond = max(0, swingQueue.size * this.clickPerSecondInteract - lastInteractBoost.size * 8)
        this.clickPerSecondList.add(this.getClickPerSecond())
        var sumClickPerSecond = 0.0
        clickPerSecondList.forEach {
            sumClickPerSecond += it
        }
        val average = sumClickPerSecond / clickPerSecondList.size.toDouble()
        var preVariance = 0.0
        for (d in clickPerSecondList) {
            preVariance += (d - average).pow(2.0)
        }
        this.clickPerSecondVariance = preVariance / clickPerSecondList.size.toDouble()
        if (this.speedShortTermTick > 5 || clickPerSecondList.size > 20) {
            this.speedShortTermTick = 0
            this.clickPerSecondList.clear()
        }
    }

    fun getSwingQueue(): ArrayList<Long> {
        return this.swingQueue
    }

    fun updateSwingQueue(queue: ArrayList<Long>) {
        this.swingQueue = queue
    }

    fun getClickPerSecond(): Double {
        return (this.clickPerSecond + lastClickPerSecond) / 2.0
    }

    fun setClickPerSecondInteract(count: Int) {
        this.clickPerSecondInteract = count
    }

    fun getClickPerSecondVariance(): Double {
        return this.clickPerSecondVariance
    }

}