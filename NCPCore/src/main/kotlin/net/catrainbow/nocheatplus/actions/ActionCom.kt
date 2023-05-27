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

import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.actions.command.ActionCommandTree
import net.catrainbow.nocheatplus.actions.command.ActionCommandTreeNode
import net.catrainbow.nocheatplus.actions.types.BanAction
import net.catrainbow.nocheatplus.actions.types.CommandAction
import net.catrainbow.nocheatplus.actions.types.LogAction
import net.catrainbow.nocheatplus.actions.types.WarnAction
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.components.registry.INCPComponent
import net.catrainbow.nocheatplus.components.registry.NCPComponent

/**
 * Action
 *
 * @author Catrainbow
 */
class ActionCom : NCPComponent(), INCPComponent {

    override fun onEnabled() {
        this.getRegisterCom().setName("NPC BanWave")
        this.getRegisterCom().setAuthor("Catrainbow")
        this.getRegisterCom().setVersion("1.0.0")

        //注册Action
        this.loadAction("moving.survivalfly", CheckType.MOVING_SURVIVAL_FLY.name)
        this.loadAction("moving.morepackets", CheckType.MOVING_MORE_PACKETS.name)
        this.loadAction("moving.creativefly", CheckType.MOVING_CREATIVE_FLY.name)
        this.loadAction("moving.nofall", CheckType.MOVING_NO_FALL.name)
        this.loadAction("moving.vehicle", CheckType.MOVING_VEHICLE.name)
        this.loadAction("inventory.instanteat", CheckType.INVENTORY_INSTANT_EAT.name)
        this.loadAction("inventory.move", CheckType.INVENTORY_MOVE.name)
        this.loadAction("inventory.open", CheckType.INVENTORY_OPEN.name)
        this.loadAction("inventory.fastclick", CheckType.INVENTORY_FAST_CLICK.name)
        this.loadAction("inventory.item", CheckType.INVENTORY_ITEM.name)
        this.loadAction("fight.speed", CheckType.FIGHT_SPEED.name)
        this.loadAction("fight.reach", CheckType.FIGHT_REACH.name)
        this.loadAction("blockbreak.fastbreak", CheckType.BLOCK_BREAK_FAST_BREAK.name)
    }

    private fun loadAction(path: String, type: String) {
        val config = NoCheatPlus.instance.getNCPConfig()
        val actionStr = config.getString("checks.${path}.actions")
        val subStr = actionStr.split("&&")
        val actionData = CheckActionData()
        for (command in subStr) {
            val subCommand = command.split(" ")
            when (subCommand[0]) {
                "cancel" -> {
                    actionData.enableCancel = true
                    actionData.cancel = subCommand[1].split(">")[1].toDouble()
                }
                "log" -> {
                    actionData.enableLog = true
                    actionData.logAction = LogAction()
                    actionData.log = subCommand[1].split(">")[1].toDouble()
                    if (subCommand.size >= 3) actionData.logAction.breakDelay = subCommand[2].split("=")[1].toInt()
                    else actionData.logAction.breakDelay = 10
                }
                "warn" -> {
                    actionData.enableWarn = true
                    actionData.warnAction = WarnAction()
                    actionData.warn = subCommand[1].split(">")[1].toDouble()
                    val baseStrName = subCommand[2].split("=")[1]
                    actionData.warnAction.message = NoCheatPlus.instance.getNCPConfig().getString("string.$baseStrName")
                }
                "kick" -> {
                    actionData.kick = subCommand[1].split(">")[1].toDouble()
                    actionData.enableKick = true
                }
                "ban" -> {
                    actionData.enableBan = true
                    actionData.banRepeat = subCommand[1].split("=")[1].toInt()
                    val timeArray = subCommand[2].split("=")[1].split(",")
                    actionData.banAction = BanAction(timeArray[0].toInt(), timeArray[1].toInt(), timeArray[2].toInt())
                }
                "cmd" -> {
                    actionData.enableCommand = true
                    actionData.commandAction = CommandAction()
                    val violation = subCommand[1].split(">")[1].toDouble()
                    val commandTree = ActionCommandTree()
                    if (subCommand[2].contains("group")) {
                        val groupName = subCommand[2].split("=")[1]
                        commandTree.commandName = groupName
                        commandTree.violation = violation
                        for (patchCommand in NoCheatPlus.instance.getNCPConfig().getStringList("command.$groupName")) {
                            val node = ActionCommandTreeNode()
                            node.command = patchCommand
                            node.violation = commandTree.violation
                            node.enableBranchCommand = true
                            commandTree.addNode(node)
                        }
                    } else {
                        commandTree.commandName = subCommand[2]
                        commandTree.violation = violation
                        val node = ActionCommandTreeNode()
                        node.command = commandTree.commandName
                        node.violation = commandTree.violation
                        node.enableBranchCommand = true
                        commandTree.addNode(node)
                    }
                    NoCheatPlus.instance.getNCPLogger().info("Print Tree:\n$commandTree")
                    if (!actionData.commandAction.commandTree.containsKey(type)) {
                        val pair: Pair<Double, ActionCommandTree> = Pair(commandTree.violation, commandTree)
                        actionData.commandAction.commandTree[type] = pair
                    } else {
                        val originalTree = actionData.commandAction.commandTree[type]!!.second
                        val pair: Pair<Double, ActionCommandTree> =
                            Pair(commandTree.violation, originalTree.graftTree(commandTree))
                        actionData.commandAction.commandTree[type] = pair
                    }
                }
            }
        }
        ActionFactory.actionDataMap[type] = actionData
    }

}