package me.geek.rank.utils.catcher

import me.geek.rank.GeekItemRank
import me.geek.rank.api.rank.RankManage.getPlayerData
import me.geek.rank.api.rank.RankManage.getQuitItem
import me.geek.rank.api.rank.RankManage.remItemPack
import me.geek.rank.api.rank.RankManage.remQuitItem
import me.geek.rank.api.rank.RankManage.updateData
import me.geek.rank.common.rank.Rank
import me.geek.rank.common.rank.Rank.matchNode
import me.geek.rank.utils.getEmptySlot
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.function.submitAsync
import taboolib.module.kether.isInt
import taboolib.module.nms.i18n.I18n
import taboolib.platform.util.giveItem
import taboolib.platform.util.sendLang

/**
 * 作者: 老廖
 * 时间: 2022/9/11
 *
 **/
class Chat(
    private val player: Player
) {
    val cancel = Regex("""cancel|取消|Cancel""")
    fun start() {
        submitAsync {
            val eve = object : Listener {
                @EventHandler
                fun onChat(e: AsyncPlayerChatEvent) {
                    if (e.player == player) {
                        e.isCancelled = true
                        if (!e.message.contains(cancel)) {
                            if (e.message.isInt()) {
                                val amt = e.message.toInt()
                                player.getQuitItem()?.let {
                                    player.getPlayerData()?.let { data ->

                                        val node = it.itemStack.matchNode()
                                        val price = if (node != null) { node.take_Price * amt } else { Rank.getRankCache().DefaultTake_Price * amt }

                                        if (price > data.points) {
                                            player.sendLang("玩家-取出物品-积分不足", price - data.pointsFormat().toDouble())
                                            return
                                        }
                                        if (amt > it.amount) {
                                            player.sendLang("玩家-取出物品-物品不足", it.amount)
                                            return
                                        }
                                        val nullStol = player.getEmptySlot(isItemAmount = true)
                                        if (nullStol < amt) {
                                            player.sendLang("玩家-取出物品-背包空间不足", amt - nullStol)
                                            return
                                        }

                                        if (amt < it.amount) {
                                            player.giveItem(it.itemStack, e.message.toInt())
                                            it.amount -= e.message.toInt()

                                            data.takePoints(price)
                                            data.updateData()

                                            player.sendLang("玩家-取出物品-成功取出",  if (it.itemStack.itemMeta!!.hasDisplayName()) {
                                                it.itemStack.itemMeta!!.displayName
                                            } else I18n.instance.getName(it.itemStack), amt)
                                            player.remQuitItem()
                                            return
                                        }

                                        // 全部取出的情况下
                                        data.takePoints(price)
                                        data.updateData()
                                        player.giveItem(it.itemStack, amt)
                                        player.remQuitItem()
                                        player.remItemPack(it)
                                    }
                                }
                            } else player.sendLang("玩家-取出物品-输入错误", e.message)
                        }
                        GeekItemRank.debug("监听器注销")
                        HandlerList.unregisterAll(this)
                    }
                }
            }
            player.sendLang("玩家-取出物品-消息提示")
            Bukkit.getPluginManager().registerEvents(eve, GeekItemRank.instance)
            runTask(eve)
        }
    }
    private fun runTask(e: Listener) {
        val end = System.currentTimeMillis() + 20000
        while (System.currentTimeMillis() < end) {
            Thread.sleep(1000)
        }
        player.remQuitItem()
        HandlerList.unregisterAll(e)
    }
}