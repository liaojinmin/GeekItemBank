package me.geek.bank.api.hook

import me.geek.bank.GeekItemRank
import me.geek.bank.api.hook.impl.ItemsAdder
import me.geek.bank.api.hook.impl.MythicMobs
import org.bukkit.Bukkit

/**
 * 作者: 老廖
 * 时间: 2022/11/8
 *
 **/
object HookPlugin {
    val mythicMobs by lazy { MythicMobs() }
    val itemsAdder by lazy { ItemsAdder() }
    fun onHook() {
        onHookPapi()
        mythicMobs
        itemsAdder
    }


    private fun onHookPapi() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Placeholder().register()
            GeekItemRank.say("&7软依赖 &fPlaceholderAPI &7已兼容.")
        }
    }



}