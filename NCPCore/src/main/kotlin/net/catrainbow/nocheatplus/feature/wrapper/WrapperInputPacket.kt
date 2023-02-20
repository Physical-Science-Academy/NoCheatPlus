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
package net.catrainbow.nocheatplus.feature.wrapper

import cn.nukkit.Player
import cn.nukkit.level.Location
import cn.nukkit.math.Vector3
import kotlin.properties.Delegates

/**
 * Player input data package
 *
 * Detected once per tick
 */
class WrapperInputPacket(player: Player) : WrapperPacket(player) {

    lateinit var rotation: Vector3
    lateinit var position: Vector3
    lateinit var motion: Vector3
    var inputMode by Delegates.notNull<Int>()
    var clientPlayMode by Delegates.notNull<Int>()
    lateinit var from: Location
    lateinit var to: Location
    var speed by Delegates.notNull<Double>()
    var clientOnGround by Delegates.notNull<Boolean>()
    var serverOnGround by Delegates.notNull<Boolean>()

}