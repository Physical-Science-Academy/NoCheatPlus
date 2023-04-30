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
        var protection_net_packet = true
        var protection_net_chunk = true
        var protection_net_chunk_dynamic_scan = true
        var protection_net_chunk_scan_height = 16.0
        var protection_net_chunk_filter: ArrayList<Int> = ArrayList()
        var protection_net_chunk_ores: ArrayList<Int> = ArrayList()
        var permission_no_permission = "§c§lNCP §7>> §rYou do not have permission to run this command."
        var string_kick_message = "§c§lNCP §7>> §rYou are kicked by NCP because of using @hack on server"
        var string_ban_message =
            "§c§lNCP §7>> §rYou are banned by NCP for §c@days,@hours,@minutes§r because of using @hack @nextEndTime: @end"
        var check_survival_fly_set_back_fall_damage = false
        var check_survival_fly_set_back_void_to_void = false
        var check_survival_fly_latency_protection = 120
        var check_survival_fly_strict_mode = false
        var check_no_fall_deal_damage = true
        var check_no_fall_skip_allow_flight = true
        var check_no_fall_reset_violation = false
        var check_no_fall_reset_on_teleport = false
        var check_no_fall_reset_vehicle = true
        var check_no_fall_anti_critical = true
        var check_inventory_fast_click_delay = 50L
        var check_fight_max_speed = 25
        var check_fight_deal_variance = 0.1
        var check_fight_cancel_damage = true
        var check_fast_break_max = 35
        var check_fast_break_min = 0
    }

}