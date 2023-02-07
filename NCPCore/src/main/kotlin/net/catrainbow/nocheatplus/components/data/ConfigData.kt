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
package net.catrainbow.nocheatplus.components.data

/**
 * 配置文件储存
 */
class ConfigData : IConfigData {

    companion object {

        var config_version_notify = false
        var config_version_version = 1000
        var logging_active = false
        var logging_auto_delete_days = 1
        var logging_debug = false
        var logging_prefix = "§c§lNCP §7>> §r"
        var logging_command = false
        var logging_violation = false
        var action_waring_delay = 10
        var action_kick_broadcast = "§c§lNCP §7>>@player has been kicked for @hack"
        var protection_command_hide_active = true
        var protection_command_hide_message = "§c§lNCP §7>> §rYou do not have permission to run this command."
        var protection_command_commands: ArrayList<String> = ArrayList()
        var permission_no_permission = "§c§lNCP §7>> §rYou do not have permission to run this command."
        var string_kick_message = "§c§lNCP §7>> §rYou are kicked by NCP because of using @hack on server"
        var string_ban_message =
            "§c§lNCP §7>> §rYou are banned by NCP for §c@days,@hours,@minutes§r because of using @hack @nextEndTime: @end"
        var check_survival_fly_set_back_fall_damage = false
        var check_survival_fly_set_back_void_to_void = false
        var check_survival_fly_strict_mode = false
    }

}