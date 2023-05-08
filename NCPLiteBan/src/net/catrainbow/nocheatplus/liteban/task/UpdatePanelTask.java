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
package net.catrainbow.nocheatplus.liteban.task;

import cn.nukkit.scheduler.Task;
import net.catrainbow.nocheatplus.gui.ViolationBuffer;
import net.catrainbow.nocheatplus.liteban.NCPLiteBan;

import java.sql.SQLException;

public class UpdatePanelTask extends Task {

    private ViolationBuffer lastBuffer;

    public UpdatePanelTask() {
        this.lastBuffer = new ViolationBuffer();
        this.lastBuffer.playerName = "default";
        this.lastBuffer.type = "default";
    }

    @Override
    public void onRun(int i) {
        try {
            String data = NCPLiteBan.getInstance().getPanelData();
            String[] branchData = data.split(":");
            String info = branchData[0];
            String playerName = branchData[1];
            if (playerName.equals(lastBuffer.playerName)) return;
            String level = branchData[2];
            String type = branchData[3];
            ViolationBuffer buffer = new ViolationBuffer();
            buffer.info = info;
            buffer.type = type;
            buffer.playerName = playerName;
            buffer.level = level;
            this.lastBuffer = buffer;
            ViolationBuffer.violationBuffers.put(System.currentTimeMillis(), buffer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
