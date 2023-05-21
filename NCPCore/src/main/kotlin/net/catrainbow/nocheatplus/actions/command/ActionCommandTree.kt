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

import net.catrainbow.nocheatplus.components.data.ConfigData
import javax.swing.Action

class ActionCommandTree {

    lateinit var commandName: String
    var violation = 0.0
    private lateinit var nextNode: ActionCommandTreeNode
    var lastDoAction = System.currentTimeMillis() - ConfigData.action_warning_delay * 1000L

    private fun getLength(): Int {
        return nextNode.getLength(0)
    }

    private fun getActionTreeNode(target: Int): ActionCommandTreeNode {
        return this.nextNode.getIndexNode(0, target)
    }

    private fun getAllNodes(): ArrayList<ActionCommandTreeNode> {
        val list: ArrayList<ActionCommandTreeNode> = ArrayList()
        var index = 0
        while (index < this.getLength()) {
            list.add(this.getActionTreeNode(index))
            index++
        }
        return list
    }

    fun addNode(node: ActionCommandTreeNode) {
        val lastNode = this.getActionTreeNode(this.getLength()).nextNode
        lastNode.nextNode = node
        lastNode.setEnabledBranch()
    }

    fun graftTree(old: ActionCommandTree, new: ActionCommandTree): ActionCommandTree {
        val lengthOld = old.getLength()
        val lengthNew = new.getLength()
        if (lengthOld == 0) return old
        if (lengthNew == 0) return new
        val baseTree = if (lengthOld >= lengthNew) old else new
        val appendTree = if (baseTree.commandName == old.commandName) old else new
        var usedNode = baseTree.nextNode
        while (usedNode.enableBranchCommand) {
            for (node in appendTree.getAllNodes()) if (usedNode.command == node.command) node.cutNode()
            usedNode = usedNode.nextNode
        }
        baseTree.addNode(appendTree.nextNode)
        return baseTree
    }

}