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
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import net.catrainbow.nocheatplus.NoCheatPlus;
import net.catrainbow.nocheatplus.components.NoCheatPlusAPI;

import java.util.HashMap;

/**
 * NCP 静态工作模式
 * NCP static mode
 *
 * @author Catrainbow
 */
public class NCPStaticBar extends PluginBase {

    public static NCPStaticBar instance;
    public static int fetchTime = 15;

    public static NCPStaticBar getInstance() {
        return instance;
    }

    public static NoCheatPlusAPI provider = NoCheatPlus.instance;
    public HashMap<String, Long> staticBar = new HashMap<>();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (this.getServer().getPluginManager().getPlugin("NoCheatPlus") == null) {
            this.getLogger().info("§e[NCP§bLiteBan§e] §cNoCheatPlus plugin not found");
            return;
        }
        Config config = this.getConfig();
        if (!config.exists("fetchTime")) {
            config.set("fetchTime", 15);
            config.save(false);
        }
        fetchTime = config.getInt("fetchTime");
        this.getServer().getScheduler().scheduleRepeatingTask(new Task() {
            @Override
            public void onRun(int i) {
                for (Player player : getServer().getOnlinePlayers().values()) {
                    if (!NCPStaticAPI.isPlayerChecked(player)) {
                        provider.clearAllViolations(player);
                    }
                }
            }
        }, 20);
        this.getServer().getPluginManager().registerEvents(new NCPStaticListener(), this);
    }

}
