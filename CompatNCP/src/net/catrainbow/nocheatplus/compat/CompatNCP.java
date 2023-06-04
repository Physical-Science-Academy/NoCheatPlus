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

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.catrainbow.nocheatplus.NoCheatPlus;
import net.catrainbow.nocheatplus.components.NoCheatPlusAPI;
import net.catrainbow.nocheatplus.utilities.PluginUpdater;

import java.util.HashMap;

/**
 * A plugin to make NCP supported server-side motion
 * like DoubleJump or Booster
 *
 * @author Catrainbow
 */
public class CompatNCP extends PluginBase {

    public static NoCheatPlusAPI provider = null;
    public static HashMap<String, Boolean> settings = new HashMap<>();

    @Override
    public void onEnable() {
        if (this.getServer().getPluginManager().getPlugin("NoCheatPlus") == null) return;
        provider = NoCheatPlus.instance;
        this.getLogger().info("§dConnected to §bNoCheatPlus§d successfully!");
        this.enableConfig();
        this.getServer().getPluginManager().registerEvents(new PluginListener(), this);
    }

    private void enableConfig() {
        Config config = this.getConfig();
        PluginUpdater updater = new PluginUpdater();
        boolean exist = config.exists("version") && config.getString("version").equals(updater.getNCPVersion());
        if (!exist) {
            updateKeys(config, "version", updater.getNCPVersion());
            //support double jump
            updateKeys(config, "plugin.slimeJump", false);
            updateKeys(config, "plugin.booster", false);
            updateKeys(config, "plugin.allowFlight", false);
            updateKeys(config, "plugin.ignorePacket", false);
            updateKeys(config, "plugin.waterDogStepIn", false);
            updateKeys(config,"plugin.kickCleanViolations",false);
        }
        for (String key : config.getSection("plugin").getAllMap().keySet()) {
            settings.put(key, config.getBoolean(key));
            this.getLogger().info("Hook NCP and add settings: " + key);
        }
    }

    private void updateKeys(Config config, String key, Object element) {
        if (!config.exists(key)) {
            config.set(key, element);
            config.save(true);
        }
    }

    public Config getConfig() {
        return new Config(this.getDataFolder() + "/config.yml", 2);
    }

}
