package me.geek.bank.common.bank

import org.bukkit.inventory.ItemStack

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
data class BankItemNode(
    val id: String,
    val itemStack: ItemStack,
    val give_Price: Double = 1.00,
    val take_Price: Double = 1.50,
)