package me.geek.bank.common.shop

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