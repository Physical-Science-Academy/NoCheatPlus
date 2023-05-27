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
package net.catrainbow.nocheatplus.actions.command

import cn.nukkit.Player
import net.catrainbow.nocheatplus.components.data.ConfigData


class ActionCommandTree {

    lateinit var commandName: String
    var violation = 0.0
    private var nextNode: ActionCommandTreeNode = ActionCommandTreeNode()
    var lastDoAction = System.currentTimeMillis() - ConfigData.action_warning_delay * 1000L

    fun dispatchAllCommand(player: Player, type: String) {
        this.dispatchAllCommand(player.name, type)
    }

    private fun dispatchAllCommand(player: String, type: String) {
        this.nextNode.dispatchNode(player, type)
    }

    private fun getLength(): Int {
        return nextNode.getLength()
    }

    fun getTotalCommands(): Int {
        return this.nextNode.getNumberTreeNodes(this.nextNode)
    }

    private fun getActionTreeNode(target: Int, right: Boolean): ActionCommandTreeNode? {
        return this.nextNode.searchActionNode(target, right)
    }

    private fun getAllNodes(): ArrayList<ActionCommandTreeNode> {
        val list: ArrayList<ActionCommandTreeNode> = ArrayList()
        var index = 0
        while (index < this.getLength()) {
            val left = this.getActionTreeNode(index, false)
            val right = this.getActionTreeNode(index, true)
            if (left != null) list.add(left)
            if (right != null) list.add(right)
            index++
        }
        return list
    }

    fun addNode(target: ActionCommandTreeNode) {
        var preNode = this.nextNode
        while (preNode.command != target.command) {
            if (preNode.leftNode == null) {
                preNode.leftNode = target
                break
            } else if (preNode.rightNode == null) {
                preNode.rightNode = target
                break
            } else if (preNode.leftNode!!.getLength() >= preNode.rightNode!!.getLength()) preNode =
                preNode.rightNode!! else preNode = preNode.leftNode!!
        }
    }

    fun graftTree(targetTree: ActionCommandTree): ActionCommandTree {
        val tree = this
        tree.nextNode = this.mergeTrees(this.nextNode, targetTree.nextNode)!!
        return tree
    }

    private fun mergeTrees(root1: ActionCommandTreeNode?, root2: ActionCommandTreeNode?): ActionCommandTreeNode? {
        if (root1 == null && root2 == null) return null
        if (root1 == null) return root2
        if (root2 == null) return root1
        val root = this.nextNode
        root.leftNode = mergeTrees(root1.leftNode, root2.leftNode)
        root.rightNode = mergeTrees(root1.rightNode, root2.rightNode)
        return root
    }

    override fun toString(): String {
        val builder = StringBuilder(
            "ActionCommandTree ${this.commandName}" + "\nTotal Nodes: ${this.getTotalCommands()}\n"
        )
        for (node in this.getAllNodes()) builder.append("- ${node.command}\n")
        return builder.toString()
    }

}