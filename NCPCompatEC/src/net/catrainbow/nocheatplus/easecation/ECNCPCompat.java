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
package net.catrainbow.nocheatplus.easecation;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import net.catrainbow.nocheatplus.NoCheatPlus;
import net.catrainbow.nocheatplus.actions.ActionType;
import net.catrainbow.nocheatplus.checks.CheckType;
import net.catrainbow.nocheatplus.components.NoCheatPlusAPI;
import net.catrainbow.nocheatplus.feature.wrapper.WrapperDisconnectPacket;
import net.catrainbow.nocheatplus.feature.wrapper.WrapperInputPacket;
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacket;
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent;

import java.util.HashMap;

/**
 * 模拟复现 EaseCation CPS检测系统
 *
 * @author Catrainbow
 */
public class ECNCPCompat extends PluginBase implements Listener {

    public static NoCheatPlusAPI provider = null;
    public static HashMap<String, Long> timeBalanceBase = new HashMap<>();
    public static HashMap<String, RecordData> playerRecordData = new HashMap<>();
    //是否关闭NCP自带连点器检测
    private static boolean cancelNCP = false;

    //检测冷却时间
    private static int BREAK_TIME = 10;
    //最大CPS限制
    private static double LIMITED_CPS = 20d;
    //踢出消息
    private static String KICK_MESSAGE = "";

    @Override
    public void onEnable() {
        this.getLogger().info("§e[NCPCompat] §b正在加载ECNCPCompat");
        if (this.getServer().getPluginManager().getPlugin("NoCheatPlus") == null) {
            this.getLogger().info("§e[NCPCompat] §c未检测到安装NoCheatPlus! 加载失败");
            return;
        }
        provider = NoCheatPlus.instance;
        this.getLogger().info("§e[NCPCompat] §b连接§cNCP§b成功");
        this.getServer().getPluginManager().registerEvents(this, this);

        Config config = new Config(this.getDataFolder() + "/config.yml", 2);
        if (!config.exists("break")) {
            config.set("break", 10);
            config.set("limited_cps", 20.0);
            config.set("cancel_ncp", true);
            config.set("broadcast", "§c§lADMIN§e频道§6<ECAntiHack#自动黑屋>§e:§d检测到玩家=@player 检测类型=持续高CPS 位置=@world 持续高CPS:1s:@cps1 5s:@cps5 10s:@cps10 15s:@cps15");
            config.save(true);
        }
        BREAK_TIME = config.getInt("break");
        LIMITED_CPS = config.getDouble("limited_cps");
        KICK_MESSAGE = config.getString("broadcast");
        cancelNCP = config.getBoolean("cancel_ncp");

        this.getServer().getScheduler().scheduleRepeatingTask(new Task() {
            @Override
            public void onRun(int i) {
                for (String playerName : playerRecordData.keySet()) {
                    RecordData data = playerRecordData.get(playerName);
                    if (data.tick >= 0) {
                        double cps = provider.getPlayerProvider(playerName).getFightData().getClickPerSecond();
                        data.tick++;
                        if (data.tick == 1) data.cpsRecord1 = cps;
                        if (data.tick == 5) data.cpsRecord5 = cps;
                        if (data.tick == 10) data.cpsRecord10 = cps;
                        if (data.tick == 15) {
                            data.cpsRecord15 = cps;
                            data.tick = -1;
                        }
                    } else {
                        if (data.cpsRecord1 > LIMITED_CPS && data.cpsRecord5 > LIMITED_CPS && data.cpsRecord15 > LIMITED_CPS && data.cpsRecord10 > LIMITED_CPS) {
                            getServer().getLogger().warning("§e[ECNCPCompat] §b玩家" + playerName + "§cCPS异常已被自动处理!");
                            getServer().broadcastMessage(KICK_MESSAGE.replace("@player", playerName).replace("@cps1", String.valueOf(data.cpsRecord1)).replace("@cps5", String.valueOf(data.cpsRecord5)).replace("@cps10", String.valueOf(data.cpsRecord10)).replace("@cps15", String.valueOf(data.cpsRecord15)).replace("@world", getServer().getPlayer(playerName).getLevel().getName()));
                            if (!getServer().getPlayer(playerName).isOp())
                                provider.kickPlayer(getServer().getPlayer(playerName), CheckType.STAFF);
                        } else getServer().getLogger().warning("§e[ECNCPCompat] §b玩家" + playerName + "§aCPS正常已加入冷却队列!");
                        playerRecordData.remove(playerName);
                    }
                }
            }
        }, 20);

        //关闭NCP自带检测
        if (cancelNCP) provider.createBypassPermission("compatNCP.fight.speed", CheckType.FIGHT_SPEED);
    }

    @Override
    public void onDisable() {
        if (cancelNCP) provider.removeBypassPermission("compatNCP.fight.speed", CheckType.FIGHT_SPEED);
    }

    @EventHandler
    public void onWrapperPacket(WrapperPacketEvent event) {
        WrapperPacket packet = event.getPacket();
        Player player = event.getPlayer();
        if (packet instanceof WrapperInputPacket) {
            double cps = provider.getPlayerProvider(player).getFightData().getClickPerSecond();
            if (cps >= LIMITED_CPS) this.putPlayerToBase(player);
        } else if (packet instanceof WrapperDisconnectPacket) {
            if (((WrapperDisconnectPacket) packet).getReason() == CheckType.FIGHT_SPEED)
                if (((WrapperDisconnectPacket) packet).getType() == ActionType.KICK && cancelNCP) {
                    provider.getPlayerProvider(player.getName()).getViolationData(CheckType.FIGHT_SPEED).clear();
                    ((WrapperDisconnectPacket) packet).setCancelled();
                }
        }
    }

    @EventHandler
    public void onPlayerQuits(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerRecordData.remove(player.getName());
    }

    private void putPlayerToBase(Player player) {
        if (!timeBalanceBase.containsKey(player.getName()))
            timeBalanceBase.put(player.getName(), System.currentTimeMillis() - 1000L * BREAK_TIME);
        if (System.currentTimeMillis() - timeBalanceBase.get(player.getName()) > 1000L * BREAK_TIME && !playerRecordData.containsKey(player.getName())) {
            timeBalanceBase.put(player.getName(), System.currentTimeMillis());
            playerRecordData.put(player.getName(), new RecordData());
            getServer().getLogger().warning("§e[ECNCPCompat] §b已将玩家" + player.getName() + "列入检测名单");
        }
    }

}
