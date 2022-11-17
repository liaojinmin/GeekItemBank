package me.geek.rank.common.shop

/**
 * 作者: 老廖
 * 时间: 2022/11/12
 *
 **/
data class SPlayerLimit(
    override val limit: Int = -1,
    override val time: Long = 0L,
): LimitData
