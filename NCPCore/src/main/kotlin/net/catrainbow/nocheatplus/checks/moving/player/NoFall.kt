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
import cn.nukkit.level.Location
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.MovingData
import net.catrainbow.nocheatplus.checks.moving.magic.Magic
import net.catrainbow.nocheatplus.compat.Bridge118.Companion.onGround
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.players.IPlayerData
import kotlin.math.max

/**
 * 无掉落伤害检测
 *
 * @author Catrainbow
 */
class NoFall : Check("checks.moving.nofall", CheckType.MOVING_NO_FALL) {

    override fun onCheck(event: WrapperPacketEvent) {
        val player = event.player
        if (player.gamemode == 1 || player.gamemode == 3) return
        val pData = NoCheatPlus.instance.getPlayerProvider(player)
        val data = pData.movingData
        if (!data.isSafeSpawn() || !data.isLive()) return
        if (data.getRespawnTick() > 0) return
        if (player.riding != null) return
        if (data.isVoidHurt()) return
        val packet = event.packet
        if (packet is WrapperInputPacket) {
            if (packet.serverOnGround || packet.clientOnGround) {
                val fromOnGround = packet.from.add(0.0, -0.3, 0.0).onGround()
                val toOnGround = packet.to.add(0.0, -0.3, 0.0).onGround()
                val now = System.currentTimeMillis()
                this.handleOnGround(
                    now, player, packet.from, packet.to, fromOnGround, toOnGround, data.getMotionY(), data,
                    pData
                )
            }
        }
    }

    /**
     * 处理玩家在地面上时的伤害,
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
     */
    private fun handleOnGround(
        now: Long,
        player: Player,
        from: Location,
        to: Location,
        fromOnGround: Boolean,
        toOnGround: Boolean,
        yDistance: Double,
        data: MovingData,
        pData: IPlayerData,
    ) {
        // Damage to be dealt
        val fallDist = data.getFallDist()
        val maxD = this.getDamage(fallDist)


    }

    /**
     * 计算伤害
     *
     * @return damage
     */
    private fun getDamage(fallDistance: Double): Double {
        return max(fallDistance - Magic.FALL_DAMAGE_DIST, 0.0)
    }

}