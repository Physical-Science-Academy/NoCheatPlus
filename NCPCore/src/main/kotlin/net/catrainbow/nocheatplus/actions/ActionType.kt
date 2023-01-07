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

/**
 * 处罚操作
 *
 * @author Catrainbow
 */
enum class ActionType(private val type: String) {
    WARING("WARING"),
    SETBACK("SETBACK"),
    KICK("KICK"),
    BAN("BAN"),
    LOG("LOG"),
    DEFAULT("DEFAULT");

    companion object {
        @JvmStatic
        fun fromTypeName(string: String): ActionType {
            for (type in ActionType.values()) {
                if (type.type == string)
                    return type
            }
            return DEFAULT
        }
    }

}