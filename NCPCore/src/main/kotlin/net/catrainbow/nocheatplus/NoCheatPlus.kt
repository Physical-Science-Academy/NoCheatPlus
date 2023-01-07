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

import cn.nukkit.Player
import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.Config
import cn.nukkit.utils.TextFormat
import net.catrainbow.nocheatplus.actions.ActionFactory
import net.catrainbow.nocheatplus.actions.ActionType
import net.catrainbow.nocheatplus.checks.CheckType
import net.catrainbow.nocheatplus.components.NoCheatPlusAPI
import net.catrainbow.nocheatplus.components.config.NCPBanConfig
import net.catrainbow.nocheatplus.components.config.NCPConfigCom
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.components.registry.NCPComManager
import net.catrainbow.nocheatplus.components.registry.NCPComponent
import net.catrainbow.nocheatplus.feature.NCPListener
import net.catrainbow.nocheatplus.logging.NCPLogger
import net.catrainbow.nocheatplus.logging.NCPLoggerCom
import net.catrainbow.nocheatplus.players.PlayerData
import net.catrainbow.nocheatplus.utilities.NCPTimeTool

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
        if (ConfigData.config_version_notify) {
            this.logger.info("你正在使用的配置文件版本为: ${ConfigData.config_version_version}")
        }
        this.logger.info("NoCheatPlus已自动删除${this.getNCPLogger().getDeleteCount()}个过期日志文件")
        this.logger.info("${TextFormat.YELLOW}NoCheatPlus loads successfully!")
        this.logger.info("${TextFormat.BLUE}开源地址: https://github.com/Physical-Science-Academy/NoCheatPlus/")
    }

    override fun onDisable() {
        this.ncpComManager.onDisabled()
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

    override fun hasPlayer(player: Player): Boolean {
        return this.hasPlayer(player.name)
    }

    override fun hasPlayer(player: String): Boolean {
        return PlayerData.allPlayersData.containsKey(player)
    }

    override fun getPlayerProvider(player: Player): PlayerData {
        return this.getPlayerProvider(player.name)
    }

    override fun getPlayerProvider(player: String): PlayerData {
        return PlayerData.allPlayersData[player]!!
    }

    override fun getNCPComponent(comName: String): NCPComponent {
        return this.getAllComponents()[comName]!!
    }

    override fun getNCPLogger(): NCPLogger {
        return (this.getNCPComponent("NCP Logger") as NCPLoggerCom).getLogger()
    }

    override fun getNCPConfig(): Config {
        return (this.getNCPComponent("NCP Config") as NCPConfigCom).getNCPConfig()
    }

    override fun getNCPBanRecord(): Config {
        return (this.getNCPComponent("NCP AutoBan") as NCPBanConfig).getRecord()
    }

    override fun isPlayerBan(player: Player): Boolean {
        if (!this.getNCPBanRecord().exists(player.name)) return false
        else {
            val now = NCPTimeTool.nowTime
            val end = NCPTimeTool.stringToTime(this.getNCPBanRecord().getStringList(player.name)[1])
            if (NCPTimeTool.canUnBan(now, end)) {
                val config = this.getNCPBanRecord()
                config.remove(player.name)
                config.save(true)
                return false
            } else return true
        }
    }

    override fun kickPlayer(player: Player, type: CheckType) {
        val data = this.getPlayerProvider(player)
        ActionFactory(player, data.getViolationData(CheckType.STAFF), ActionType.KICK).build().forceDoAction(0, 0, 0)
    }

    override fun banPlayer(player: Player, days: Int, hours: Int, minutes: Int) {
        val data = this.getPlayerProvider(player)
        ActionFactory(player, data.getViolationData(CheckType.STAFF), ActionType.BAN).build()
            .forceDoAction(days, hours, minutes)
    }

    override fun banPlayer(player: Player, days: Int, hours: Int) {
        this.banPlayer(player, days, hours, 0)
    }

    override fun banPlayer(player: Player, days: Int) {
        this.banPlayer(player, days, 0, 0)
    }

}
