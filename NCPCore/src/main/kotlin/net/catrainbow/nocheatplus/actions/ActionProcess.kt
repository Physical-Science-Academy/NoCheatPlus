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
package net.catrainbow.nocheatplus.actions

import cn.nukkit.Player
import cn.nukkit.network.protocol.DisconnectPacket
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.actions.types.BanAction
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.checks.ViolationData
import net.catrainbow.nocheatplus.checks.access.ACheckData
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.feature.wrapper.WrapperActionPacket
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent
import net.catrainbow.nocheatplus.feature.wrapper.WrapperSetBackPacket
import net.catrainbow.nocheatplus.utilities.NCPTimeTool

/**
 * 处罚进程
 *
 * @author Catrainbow
 */
class ActionProcess(
    private val player: Player,
    private val violationData: ViolationData,
    private val checkType: CheckType,
    private val actionType: ActionType,
) {

    private fun getActionType(): ActionType {
        return this.actionType
    }

    fun getCheckType(): CheckType {
        return this.checkType
    }

    fun getViolationData(): ViolationData {
        return this.violationData
    }

    fun forceDoAction(days: Int, hours: Int, minutes: Int) {
        val actionPacket = WrapperActionPacket(player)
        actionPacket.actionType = this.getActionType()
        val actionEvent = WrapperPacketEvent()
        actionEvent.player = player
        actionEvent.packet = actionPacket
        NoCheatPlus.instance.server.pluginManager.callEvent(actionEvent)
        if (actionEvent.isCancelled) return
        when (this.actionType) {

            ActionType.KICK -> {
                val disconnectPacket = DisconnectPacket()
                disconnectPacket.hideDisconnectionScreen = false
                disconnectPacket.message = this.formatMessage(ConfigData.string_kick_message)
                player.dataPacket(disconnectPacket)
            }

            ActionType.BAN -> {
                ACheckData.clear(player)
                val banAction = BanAction(days, hours, minutes)
                this.doBan(banAction)
                val disconnectPacket = DisconnectPacket()
                disconnectPacket.hideDisconnectionScreen = false
                disconnectPacket.message = this.formatMessage(ConfigData.string_kick_message)
                player.dataPacket(disconnectPacket)
            }

            else -> {}
        }
    }

    fun doAction(data: CheckActionData) {
        val actionPacket = WrapperActionPacket(player)
        actionPacket.actionType = this.getActionType()
        val actionEvent = WrapperPacketEvent()
        actionEvent.player = player
        actionEvent.packet = actionPacket
        NoCheatPlus.instance.server.pluginManager.callEvent(actionEvent)
        if (actionEvent.isCancelled) return
        val history = NoCheatPlus.instance.getPlayerProvider(this.getPlayer()).getViolationData(checkType).getHistory()
        when (this.actionType) {
            ActionType.SETBACK -> {
                if (this.violationData.getVL() < data.cancel || !data.enableCancel) return
                val setBack = NoCheatPlus.instance.getPlayerProvider(this.getPlayer()).getSetbackStorage()
                if (!setBack.isEmpty()) {
                    val setBackEntry = setBack.getFreeSetback()
                    NoCheatPlus.instance.getPlayerProvider(this.getPlayer()).getSetbackStorage().pop()
                    NoCheatPlus.instance.getPlayerProvider(this.getPlayer()).getViolationData(this.checkType)
                        .getHistory().setLastSetBack(setBackEntry)
                    if (setBackEntry.canLagBack()) {
                        val packet = WrapperSetBackPacket(this.getPlayer())
                        packet.target = setBackEntry.toLocation()
                        packet.checkType = this.checkType
                        val setBackEvent = WrapperPacketEvent()
                        setBackEvent.packet = packet
                        setBackEvent.player = player
                        NoCheatPlus.instance.server.pluginManager.callEvent(setBackEvent)
                        if (!setBackEvent.isCancelled) player.teleport((setBackEvent.packet as WrapperSetBackPacket).target)
                    }
                }
            }
            ActionType.LOG -> {
                if (this.violationData.getVL() < data.log || !data.enableLog) return
                if (System.currentTimeMillis() - history.getLastLog() > ConfigData.action_waring_delay * 1000) {
                    NoCheatPlus.instance.getPlayerProvider(player).getViolationData(checkType).getHistory()
                        .setLastLog(System.currentTimeMillis())
                    NoCheatPlus.instance.getNCPLogger().info("${player.name} failed $checkType Check")
                }
            }

            ActionType.WARING -> {
                if (this.violationData.getVL() < data.warn || !data.enableWarn) return
                if (System.currentTimeMillis() - history.getLastWaring() > ConfigData.action_waring_delay * 1000) {
                    NoCheatPlus.instance.getPlayerProvider(player).getViolationData(checkType).getHistory()
                        .setLastWaring(System.currentTimeMillis())
                    player.sendMessage(this.formatMessage(data.warnAction.message))
                }
            }

            ActionType.KICK -> {
                if (this.violationData.getVL() < data.kick || !data.enableKick) return
                ACheckData.plus(this.getPlayer())
                val disconnectPacket = DisconnectPacket()
                disconnectPacket.hideDisconnectionScreen = false
                disconnectPacket.message = this.formatMessage(ConfigData.string_kick_message)
                if (ACheckData.getBufferCount(player) >= data.banRepeat) {
                    if (data.enableBan) {
                        ACheckData.clear(player)
                        this.doBan(data.banAction)
                    }
                }
                player.dataPacket(disconnectPacket)
            }

            ActionType.BAN -> {
                if (ACheckData.getBufferCount(player) >= data.banRepeat) {
                    if (data.enableBan) {
                        ACheckData.clear(player)
                        this.doBan(data.banAction)
                    } else return
                }
            }

            else -> {}
        }
    }

    private fun doBan(action: BanAction) {
        var localDateTime = NCPTimeTool.nowTime
        localDateTime = NCPTimeTool.plusTimeByDays(localDateTime, action.days)
        localDateTime = NCPTimeTool.plusTimeByHours(localDateTime, action.hours)
        localDateTime = NCPTimeTool.plusTimeByMinute(localDateTime, action.minutes)
        val list: ArrayList<String> = ArrayList()
        list.add(this.checkType.name)
        list.add(NCPTimeTool.formatTime(localDateTime))
        NoCheatPlus.instance.getNCPLogger()
            .info("${player.name} was banned for $checkType.(to: ${NCPTimeTool.formatTime(localDateTime)})")
        val config = NoCheatPlus.instance.getNCPBanRecord()
        config.set(player.name, list)
        config.save(true)
    }

    private fun getPlayer(): Player {
        return NoCheatPlus.instance.server.getPlayer(this.player.name)
    }

    private fun formatMessage(string: String): String {
        return string.replace("@player", player.name).replace("@vl", this.violationData.getVL().toString())
            .replace("@hack", this.checkType.name).replace("@next", "\n")
    }

}