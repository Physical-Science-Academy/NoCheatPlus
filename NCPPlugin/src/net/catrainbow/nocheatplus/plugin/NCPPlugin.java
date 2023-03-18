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
package net.catrainbow.nocheatplus.plugin;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import net.catrainbow.nocheatplus.NoCheatPlus;
import net.catrainbow.nocheatplus.actions.ActionType;
import net.catrainbow.nocheatplus.components.NoCheatPlusAPI;
import net.catrainbow.nocheatplus.feature.wrapper.WrapperDisconnectPacket;
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacket;
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent;

/**
 * @author Catrainbow
 */
public class NCPPlugin extends PluginBase implements Listener {

    public static NoCheatPlusAPI provider = null;

    @Override
    public void onEnable() {
        if (this.getServer().getPluginManager().getPlugin("NoCheatPlus") == null) {
            this.getLogger().info("Can not find plugin NoCheatPlus!");
            return;
        }
        provider = NoCheatPlus.instance;
        this.getLogger().info("§e[NCPPlugin] §dHook NoCheatPlus successfully!");
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onWrapperPacketEvents(WrapperPacketEvent event) {
        Player player = event.getPlayer();
        WrapperPacket packet = event.getPacket();
        if (packet instanceof WrapperDisconnectPacket)
            if (((WrapperDisconnectPacket) packet).getType() == ActionType.KICK) {
                ((WrapperDisconnectPacket) packet).setCancelled();
                player.sendTitle("§cYou are kicked by NCP", "§8Reason: " + ((WrapperDisconnectPacket) packet).getReason(), 2, 100, 2);
                player.sendMessage("§e[NCPPlugin] §bYou have been kicked because of hacking but I prevent you from kicking!");
                player.sendMessage("§e[NCPPlugin] §aNow all your violations are clear.");
                provider.clearAllViolations(player);
            }
    }

}
