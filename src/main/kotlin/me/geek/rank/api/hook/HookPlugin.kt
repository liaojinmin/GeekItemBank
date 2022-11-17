package me.geek.rank.api.hook

import me.geek.rank.GeekItemRank
import me.geek.rank.api.hook.impl.ItemsAdder
import me.geek.rank.api.hook.impl.MythicMobs
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