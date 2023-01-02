package net.catrainbow.nocheatplus.components.task

import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.checks.NCPTickTask
import net.catrainbow.nocheatplus.components.registry.INCPComponent
import net.catrainbow.nocheatplus.components.registry.NCPComponent

class NCPTaskCom : NCPComponent(), INCPComponent {

    override fun onEnabled() {
        this.getRegisterCom().setName("NCP Task")
        this.getRegisterCom().setAuthor("Catrainbow")
        this.getRegisterCom().setVersion("1.0.0")
        NoCheatPlus.instance.server.scheduler.scheduleRepeatingTask(NCPTickTask(), 1)
    }

}