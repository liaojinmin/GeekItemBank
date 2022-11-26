package me.geek.bank.common.data

/**
 * 作者: 老廖
 * 时间: 2022/11/15
 *
 **/
data class GlobalData(
    /**
     * 商品ID
     */
    val ShopId: String,
    /**
     * 已购买次数
     */
    var GBuy_amt: Int = 0,
    /**
     * 重置时间
     */
    val resTime: Long = 0,
)
