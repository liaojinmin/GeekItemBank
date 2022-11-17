package me.geek.rank.common.rank

import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
interface ItemPack {
    val itemUid: UUID
    val itemStack: ItemStack
    val amount: Int
}