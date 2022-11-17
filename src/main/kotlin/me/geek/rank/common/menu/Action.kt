package me.geek.rank.common.menu

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

/**
 * 作者: 老廖
 * 时间: 2022/11/9
 *
 **/
interface Action {

    val player: Player

    val tag: Session? get() = null

    val inv: Inventory? get() = null

}