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
package net.catrainbow.nocheatplus.liteban;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import com.j256.ormlite.support.ConnectionSource;
import net.catrainbow.nocheatplus.NoCheatPlus;
import net.catrainbow.nocheatplus.actions.ActionType;
import net.catrainbow.nocheatplus.checks.CheckType;
import net.catrainbow.nocheatplus.components.NoCheatPlusAPI;
import net.catrainbow.nocheatplus.feature.wrapper.WrapperActionPacket;
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacket;
import net.catrainbow.nocheatplus.feature.wrapper.WrapperPacketEvent;
import net.catrainbow.nocheatplus.utilities.NCPTimeTool;
import ru.nukkit.dblib.DbLib;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 一个让NCP支持数据库封禁的插件
 * <p>
 * A plugin to make NCP supported by MySQL
 *
 * @author Catrainbow
 */
public class NCPLiteBan extends PluginBase implements Listener {

    private final String TABLE_NAME = "nocheatplusliteban";
    private final NoCheatPlusAPI provider = NoCheatPlus.instance;

    @Override
    public void onEnable() {
        if (this.getServer().getPluginManager().getPlugin("DbLib") == null) {
            this.getLogger().info("§e[NCP§bLiteBan§e] §cDbLib plugin not found");
            return;
        }
        if (this.getServer().getPluginManager().getPlugin("NoCheatPlus") == null) {
            this.getLogger().info("§e[NCP§bLiteBan§e] §cNoCheatPlus plugin not found");
            return;
        }
        if (this.connectToDbLib()) {
            try {
                this.createTable();
                this.getServer().getPluginManager().registerEvents(this, this);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.getLogger().info("§e[NCP§bLiteBan§e] §cCan't connect to DBLib!");
        }
    }

    @EventHandler
    public void onPlayerJoins(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        if (this.isPlayerHasRecord(player)) {
            String date = this.getPlayerRecordDate(player);
            LocalDateTime dateTime = NCPTimeTool.INSTANCE.stringToTime(date);
            if (NCPTimeTool.INSTANCE.canUnban(NCPTimeTool.INSTANCE.getNowTime(), dateTime)) {
                this.deletePlayerBanRecord(player);
                this.getLogger().info("§e[NCP§bLiteBan§e] §aAuto unban player " + player.getName() + " successfully because of time out!");
            } else {
                if (!provider.isPlayerBan(player)) {
                    int[] diffDate = NCPTimeTool.INSTANCE.getTimeBetween2(NCPTimeTool.INSTANCE.getNowTime(), dateTime);
                    provider.banPlayer(player, diffDate[0], diffDate[1], diffDate[2]);
                }
                provider.kickPlayer(player, CheckType.STAFF);
                this.getLogger().info("§e[NCP§bLiteBan§e] §aAuto kick player " + player.getName() + " because he was banned on cloud!");
            }
        } else if (provider.isPlayerBan(player)) {
            String date = provider.getNCPBanRecord().getStringList(player.getName()).get(1);
            try {
                updateBanDate(player, date);
                this.getLogger().info("§e[NCP§bLiteBan§e] §aUpload " + player.getName() + " ban status to cloud successfully!");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @EventHandler
    public void onWrapperPackets(WrapperPacketEvent event) {
        WrapperPacket packet = event.getPacket();
        if (packet instanceof WrapperActionPacket) {
            if (((WrapperActionPacket) packet).actionType == ActionType.BAN) {
                Player player = event.player;
                //延迟5秒获取封禁日期
                this.getServer().getScheduler().scheduleDelayedTask(new Task() {
                    @Override
                    public void onRun(int i) {
                        if (provider.isPlayerBan(player)) {
                            String date = provider.getNCPBanRecord().getStringList(player.getName()).get(1);
                            try {
                                updateBanDate(player, date);
                                getLogger().info("§e[NCP§bLiteBan§e] §aUpload " + player.getName() + " ban status to cloud successfully!");
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }, 20 * 5);
            }
        }
    }

    public void updateBanDate(Player player, String date) throws SQLException {
        String query;
        if (isPlayerHasRecord(player)) {
            query = "update " + TABLE_NAME + " set date = \"" + date + "\" where name = \"$" + player.getName() + "\";";
        } else query = "insert into " + TABLE_NAME + " (name,date) values ('" + player.getName() + "','" + date + "')";
        executeUpdate(query);
    }

    public boolean isPlayerHasRecord(Player player) throws SQLException {
        String query = "select * from " + TABLE_NAME + " where name='" + player.getName() + "'";
        List<String> list = executeSelect(query);
        assert list != null;
        return !list.isEmpty();
    }

    public void deletePlayerBanRecord(Player player) throws SQLException {
        String query = "delete from " + TABLE_NAME + " where (name = \"" + player.getName() + "\");";
        executeSelect(query);
    }

    public String getPlayerRecordDate(Player player) throws SQLException {
        if (isPlayerHasRecord(player)) {
            String query = "select * from " + TABLE_NAME + " where name='" + player.getName() + "'";
            List<String> list = executeSelect(query);
            assert list != null;
            for (String s : list) return s;
        } else return null;
        return null;
    }

    public boolean connectToDbLib() {
        ConnectionSource connectionSource = DbLib.getConnectionSource();
        return connectionSource != null;
    }

    public void createTable() throws SQLException {
        String query = "create table if not exists " + TABLE_NAME + " (name varchar(255), date varchar(255))";
        if (executeUpdate(query)) this.getLogger().info("§e[NCP§bLiteBan§e] §aTable created successfully!");
        else {
            this.getLogger().info("§e[NCP§bLiteBan§e] §cFailed to create table!");
        }
    }

    public static Connection connectToMySQL() {
        return DbLib.getDefaultConnection();
    }

    public static boolean executeUpdate(String query) throws SQLException {
        Connection connection = connectToMySQL();
        if (connection == null) return false;
        Statement statement = connection.prepareStatement(query);
        statement.executeUpdate(query);
        statement.close();
        connection.close();
        return true;
    }

    public static List<String> executeSelect(String query) throws SQLException {
        List<String> list = new ArrayList<>();
        Connection connection = connectToMySQL();
        if (connection == null) return list;
        Statement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet == null) return null;
        while (resultSet.next()) list.add(resultSet.getString("name") + " " + resultSet.getString("date"));
        statement.close();
        connection.close();
        return list;
    }

}
