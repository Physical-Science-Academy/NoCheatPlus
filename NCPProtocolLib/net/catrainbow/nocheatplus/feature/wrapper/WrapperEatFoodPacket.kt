package net.catrainbow.nocheatplus.feature.wrapper

import cn.nukkit.Player
import cn.nukkit.item.food.Food
import kotlin.properties.Delegates

class WrapperEatFoodPacket(player: Player) : WrapperPacket(player) {

    lateinit var food: Food
    var eat by Delegates.notNull<Boolean>()

}