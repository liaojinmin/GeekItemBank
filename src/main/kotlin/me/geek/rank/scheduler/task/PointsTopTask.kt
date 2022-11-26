package me.geek.rank.scheduler.task

import me.geek.rank.api.rank.RankManage
import me.geek.rank.scheduler.sql.SqlService
import taboolib.common.platform.function.submitAsync
import java.sql.Connection
import java.util.concurrent.ConcurrentHashMap

/**
 * 作者: 老廖
 * 时间: 2022/11/17
 *
 **/
class PointsTopTask {
    val points: MutableMap<Int, String> = ConcurrentHashMap()

    init {
        submitAsync(delay = 600 * 20, period = 600 * 20) {
            val var10 = ArrayList(RankManage.getPlayerCache().entries)
            var index = 0
            var10.sortedWith { o1, o2 ->
                o2.value.pointsFormat().toDouble().compareTo(o1.value.pointsFormat().toDouble())
            }.forEach {
                points[index + 1] = "${it.value.playerName};${it.value.pointsFormat()}"
                index++
            }
        }
    }

    companion object {
        private val p = PointsTopTask()
        fun getPointsTop(): MutableMap<Int, String> = p.points
    }
    class LocSqlite() : SqlService() {
        override var isActive: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}

        override fun getConnection(): Connection {
            TODO("Not yet implemented")
        }

        override fun onStart() {
            TODO("Not yet implemented")
        }

        override fun onClose() {
            TODO("Not yet implemented")
        }

    }
}