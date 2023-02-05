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
package net.catrainbow.nocheatplus.bridge;

import com.nukkitx.protocol.bedrock.packet.TextPacket;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.plugin.Plugin;
import dev.waterdog.waterdogpe.scheduler.Task;

/**
 * WaterDog和NCP通信,群组服需要安装
 *
 * @author Catrainbow
 */
public class NCPWaterDogBridge extends Plugin {
    @Override
    public void onEnable() {
        this.getProxy().getScheduler().scheduleRepeating(new Task() {
            @Override
            public void onRun(int i) {
                for (ProxiedPlayer player : getProxy().getPlayers().values()) {
                    TextPacket textPacket = new TextPacket();
                    textPacket.setType(TextPacket.Type.SYSTEM);
                    //发送消息
                    textPacket.setMessage("PONG:" + player.getPing());
                    player.sendPacket(textPacket);
                }
            }

            @Override
            public void onCancel() {

            }
        }, 20);
    }
}
