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
package net.catrainbow.nocheatplus.logging

import net.catrainbow.nocheatplus.components.registry.INCPComponent
import net.catrainbow.nocheatplus.components.registry.NCPComponent

/**
 * NCP 日志记录
 *
 * @author Catrainbow
 */
class NCPLoggerCom : NCPComponent(), INCPComponent {

    private lateinit var logger: NCPLogger

    override fun onEnabled() {
        this.getRegisterCom().setName("NCP Logger")
        this.getRegisterCom().setAuthor("Catrainbow")
        this.getRegisterCom().setVersion("1.0.0")
        this.logger = NCPLogger()
    }

    fun getLogger(): NCPLogger {
        return this.logger
    }

}