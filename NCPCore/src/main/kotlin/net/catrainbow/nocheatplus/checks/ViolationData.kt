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
package net.catrainbow.nocheatplus.checks

import cn.nukkit.Player
import cn.nukkit.level.Location
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.actions.ActionFactory
import net.catrainbow.nocheatplus.actions.ActionHistory
import net.catrainbow.nocheatplus.actions.ActionProcess
import net.catrainbow.nocheatplus.actions.ActionType
import net.catrainbow.nocheatplus.checks.moving.location.setback.SetBackEntry
import net.catrainbow.nocheatplus.components.data.ConfigData
import java.util.Vector
import kotlin.math.max

/**
 * Violation Level 数据
 *
 * @author Catrainbow
 */
class ViolationData(type: CheckType, private val player: Player) {

    private val checkType = type

    private var vl = 0.0

    private var willCancel = false

    private var addedVL = 0.0

    private var multiply = 1.0

    private var actions: Vector<ActionProcess> = Vector()

    private var history: ActionHistory = ActionHistory()

    private val preLogger: HashMap<String, Int> = HashMap()

    fun getVL(): Double {
        return this.vl
    }

    private fun clearBuffer() {
        this.addedVL = 0.0
        this.multiply = 1.0
    }

    fun addVL(vl: Double) {
        this.addedVL += max(0.0, vl)
    }

    private fun clearBufferPublic() {
        this.willCancel = false
        this.addedVL = 0.0
    }

    fun clear() {
        this.vl = 0.0
        this.clearBufferPublic()
        this.clearBuffer()
        this.preLogger.clear()
    }

    fun setCancel() {
        this.willCancel = true
    }

    fun preVL(buffer: Double) {
        this.multiply = buffer
    }

    fun update() {
        if (this.addedVL != 0.0) {
            if (!willCancel) {
                this.vl += this.addedVL
                if (ConfigData.logging_debug) NoCheatPlus.instance.server.broadcastMessage("${ConfigData.logging_prefix}${this.player.name} failed ${this.checkType.name} vl: ${this.addedVL} total: ${this.vl}")
            }
        } else {
            this.vl *= this.multiply
        }
        this.clearBuffer()
        this.executeAction()
        this.willCancel = false
    }

    fun setLagBack(loc: Location) {
        val action = ActionFactory(player, this, ActionType.SETBACK).build()
        this.actions.add(action)
        NoCheatPlus.instance.getPlayerProvider(this.player.name).getSetbackStorage().push(SetBackEntry(loc, 0))
    }

    fun getHistory(): ActionHistory {
        return this.history
    }

    fun isCheat(): Boolean {
        return this.addedVL != 0.0
    }

    private fun executeAction() {
        if (!ActionFactory.actionDataMap.containsKey(this.checkType.name)) return
        val data = ActionFactory.actionDataMap[this.checkType.name]
        if (actions.size < 5) {
            var revert = false
            if (data!!.enableKick) if (this.vl > data.kick) {
                this.actions.addElement(
                    ActionFactory(
                        player, this, ActionType.KICK
                    ).build()
                )
                revert = true
            }
            if (data.enableWarn) if (this.history.canWarn() && !revert) if (this.vl > data.warn) {
                this.actions.addElement(
                    ActionFactory(
                        player, this, ActionType.WARING
                    ).build()
                )
                revert = true
            }
            if (data.enableLog) if (this.history.canLog() && !revert) if (this.vl > data.log) {
                this.actions.addElement(
                    ActionFactory(
                        player, this, ActionType.LOG
                    ).build()
                )
            }
        }
        if (actions.isEmpty()) return
        val action = actions.firstElement()
        val checkType = action.getCheckType()
        val actionData = ActionFactory.actionDataMap[checkType.name]!!
        action.doAction(actionData)
        this.actions.remove(action)
    }

    fun getCheckType(): CheckType {
        return this.checkType
    }

    fun addAction(actionProcess: ActionProcess) {
        this.actions.addElement(actionProcess)
    }

    fun addPreVL(name: String) {
        if (!preLogger.containsKey(name)) preLogger[name] = 0
        preLogger[name] = preLogger[name]!! + 1
    }

    fun clearPreVL(name: String) {
        preLogger[name] = 0
    }

    fun getPreVL(name: String): Int {
        if (!preLogger.containsKey(name)) preLogger[name] = 0
        return preLogger[name]!!
    }

}