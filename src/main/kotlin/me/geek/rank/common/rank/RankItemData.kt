package me.geek.rank.common.rank

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
data class RankItemData(
    val DefaultGive_Price: Double = 1.0,
    val DefaultTake_Price: Double = 1.1,
    val ItemDataNode: Collection<RankItemNode> = mutableListOf()
)
