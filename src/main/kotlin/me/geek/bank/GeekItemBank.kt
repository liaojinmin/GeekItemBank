package me.geek.bank

import me.geek.bank.api.hook.HookPlugin
import me.geek.bank.api.bank.BankManage
import me.geek.bank.api.bank.BankManage.setGlobalShopLimitCache
import me.geek.bank.api.bank.BankManage.update
import me.geek.bank.common.data.SqlManager
import me.geek.bank.common.menu.Menu
import me.geek.bank.common.bank.Bank
import me.geek.bank.common.settings.SetTings
import me.geek.bank.common.shop.Shop
import me.geek.bank.scheduler.task.GShopTask
import me.geek.bank.scheduler.task.PointsTopTask
import me.geek.bank.utils.colorify
import org.bukkit.Bukkit
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.platform.BukkitPlugin

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
@RuntimeDependencies(
    RuntimeDependency(value = "!com.zaxxer:HikariCP:4.0.3",
        relocate = ["!com.zaxxer.hikari",
            "!com.zaxxer.hikari_4_0_3_bank"]),
)
object GeekItemRank : Plugin() {
    val instance by lazy { BukkitPlugin.getInstance() }
    val BukkitVersion by lazy { Bukkit.getVersion().substringAfter("MC:").filter { it.isDigit() }.toInt() }

    const val VERSION = 1.3


    override fun onLoad() {

        console().sendMessage("")
        console().sendMessage("正在加载 §3§lGeekItemBank  §f...  §8" + Bukkit.getVersion())
        console().sendMessage("")
    }
    override fun onEnable() {
        runLogo()
        SetTings.onLoadSetTings()

        SqlManager.start()

        setGlobalShopLimitCache(SqlManager.selectGlobalData())

        HookPlugin.onHook()

        GShopTask() // 限制任务

        PointsTopTask.getPointsTop() // 拉起排行榜任务
    }
    override fun onActive() {
        // 加载物品配置
        Bank.onLoad()
        Shop.onLoad()
    }


    override fun onDisable() {
        Menu.closeGui()
        BankManage.getGlobalShopLimitCache().update()
        SqlManager.closeData()
    }

    fun onReload() {
        Menu.closeGui()
        SetTings.onLoadSetTings()
        Menu.loadMenu()
        Bank.onLoad()
        Shop.onLoad()
    }


    @JvmStatic
    fun say(msg: String) {
        if (BukkitVersion >= 1160)
            console().sendMessage("&8[<g#2:#FFB5C5:#EE0000>GeekItemRank&8] &7$msg".colorify())
        else
            console().sendMessage("§8[§6GeekItemBank§8] ${msg.replace("&", "§")}")
    }
    @JvmStatic
    fun debug(msg: String) {
        if(SetTings.debug) {
            if (BukkitVersion >= 1160)
                console().sendMessage("&8[<g#2:#FFB5C5:#EE0000>GeekItemBank&8] &cDeBug &8| &7$msg".colorify())
            else
                console().sendMessage("§8[§6GeekItemBank§8] ${msg.replace("&", "§")}")
        }
    }

    private fun runLogo() {
        console().sendMessage("  ________               __   .___  __                __________                __    ")
        console().sendMessage(" /  _____/  ____   ____ |  | _|   |/  |_  ____   _____\\______   \\_____    ____ |  | __")
        console().sendMessage("/   \\  ____/ __ \\_/ __ \\|  |/ /   \\   __\\/ __ \\ /     \\|    |  _/\\__  \\  /    \\|  |/ /")
        console().sendMessage("\\    \\_\\  \\  ___/\\  ___/|    <|   ||  | \\  ___/|  Y Y  \\    |   \\ / __ \\|   |  \\    < ")
        console().sendMessage(" \\______  /\\___  >\\___  >__|_ \\___||__|  \\___  >__|_|  /______  /(____  /___|  /__|_ \\")
        console().sendMessage("        \\/     \\/     \\/     \\/              \\/      \\/       \\/      \\/     \\/     \\/")
        console().sendMessage("")
        console().sendMessage("")
        console().sendMessage("       §aGeekItemBank§8-§6Custom §bv$VERSION §7by §awww.geekcraft.ink")
        console().sendMessage("       §8适用于Bukkit: §71.12.2-1.19.2 §8当前: §7 ${Bukkit.getServer().version}")
        console().sendMessage("")
    }



}