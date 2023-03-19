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
package net.catrainbow.nocheatplus.utilities

import com.google.gson.JsonParser
import net.catrainbow.nocheatplus.NoCheatPlus
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.util.Properties
import java.util.concurrent.CompletableFuture


/**
 * 自动获取更新
 *
 * @author Catrainbow
 */
class PluginUpdater {

    private val updateLink = "https://api.github.com/repos/Physical-Science-Academy/NoCheatPlus/commits/main"

    private val gitInfo = getGitInfo()
    private val version = getVersion()
    var latest = "null"

    fun getNCPVersion(): String {
        return this.version
    }

    private fun getGitInfo(): Properties? {
        val gitFileStream = this.javaClass.classLoader.getResourceAsStream("git.properties")
        if (gitFileStream == null) {
            NoCheatPlus.instance.getNCPLogger().info("[NCPUpdater] Unable to find git.properties")
            return null
        }
        val properties = Properties()
        try {
            properties.load(gitFileStream)
        } catch (e: IOException) {
            NoCheatPlus.instance.getNCPLogger()
                .info("[NCPUpdater] Unable to load git.properties ${e.printStackTrace()}")
            return null
        }
        return properties
    }

    private fun getVersion(): String {
        val version = StringBuilder()
        version.append("git-")
        var commitId = ""
        return if (gitInfo == null || gitInfo.getProperty("git.commit.id.abbrev").also { commitId = it } == null) {
            version.append("null").toString()
        } else version.append(commitId).toString()
    }


    fun onUpdate(): Boolean {
        var update = false
        CompletableFuture.runAsync {
            update = try {
                val request: URLConnection = URL(updateLink).openConnection()
                request.connect()
                val content = InputStreamReader(request.content as InputStream)
                latest = "git-" + JsonParser.parseReader(content).asJsonObject.get("sha").asString.substring(0, 7)
                content.close()
                version != latest && version != "git-null"

            } catch (ignore: Exception) {
                false
            }
        }
        return update
    }

}