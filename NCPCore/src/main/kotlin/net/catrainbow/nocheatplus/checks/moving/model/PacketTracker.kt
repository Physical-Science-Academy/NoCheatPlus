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

import cn.nukkit.network.protocol.DataPacket
import cn.nukkit.network.protocol.MovePlayerPacket
import cn.nukkit.network.protocol.PlayerAuthInputPacket
import net.catrainbow.nocheatplus.checks.moving.MovingData

/**
 * 数据包追踪器
 *
 * @author Catrainbow
 */
class PacketTracker(val data: MovingData) {

    private var tick = 0
    private var isLive = true
    private var count = 0

    private var maxCount = 0
    private var sumCount = 0
    private var tickSum = 0

    fun onUpdate() {
        if (isLive) {
            this.tick++
            this.tickSum++
            if (tick >= 20) this.kill()
        }
        //防止数据包过载,上溢
        if (sumCount > 20 * 20) this.resetSum()
    }

    fun onPacketReceive(packet: DataPacket) {
        var countPacket = false
        if (this.isLive())
            if (packet is MovePlayerPacket) countPacket = true
            else if (packet is PlayerAuthInputPacket) {
                //忽略静默状态下的发包
                if (data.getSpeed() > 0.21) countPacket = true
            }
        if (countPacket) {
            this.count++
            this.sumCount++
        }
    }

    fun run() {
        this.isLive = true
        this.count = 0
        this.tick = 0
        this.maxCount = 0
    }

    fun isLive(): Boolean {
        return this.isLive
    }

    fun resetSum() {
        this.sumCount = 0
        this.tickSum = 0
    }

    fun getCount(): Int {
        return if (this.tick == 20) this.count else 0
    }

    fun kill() {
        if (this.count > this.maxCount) this.maxCount = this.count
        this.isLive = false
    }

    fun getMaxCount(): Int {
        return this.maxCount
    }

    fun getAverage(): Double {
        return this.sumCount.toDouble() / (this.tickSum.toDouble() / 20.0)
    }

}