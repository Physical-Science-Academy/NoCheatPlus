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
package net.catrainbow.nocheatplus.logging

import net.catrainbow.nocheatplus.NoCheatPlus
import net.catrainbow.nocheatplus.components.data.ConfigData
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * NCPLogger
 *
 * @author Catrainbow
 */
class NCPLogger {

    private lateinit var logFile: File
    private var fw: FileWriter? = null
    private var writer: BufferedWriter? = null
    private var deleteCount = 0

    init {
        try {
            File(
                NoCheatPlus.instance.dataFolder.toString() + File.separator + "logs", "NCP_log_" + fileTime() + ".txt"
            ).also { this.logFile = it }
            val path = File(NoCheatPlus.instance.dataFolder.toString() + File.separator + "logs")
            if (!path.exists()) {
                path.mkdirs()
            }

            if (ConfigData.logging_active) {
                this.recursiveDeleteFilesOlderThanNDays(
                    ConfigData.logging_auto_delete_days,
                    NoCheatPlus.instance.dataFolder.toString() + File.separator + "logs"
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun info(message: String) {
        if (!ConfigData.logging_active) return
        try {
            fw = FileWriter(logFile, true)
            writer = fw?.let { BufferedWriter(it) }
            writer!!.write("$time $message")
            writer!!.newLine()
            writer!!.close()
            fw!!.close()
        } catch (var2: Exception) {
            var2.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun recursiveDeleteFilesOlderThanNDays(days: Int, dirPath: String?) {
        val cutOff = System.currentTimeMillis() - days.toLong() * 24 * 60 * 60 * 1000
        Files.list(dirPath?.let { Paths.get(it) })
            .forEach { path: Path ->
                if (Files.isDirectory(path)) {
                    try {
                        recursiveDeleteFilesOlderThanNDays(days, path.toString())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        if (Files.getLastModifiedTime(path)
                                .to(TimeUnit.MILLISECONDS) < cutOff
                        ) {
                            Files.delete(path)
                            this.deleteCount++
                        }
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }
            }
    }

    private val time: String
        get() {
            val d = Date()
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            return "[" + sdf.format(d) + "]"
        }

    private fun fileTime(): String {
        val d = Date()
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
        return sdf.format(d)
    }

    fun getDeleteCount(): Int {
        return this.deleteCount
    }

}