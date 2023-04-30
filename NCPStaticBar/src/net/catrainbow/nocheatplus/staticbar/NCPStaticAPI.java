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

public class NCPStaticAPI {

    public static long getPlayerFetchTime(Player player) {
        if (!NCPStaticBar.instance.staticBar.containsKey(player.getName()))
            NCPStaticBar.instance.staticBar.put(player.getName(), System.currentTimeMillis() - NCPStaticBar.fetchTime * 1000L);
        return NCPStaticBar.instance.staticBar.get(player.getName());
    }

    public static boolean isPlayerChecked(Player player) {
        if (!NCPStaticBar.instance.staticBar.containsKey(player.getName()))
            NCPStaticBar.instance.staticBar.put(player.getName(), System.currentTimeMillis() - NCPStaticBar.fetchTime * 1000L);
        return System.currentTimeMillis() - getPlayerFetchTime(player) < NCPStaticBar.fetchTime * 1000L;
    }

    public static void setPlayerCheckable(Player player) {
        setPlayerCheckable(player.getName());
    }

    public static void setPlayerCheckable(String player) {
        NCPStaticBar.getInstance().staticBar.put(player, System.currentTimeMillis());
    }

}
