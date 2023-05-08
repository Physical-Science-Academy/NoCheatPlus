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
package net.catrainbow.nocheatplus.compat;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockSlime;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.scheduler.Task;
import net.catrainbow.nocheatplus.checks.CheckType;
import net.catrainbow.nocheatplus.checks.moving.MovingData;
import net.catrainbow.nocheatplus.feature.wrapper.*;
import net.catrainbow.nocheatplus.players.PlayerData;

import java.util.HashMap;

public class PluginListener implements Listener {

    public static HashMap<String, String> playerMoveRecord = new HashMap<>();
    public static HashMap<String, Long> playerStepInRecord = new HashMap<>();

    @EventHandler
    public void playerMoves(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!playerMoveRecord.containsKey(player.getName())) {
            if (player.add(0.0, -1.0, 0.0).getLevelBlock() instanceof BlockSlime) {
                if (CompatNCP.settings.get("slimeJump")) playerMoveRecord.put(player.getName(), player.getName());
            }
        } else {
            PlayerData playerData = CompatNCP.provider.getPlayerProvider(player);
            MovingData data = playerData.getMovingData();
            if (data.getLastMotionY() <= 0.0 || (data.getGroundTick() > 25 && !data.isJump()))
                playerMoveRecord.remove(player.getName());
        }
    }

    @EventHandler
    public void onPlayerJoins(PlayerJoinEvent event) {
        if (CompatNCP.settings.get("waterDogStepIn")) {
            Player player = event.getPlayer();
            if (CompatNCP.provider.hasPlayer(player)) {
                playerStepInRecord.put(player.getName(), System.currentTimeMillis());
                Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
                    @Override
                    public void onRun(int i) {
                        playerStepInRecord.remove(player.getName());
                    }
                }, 20 * 5);
            }
        }
    }

    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event) {
        if (!CompatNCP.settings.get("booster")) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            playerMoveRecord.put(player.getName(), player.getName());
        }
    }

    @EventHandler
    public void onWrapperEvents(WrapperPacketEvent event) {
        WrapperPacket packet = event.getPacket();
        Player player = event.getPlayer();
        PlayerData playerData = CompatNCP.provider.getPlayerProvider(player);
        if (CompatNCP.settings.get("waterDogStepIn")) {
            if (playerStepInRecord.containsKey(player.getName())) {
                if (packet instanceof WrapperActionPacket || packet instanceof WrapperDisconnectPacket || packet instanceof WrapperSetBackPacket) {
                    event.setInvalid();
                    event.setCancelled();
                    return;
                }
                if (packet instanceof WrapperInputPacket)
                    CompatNCP.provider.getPlayerProvider(player).getViolationData(CheckType.MOVING_MORE_PACKETS).clear();
            }
        }
        if (CompatNCP.settings.get("slimeJump")) {
            if (playerMoveRecord.containsKey(player.getName())) {
                if (packet instanceof WrapperSetBackPacket) {
                    playerData.getViolationData(CheckType.MOVING_SURVIVAL_FLY).setCancel();
                    event.setInvalid();
                    return;
                }
            }
        }
        if (CompatNCP.settings.get("allowFlight")) {
            if (player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT)) {
                playerData.getViolationData(CheckType.MOVING_SURVIVAL_FLY).setCancel();
                playerData.getViolationData(CheckType.MOVING_SURVIVAL_FLY).clear();
                if (packet instanceof WrapperSetBackPacket) {
                    event.setInvalid();
                    return;
                }
            }
        }
        if (CompatNCP.settings.get("ignorePacket")) {
            if (event.packet instanceof WrapperDisconnectPacket) {
                if (((WrapperDisconnectPacket) event.packet).getReason() == CheckType.UNKNOWN_PACKET) {
                    ((WrapperDisconnectPacket) event.packet).setCancelled();
                    event.setInvalid();
                }
            }
        }
    }

}
