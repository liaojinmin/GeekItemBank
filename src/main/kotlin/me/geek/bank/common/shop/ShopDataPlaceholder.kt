package me.geek.bank.common.shop

import me.geek.bank.api.bank.BankManage
import me.geek.bank.common.data.PointsData
import org.jetbrains.annotations.NotNull

/**
 * 作者: 老廖it
 * 时间: 2022/11/12
 *
 **/
abstract class ShopDataPlaceholder: ShopData {
    private val prices = Regex("(\\{|\\[)(item_Price|商品价格)(}|])")
    private val limit = Regex("(\\{|\\[)(player_limit|个人限制)(}|])")
    private val globalLimit = Regex("(\\{|\\[)(global_limit|全局限制)(}|])")

    fun parseItemInfo(@NotNull iconLore: List<String>, pointsData: MutableList<PointsData>?): MutableList<String> {
        val list = mutableListOf<String>()
        iconLore.forEach {
            when {
                it.contains(prices) -> list.add(it.replace(prices, this.price.toString()))

                it.contains(limit) -> {
                    player?.let { data ->
                        if (pointsData != null && pointsData.isNotEmpty()) {
                            var ac = true
                            pointsData.forEach { p ->
                                if (p.ShopId == packID) {
                                    ac = false
                                    list.add(it.replace(limit, (data.limit - p.Buy_amt).toString()))
                                }
                            }.also { _ -> if (ac) list.add(it.replace(globalLimit, data.limit.toString())) }
                        } else list.add(it.replace(limit, data.limit.toString()))

                    } ?: list.add(it.replace(limit, "不限制"))
                }

                it.contains(globalLimit) -> {
                    global?.let { data ->
                        if (BankManage.getGlobalShopLimitCache().isNotEmpty()) {
                            var ac = true
                            BankManage.getGlobalShopLimitCache().forEach { p ->
                                if (p.ShopId == packID) {
                                    ac = false
                                    list.add(it.replace(globalLimit, (data.limit - p.GBuy_amt).toString()))
                                }
                            }.also { _ -> if (ac) list.add(it.replace(globalLimit, data.limit.toString())) }
                        } else list.add(it.replace(globalLimit, data.limit.toString()))
                    } ?: list.add(it.replace(globalLimit, "不限制"))
                }

                else -> list.add(it)
            }
        }
        return list
    }

}