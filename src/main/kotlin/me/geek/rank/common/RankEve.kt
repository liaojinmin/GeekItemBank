package me.geek.rank.common

import me.geek.rank.GeekItemRank
import me.geek.rank.api.rank.RankManage
import me.geek.rank.api.rank.RankManage.remPlayerData
import me.geek.rank.api.rank.RankManage.setPlayerData
import me.geek.rank.api.rank.RankManage.updateData

import me.geek.rank.common.data.SqlManager
import me.geek.rank.common.menu.Menu
import me.geek.rank.common.menu.Menu.openMenu
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.platform.util.sendLang
import java.text.SimpleDateFormat
import java.util.Calendar


/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
object RankEve {
    private val df = SimpleDateFormat("dd")
    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player
        submitAsync {
            SqlManager.selectPlayerData(player).also {
                val ca = Calendar.getInstance()
            //    GeekItemRank.debug("A-日期: ${it.Day} += ${ca.get(Calendar.DAY_OF_MONTH)}")
                if (it.Day != ca.get(Calendar.DAY_OF_MONTH) && it.ItemList.isNotEmpty()) {
                    it.Day = ca.get(Calendar.DAY_OF_MONTH)
                    val a = it.pointsFormat(false).toDouble()
                    it.givePoints(a)
                  //  GeekItemRank.debug("B-日期: ${it.Day} += ${ca.get(Calendar.DAY_OF_MONTH)}")
                    player.sendLang("玩家-每日登录-发放利息", a)
                    it.updateData()
                }
                it.setPlayerData()
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onQQuit(e: PlayerQuitEvent) {
        val player = e.player
        RankManage.getPlayerData(player.uniqueId)?.remPlayerData()
    }


    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onCommand(e: PlayerCommandPreprocessEvent) {
        val message = e.message.removePrefix("/")
        if (message.isNotBlank()) {
            Menu.getMenuCommand(message)?.let {
                e.isCancelled = true
                e.player.openMenu(it)
            }
        }
    }
}