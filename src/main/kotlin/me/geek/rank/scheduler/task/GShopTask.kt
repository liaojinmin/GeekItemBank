package me.geek.rank.scheduler.task

import me.geek.rank.api.rank.RankManage
import me.geek.rank.api.rank.RankManage.updateData

import me.geek.rank.common.data.SqlManager
import taboolib.common.platform.function.submitAsync

/**
 * 作者: 老廖
 * 时间: 2022/11/15
 *
 **/
class GShopTask {
    init {
        submitAsync(delay = 600 * 20, period = 600 * 20) {
            // 玩家限制更新
            RankManage.getPlayerCache().forEach { (_, value) ->
                val data = value.pointsData.iterator()
                var ac = false
                while (data.hasNext()) {
                    val var10 = data.next()
                    if (var10.resTime <= System.currentTimeMillis()) {
                        ac = true
                        data.remove()
                    }
                }
                if (ac) value.updateData()
            }
            RankManage.getGlobalShopLimitCache().iterator().apply {
                val data: MutableList<String> = mutableListOf()
                while (this.hasNext()) {
                    val var10 = this.next()
                    if (var10.resTime <= System.currentTimeMillis()) {
                        data.add(var10.ShopId)
                        this.remove()
                    }
                }
                if (data.isNotEmpty()) SqlManager.deleteGlobalData(data)
            }
        }
    }
}