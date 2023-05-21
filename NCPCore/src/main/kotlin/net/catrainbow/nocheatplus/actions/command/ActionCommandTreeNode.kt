package net.catrainbow.nocheatplus.actions.command

import cn.nukkit.Player
import net.catrainbow.nocheatplus.NoCheatPlus

class ActionCommandTreeNode {

    lateinit var command: String
    lateinit var nextNode: ActionCommandTreeNode
    var violation = 0.0
    var enableBranchCommand = false

    fun getLength(index: Int): Int {
        return if (enableBranchCommand) this.nextNode.getLength(index + 1)
        else index + 1
    }

    fun dispatchNode(player: String, index: Int, target: Int, type: String) {
        if (index > target) return
        val dispatchCommand = this.command.replace("@player", player).replace("@type", type)
        NoCheatPlus.instance.server.dispatchCommand(NoCheatPlus.instance.server.consoleSender, dispatchCommand)
        return this.nextNode.dispatchNode(player, index + 1, target, type)
    }

    fun cutNode() {
        if (this.enableBranchCommand) {
            this.enableBranchCommand = false
            this.command = this.nextNode.command
            this.violation = this.nextNode.violation
        }
    }

    fun setEnabledBranch() {
        this.enableBranchCommand = true
    }

    fun getIndexNode(index: Int, target: Int): ActionCommandTreeNode {
        return if (index + 1 >= target) this
        else this.nextNode.getIndexNode(index + 1, target)
    }

}