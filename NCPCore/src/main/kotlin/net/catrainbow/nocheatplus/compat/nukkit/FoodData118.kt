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
package net.catrainbow.nocheatplus.compat.nukkit

import cn.nukkit.item.Item

/**
 * 食物数据
 *
 * 更新到1.18
 *
 * @author Catrainbow
 */
object FoodData118 {

    const val DEFAULT_EAT_TICK = 14


    private val FOOD_LIST: IntArray = intArrayOf(
        Item.POTATOES, Item.BAKED_POTATOES,
        Item.BEETROOT, Item.BEETROOT_SOUP,
        Item.CARROTS, Item.GOLDEN_CARROT,
        Item.APPLE, Item.GOLDEN_APPLE, Item.GOLDEN_APPLE_ENCHANTED,
        Item.RAW_BEEF, Item.COOKED_BEEF,
        Item.COOKED_CHICKEN, Item.RAW_CHICKEN,
        Item.ROTTEN_FLESH,
        Item.RAW_RABBIT, Item.COOKED_RABBIT,
        Item.RAW_MUTTON, Item.COOKED_MUTTON,
        Item.RAW_PORKCHOP, Item.COOKED_PORKCHOP,
        Item.COOKED_FISH, Item.RAW_FISH,
        Item.PUFFERFISH,
        Item.COOKIE
    )

    fun isFood(item: Int): Boolean {
        return FOOD_LIST.contains(item)
    }

}