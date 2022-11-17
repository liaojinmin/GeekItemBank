package me.geek.rank.common.rank

import org.bukkit.inventory.ItemStack

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
data class RankItemNode(
    val id: String,
    val itemStack: ItemStack,
    val give_Price: Double = 1.00,
    val take_Price: Double = 1.50,
)