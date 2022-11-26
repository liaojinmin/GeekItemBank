package me.geek.bank.api.hook.impl

import dev.lone.itemsadder.api.CustomStack
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent
import me.geek.bank.GeekItemRank
import me.geek.bank.common.menu.Menu
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * 作者: 老廖
 * 时间: 2022/10/9
 *
 **/
class ItemsAdder {
    private val empty = buildItem(XMaterial.STONE) { name = "错误的物品命名" }
    var isHook = false
    init {
        hook()
    }

    private fun hook() {
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            GeekItemRank.say("&7软依赖 &fItemsAdder &7已兼容.")
            isHook = true
        } else Menu.loadMenu()
    }

    fun getItem(id: String): ItemStack {
        return CustomStack.getInstance(id)?.itemStack ?: empty
    }

    companion object {
        @SubscribeEvent
        fun onHook(e: ItemsAdderLoadDataEvent) { Menu.loadMenu() }
    }

}