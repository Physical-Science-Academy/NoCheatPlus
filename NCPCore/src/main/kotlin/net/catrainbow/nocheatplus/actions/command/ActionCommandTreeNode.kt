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

import net.catrainbow.nocheatplus.NoCheatPlus
import java.util.*

class ActionCommandTreeNode {

    var command: String = "None"
    var leftNode: ActionCommandTreeNode? = null
    var rightNode: ActionCommandTreeNode? = null
    var violation = 0.0
    var enableBranchCommand = false

    fun getLength(): Int {
        return this.getDeepLength(this)
    }

    private fun getDeepLength(root: ActionCommandTreeNode?): Int {
        if (root == null) return 0
        return if (root.leftNode == null && root.rightNode == null) {
            1
        } else getDeepLength(root.leftNode) + getDeepLength(root.rightNode)
    }


    fun getNumberTreeNodes(root: ActionCommandTreeNode?): Int {
        if (root == null) return 0
        val left = getNumberTreeNodes(root.leftNode)
        val right = getNumberTreeNodes(root.rightNode)
        return left + right + 1
    }

    fun searchActionNode(index: Int, right: Boolean): ActionCommandTreeNode? {
        if (index == 1) return this
        return if (right) if (this.rightNode != null) this.searchActionNode(index - 1, right) else null
        else if (this.leftNode != null) this.searchActionNode(index - 1, right) else null
    }


    fun dispatchNode(player: String, type: String) {
        val root = this
        var cur: ActionCommandTreeNode
        var pre: ActionCommandTreeNode? = null
        val stack: Stack<ActionCommandTreeNode> = Stack()
        stack.push(root)
        while (!stack.empty()) {
            cur = stack.peek()
            if (cur.leftNode == null && cur.rightNode == null || pre != null && (pre === cur.leftNode || pre === cur.rightNode)) {

                val dispatchCommand = cur.command.replace("@player", player).replace("@type", type)
                if (dispatchCommand != "None") NoCheatPlus.instance.server.dispatchCommand(
                    NoCheatPlus.instance.server.consoleSender, dispatchCommand
                )

                stack.pop()
                pre = cur
            } else {
                if (cur.rightNode != null) stack.push(cur.rightNode)
                if (cur.leftNode != null) stack.push(cur.leftNode)
            }
        }

    }

    private fun getIndexNode(root: ActionCommandTreeNode?, target: Int, right: Boolean): ActionCommandTreeNode? {
        if (root == null) return null
        return if (target > 0) this.getIndexNode(
            root, target - 1, right
        ) else if (right) root.rightNode else root.leftNode
    }

}