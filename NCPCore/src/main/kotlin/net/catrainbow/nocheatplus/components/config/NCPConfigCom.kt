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
package net.catrainbow.nocheatplus.components.config

import cn.nukkit.utils.Config
import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.components.data.ConfigData
import net.catrainbow.nocheatplus.components.registry.INCPComponent
import net.catrainbow.nocheatplus.components.registry.NCPComponent

class NCPConfigCom : NCPComponent(), INCPComponent {
    private lateinit var config: Config

    override fun onEnabled() {
        this.getRegisterCom().setName("NCP Config")
        this.getRegisterCom().setVersion("1.0.0")
        this.getRegisterCom().setAuthor("Catrainbow")
        NoCheatPlus.instance.saveResource("ncpconfig.yml")
        val config = this.getNCPConfig()
        this.config = config
        this.inputConfig()
    }

    private fun getNCPConfig(): Config {
        return Config("${NoCheatPlus.instance.dataFolder}/ncpconfig.yml", Config.YAML)
    }

    fun reload() {
        this.inputConfig()
    }

    fun writeConfig() {
        config.set("config-version.notify", ConfigData.config_version_notify)
        config.set("config-version.version", ConfigData.config_version_version)
        config.set("logging.active", ConfigData.logging_active)
        config.set("auto-delete-days", ConfigData.logging_auto_delete_days)
        config.set("logging.extended.command", ConfigData.logging_command)
        config.set("logging.extended.violation", ConfigData.logging_violation)
        config.set("logging.debug", ConfigData.logging_debug)
        config.set("logging.prefix", ConfigData.logging_prefix)
        config.set("actions.waring_delay", ConfigData.action_waring_delay)
        config.set("actions.kick_broadcast", ConfigData.action_kick_broadcast)
        config.set("protection.command.hide.active", ConfigData.protection_command_hide_active)
        config.set("protection.command.hide.message", ConfigData.protection_command_hide_message)
        config.set("protection.command.hide.commands", ConfigData.protection_command_commands)
        config.set("string.kick", ConfigData.string_kick_message)
        config.set("string.ban", ConfigData.string_ban_message)
        config.set("permission.no_permission", ConfigData.permission_no_permission)
        config.set("checks.moving.survivalfly.setback_policy.fall_damage", ConfigData.check_survival_fly_set_back_fall_damage)
        config.set("checks.moving.survivalfly.setback_policy.void_to_void", ConfigData.check_survival_fly_set_back_void_to_void)
        config.set("checks.moving.survivalfly.setback_policy.latency_protection", ConfigData.check_survival_fly_latency_protection)
        config.set("checks.moving.survivalfly.strict_mode", ConfigData.check_survival_fly_strict_mode)
        config.set("checks.moving.nofall.dealdamage", ConfigData.check_no_fall_deal_damage)
        config.set("checks.moving.nofall.skipallowflight", ConfigData.check_no_fall_skip_allow_flight)
        config.set("checks.moving.nofall.resetonviolation", ConfigData.check_no_fall_reset_violation)
        config.set("checks.moving.nofall.resetonteleport", ConfigData.check_no_fall_reset_on_teleport)
        config.set("checks.moving.nofall.resetonvehicle", ConfigData.check_no_fall_reset_vehicle)
        config.set("checks.moving.nofall.anticriticals", ConfigData.check_no_fall_anti_critical)
        config.set("checks.inventory.fastclick.delay", ConfigData.check_inventory_fast_click_delay)
        config.save()
    }


    private fun inputConfig() {
        ConfigData.config_version_notify = config.getBoolean("config-version.notify")
        ConfigData.config_version_version = config.getInt("config-version.version")
        ConfigData.logging_active = config.getBoolean("logging.active")
        ConfigData.logging_auto_delete_days = config.getInt("auto-delete-days")
        ConfigData.logging_command = config.getBoolean("logging.extended.command")
        ConfigData.logging_violation = config.getBoolean("logging.extended.violation")
        ConfigData.logging_debug = config.getBoolean("logging.debug")
        ConfigData.logging_prefix = config.getString("logging.prefix")
        ConfigData.action_waring_delay = config.getInt("actions.waring_delay")
        ConfigData.action_kick_broadcast = config.getString("actions.kick_broadcast")
        ConfigData.protection_command_hide_active = config.getBoolean("protection.command.hide.active")
        ConfigData.protection_command_hide_message = config.getString("protection.command.hide.message")
        ConfigData.protection_command_commands = config.getStringList("protection.command.hide.commands") as ArrayList
        ConfigData.string_kick_message = config.getString("string.kick")
        ConfigData.string_ban_message = config.getString("string.ban")
        ConfigData.permission_no_permission = config.getString("permission.no_permission")
        ConfigData.check_survival_fly_set_back_fall_damage =
            config.getBoolean("checks.moving.survivalfly.setback_policy.fall_damage")
        ConfigData.check_survival_fly_set_back_void_to_void =
            config.getBoolean("checks.moving.survivalfly.setback_policy.void_to_void")
        ConfigData.check_survival_fly_latency_protection =
            config.getInt("checks.moving.survivalfly.setback_policy.latency_protection")
        ConfigData.check_survival_fly_strict_mode = config.getBoolean("checks.moving.survivalfly.strict_mode")
        ConfigData.check_no_fall_deal_damage = config.getBoolean("checks.moving.nofall.dealdamage")
        ConfigData.check_no_fall_skip_allow_flight = config.getBoolean("checks.moving.nofall.skipallowflight")
        ConfigData.check_no_fall_reset_violation = config.getBoolean("checks.moving.nofall.resetonviolation")
        ConfigData.check_no_fall_reset_on_teleport = config.getBoolean("checks.moving.nofall.resetonteleport")
        ConfigData.check_no_fall_reset_vehicle = config.getBoolean("checks.moving.nofall.resetonvehicle")
        ConfigData.check_no_fall_anti_critical = config.getBoolean("checks.moving.nofall.anticriticals")
        ConfigData.check_inventory_fast_click_delay = config.getLong("checks.inventory.fastclick.delay")
    }
}