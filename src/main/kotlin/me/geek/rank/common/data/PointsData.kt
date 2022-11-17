package me.geek.rank.common.data

/**
 * 作者: 老廖
 * 时间: 2022/11/10
 *
 **/
data class PointsData(
    /**
     * 商品ID
     */
    val ShopId: String,
    /**
     * 已购买次数
     */
    var Buy_amt: Int = 0,
    /**
     * 重置时间
     */
    val resTime: Long = 0,
)