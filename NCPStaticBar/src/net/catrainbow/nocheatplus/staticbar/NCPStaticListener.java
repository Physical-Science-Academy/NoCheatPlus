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
package net.catrainbow.nocheatplus.staticbar;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import net.catrainbow.nocheatplus.feature.wrapper.*;

/**
 * 监听器
 *
 * @author Catrainbow
 */
public class NCPStaticListener implements Listener {

    @EventHandler
    public void onWrapperEvents(WrapperPacketEvent event) {
        WrapperPacket packet = event.getPacket();
        Player player = event.getPlayer();
        if (packet instanceof WrapperActionPacket || packet instanceof WrapperSetBackPacket || packet instanceof WrapperDisconnectPacket) {
            if (NCPStaticAPI.isPlayerChecked(player)) {
                event.setCancelled();
                event.setInvalid();
                if (packet instanceof WrapperDisconnectPacket) ((WrapperDisconnectPacket) packet).setCancelled();
            }
        }
    }

}
