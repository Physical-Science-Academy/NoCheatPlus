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

import net.catrainbow.nocheatplus.actions.types.BanAction
import net.catrainbow.nocheatplus.actions.types.LogAction
import net.catrainbow.nocheatplus.actions.types.WarnAction
import net.catrainbow.nocheatplus.components.data.ICheckData

class CheckActionData : ICheckData {

    var cancel = 0.0
    var enableCancel = false
    var log = 0.0
    lateinit var logAction: LogAction
    var enableLog = false
    var warn = 0.0
    lateinit var warnAction: WarnAction
    var enableWarn = false
    var kick = 0.0
    var enableKick = false
    var banRepeat = 1
    lateinit var banAction: BanAction
    var enableBan = false

}