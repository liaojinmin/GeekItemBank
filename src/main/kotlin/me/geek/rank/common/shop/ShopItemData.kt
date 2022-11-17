package me.geek.rank.common.shop


/**
 * 作者: 老廖
 * 时间: 2022/11/12
 *
 **/
data class ShopItemData(
    val DefaultPlayer_limit: Int = -1,
    val DefaultPlayer_time: Long = 0L,
    val DefaultGlobal_limit: Int = -1,
    val DefaultGlobal_time: Long = 0L,
    val shopItemNode: MutableList<ShopItemNode> = mutableListOf()
)
