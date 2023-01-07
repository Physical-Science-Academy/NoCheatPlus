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
package net.catrainbow.nocheatplus.checks.moving.location.setback

/**
 * 拉回玩家队列
 *
 * @author Catrainbow
 */
class SetbackStorage {

    private var setbackEntries: ArrayList<SetBackEntry> = ArrayList()
    private var defaultIndex = 0

    fun push(entry: SetBackEntry) {
        this.setbackEntries.add(entry)
    }

    fun isEmpty(): Boolean {
        return this.setbackEntries.isEmpty()
    }

    fun getFreeSetback(): SetBackEntry {
        return this.setbackEntries[defaultIndex]
    }

    fun pop() {
        if (!this.isEmpty()) {
            setbackEntries.removeAt(defaultIndex)
        }
    }

}