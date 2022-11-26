package me.geek.bank.utils

import me.geek.bank.GeekItemRank
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * 作者: 老廖
 * 时间: 2022/11/26
 *
 **/

fun Player.getEmptySlot(hasEquipment: Boolean = false, isItemAmount: Boolean = false): Int {
    var air = 0
    for (itemStack in inventory.contents) {
        if (itemStack == null || itemStack.type == Material.AIR) { air++ }
    }
    if (!hasEquipment) {
        if (GeekItemRank.BukkitVersion !in 170..190) {
            if (inventory.itemInOffHand.type == Material.AIR) air--
            if (inventory.helmet == null) air--
            if (inventory.chestplate == null) air--
            if (inventory.leggings == null) air--
            if (inventory.boots == null) air--
        }
    }
    return if (isItemAmount) air * 64 else air
}