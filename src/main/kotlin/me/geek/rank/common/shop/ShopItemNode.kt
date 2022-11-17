package me.geek.rank.common.shop

import me.geek.rank.common.shop.reward.RewardData

/**
 * 作者: 老廖
 * 时间: 2022/11/13
 *
 **/
data class ShopItemNode(
    override val displayName: String,
    override val lore: List<String> = mutableListOf(),
    override val mate: String,
    override val packID: String,
    override val price: Int = 0,
    override val global: SGlobalLimit? = null,
    override val player: SPlayerLimit? = null,
    override val action: String,
    override val reward: String
) : ShopDataPlaceholder()
