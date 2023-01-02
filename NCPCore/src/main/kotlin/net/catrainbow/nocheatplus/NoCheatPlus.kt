/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.catrainbow.nocheatplus

import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.TextFormat
import net.catrainbow.nocheatplus.components.NoCheatPlusAPI
import net.catrainbow.nocheatplus.components.registry.NCPComManager
import net.catrainbow.nocheatplus.components.registry.NCPComponent
import net.catrainbow.nocheatplus.feature.NCPListener
import net.catrainbow.nocheatplus.players.PlayerData

/**
 * NoCheatPlus 主类
 *
 * @author Catrainbow
 */
class NoCheatPlus : PluginBase(), NoCheatPlusAPI {

    companion object {
        lateinit var instance: NoCheatPlus
        const val PLUGIN_VERSION: String = "1.0.0"
    }

    private lateinit var ncpComManager: NCPComManager
    override fun onLoad() {
        instance = this
    }

    override fun onEnable() {
        this.logger.info("Loading NoCheatPlus $PLUGIN_VERSION...")
        this.ncpComManager = NCPComManager()
        this.ncpComManager.onEnabled()
        this.server.pluginManager.registerEvents(NCPListener(), this)
        this.logger.info("${TextFormat.YELLOW}NoCheatPlus loads successfully!")
    }

    override fun getNCPProvider(): NoCheatPlus {
        return instance
    }

    override fun getComManager(): NCPComManager {
        return this.ncpComManager
    }

    override fun getAllComponents(): HashMap<String, NCPComponent> {
        return this.getComManager().getComponents()
    }

    override fun getAllPlayerData(): HashMap<String, PlayerData> {
        return PlayerData.allPlayersData
    }

    override fun addComponents(components: NCPComponent) {
        this.getComManager().registerCom(components)
    }

}