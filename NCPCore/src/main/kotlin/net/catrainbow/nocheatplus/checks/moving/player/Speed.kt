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

import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.Check
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.moving.MovingData
import net.catrainbow.nocheatplus.checks.moving.model.SpeedTracker
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent

/**
 * Checks if a player is faster than he should be allowed to.
 *
 * @author Verox001
 */
class Speed : Check("checks.moving.speed", CheckType.MOVING_SPEED) {
    override fun onCheck(event: WrapperPacketEvent) {
        val player = event.player
        if (player.gamemode == 1 || player.gamemode == 3) return

        val playerData = NoCheatPlus.instance.getPlayerProvider(player)

        val data = playerData.movingData

        if (!data.isSafeSpawn() || !data.isLive()) return
        if (data.getRespawnTick() > 0) return
        if (ConfigData.check_survival_fly_set_back_void_to_void && data.isVoidHurt()) return

        val packet = event.packet
        if (packet is WrapperInputPacket)
            this.checkPlayerSpeed(data)
    }

    fun checkPlayerSpeed(data: MovingData) {
        if (data.getSpeedTracker() == null) {
            NoCheatPlus.instance.logger.info("Null")
            return
        }
        NoCheatPlus.instance.logger.info("Max Speed: ${data.getSpeedTracker()!!.getMaxSpeed()}")
        NoCheatPlus.instance.logger.info("Is Live: ${data.getSpeedTracker()!!.isLive()}")
    }
}