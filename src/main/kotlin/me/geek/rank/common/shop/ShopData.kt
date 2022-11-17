package me.geek.rank.common.shop

import me.geek.rank.common.shop.reward.PrizesNode
import me.geek.rank.common.shop.reward.RewardData

/**
 * 作者: 老廖
 * 时间: 2022/11/12
 *
 **/
interface ShopData {
    val displayName: String
    val lore: List<String>
    val mate: String
    val packID: String
    val price: Int
    val global: SGlobalLimit?
    val player: SPlayerLimit?
    val action: String
    val reward: String
}