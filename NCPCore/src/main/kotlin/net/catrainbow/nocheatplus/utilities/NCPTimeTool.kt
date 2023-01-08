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
package net.catrainbow.nocheatplus.utilities

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * WatchDogDelayTool 时间管理类工具
 * 直接从WatchDogX复制过来了
 *
 * @author Catrainbow
 */
object NCPTimeTool {

    fun canUnBan(nowTime: LocalDateTime, newTime: LocalDateTime?): Boolean {
        return nowTime.isAfter(newTime)
    }

    fun getTimeBetween2(nowTime: LocalDateTime, newTime: LocalDateTime): IntArray {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val now = localTimeToDate(newTime)
        val date = localTimeToDate(nowTime)
        val l = now.time - date.time
        val day = l / 86400000L
        val hour = l / 3600000L - day * 24L
        val min = l / 60000L - day * 24L * 60L - hour * 60L
        val s = l / 1000L - day * 24L * 60L * 60L - hour * 60L * 60L - min * 60L
        return intArrayOf(day.toInt(), hour.toInt(), min.toInt(), s.toInt())
    }

    private fun localTimeToDate(localDateTime: LocalDateTime): Date {
        val zone = ZoneId.systemDefault()
        val instant = localDateTime.atZone(zone).toInstant()
        return Date.from(instant)
    }

    val nowTimeForArray: IntArray
        get() {
            val localDateTime = LocalDateTime.now()
            return intArrayOf(
                localDateTime.year,
                localDateTime.monthValue,
                localDateTime.dayOfMonth,
                localDateTime.hour,
                localDateTime.minute,
                localDateTime.second
            )
        }

    fun stringToTime(str: String?): LocalDateTime {
        val timeDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return LocalDateTime.parse(str, timeDtf)
    }

    fun timeToArray(time: LocalDateTime): IntArray {
        return intArrayOf(time.year, time.monthValue, time.dayOfMonth, time.hour, time.minute, time.second)
    }

    val nowTime: LocalDateTime
        get() = LocalDateTime.now()

    fun plusTimeByDays(now: LocalDateTime, days: Int): LocalDateTime {
        return now.plusDays(days.toLong())
    }

    fun plusTimeByHours(now: LocalDateTime, hours: Int): LocalDateTime {
        return now.plusHours(hours.toLong())
    }

    fun plusTimeByMinute(now: LocalDateTime, minute: Int): LocalDateTime {
        return now.plusMinutes(minute.toLong())
    }

    fun plusTimeByMonth(now: LocalDateTime, month: Int): LocalDateTime {
        return now.plusMonths(month.toLong())
    }

    fun createTime(year: Int, month: Int, day: Int, hour: Int, second: Int): LocalDateTime {
        return LocalDateTime.of(year, month, day, hour, second)
    }

    fun formatTime(localDateTime: LocalDateTime): String {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))
    }
}
