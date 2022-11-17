package me.geek.rank.scheduler.task

import me.geek.rank.api.rank.RankManage
import taboolib.common.platform.function.submitAsync
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
        val getPointsTop: MutableMap<Int, String> =  PointsTopTask().points
    }
}