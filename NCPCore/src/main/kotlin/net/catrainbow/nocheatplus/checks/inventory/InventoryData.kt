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
package net.catrainbow.nocheatplus.checks.inventory

import net.catrainbow.nocheatplus.components.data.ICheckData

/**
 * 背包数据
 *
 * @author Catrainbow
 */
class InventoryData : ICheckData {

    private var eatFoodTick = 0
    private var eat = false
    private var lastEat = false
    private var toggleInventory = false
    private var lastClicked = System.currentTimeMillis()
    private var toggleInventoryTick = 0

    fun onUpdate() {
        if (eat) this.eatFoodTick++
        if (this.toggleInventory) this.toggleInventoryTick++ else this.toggleInventoryTick = 0

        this.lastEat = eat
    }

    fun resetEatTick() {
        this.eatFoodTick = 0
    }

    fun getEatFoodTick(): Int {
        return this.eatFoodTick
    }

    fun isEating(): Boolean {
        return this.eat
    }

    fun setEating(boolean: Boolean) {
        this.eat = boolean
    }

    fun onOpen() {
        this.toggleInventory = true
    }

    fun onClosed() {
        this.toggleInventory = false
    }

    fun getToggleInventory(): Boolean {
        return this.toggleInventory
    }

    fun onClicked() {
        this.lastClicked = System.currentTimeMillis()
    }

    fun getToggleInventoryTick(): Int {
        return this.toggleInventoryTick
    }

    fun getLastClicked(): Long {
        return this.lastClicked
    }

    fun getLastEating(): Boolean {
        return this.lastEat
    }

}