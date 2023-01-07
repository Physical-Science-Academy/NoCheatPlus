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

/**
 * 作弊类型
 *
 * @author Catrainbow
 */
enum class CheckType(parent: CheckType?) {

    ALL(null),
    STAFF(ALL),
    MOVING(ALL),
    MOVING_SURVIVAL_FLY(MOVING);

    companion object {

        /**
         * 获得CheckType
         *
         * @return CheckType
         */
        @JvmStatic
        fun getTypeByName(name: String): CheckType {
            for (type in CheckType.values()) {
                if (type.toString() == name) {
                    return type
                }
            }
            return ALL
        }
    }

}