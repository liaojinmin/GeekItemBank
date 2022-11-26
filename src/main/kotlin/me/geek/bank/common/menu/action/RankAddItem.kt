package me.geek.bank.common.menu.action

import me.geek.bank.GeekItemRank
import me.geek.bank.api.bank.BankManage.addItemPack
import me.geek.bank.api.bank.BankManage.getPlayerData
import me.geek.bank.api.bank.BankManage.updateData
import me.geek.bank.common.menu.ActionBase
import me.geek.bank.common.menu.Menu
import me.geek.bank.common.menu.Menu.sound
import me.geek.bank.common.bank.PlayerItemPack
import me.geek.bank.common.bank.Bank
import me.geek.bank.common.bank.Bank.matchNode
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.i18n.I18n
import taboolib.platform.util.sendLang
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/11/8
 *
 **/
class RankAddItem(
    override val player: Player,

    // 拿来判断玩家放入的物品是否符合要求
    private val item: ItemStack? = null,
) : ActionBase() {
    init {
        action()
    }

    override fun action() {
        val playerData = player.getPlayerData()
        Menu.isOpen.add(player)
        var name = "物品"
        if (item != null) {
            name = if (item.itemMeta!!.hasDisplayName()) {
                item.itemMeta!!.displayName
            } else I18n.instance.getName(item)
        }

        player.openInventory(Bukkit.createInventory(player, 27, "§0放入 $name §7| §0关闭界面"))
        player.sound("BLOCK_NOTE_BLOCK_HARP", 1f, 1f)
        Bukkit.getPluginManager().registerEvents(object : Listener {
            var cd: Long = 0

            @EventHandler
            fun onClick(e: InventoryClickEvent) {
                if ((e.view.title != "§0放入 $name §7| §0关闭界面") || (e.view.player != player)) return
                if (cd < System.currentTimeMillis()) {
                    cd = System.currentTimeMillis() + 100
                    if (e.rawSlot < 0) return
                    item?.let {
                        e.currentItem?.let {
                            if (it.itemMeta != item.itemMeta || it.type != item.type) {
                                player.sendLang(
                                    "玩家-存入物品-不匹配", if (it.itemMeta!!.hasDisplayName()) {
                                        it.itemMeta!!.displayName
                                    } else I18n.instance.getName(it)
                                )
                                e.isCancelled = true
                            }
                        }
                    }

                } else {
                    e.isCancelled = true
                }
            }

            @EventHandler
            fun onDrag(e: InventoryDragEvent) {
                if (player === e.whoClicked) {
                    e.isCancelled = true
                }
            }
            @EventHandler
            fun onClose(e: InventoryCloseEvent) {
                if (player == e.player) {
                    HandlerList.unregisterAll(this)
                    Menu.isOpen.removeIf { it == player }
                    playerData?.let {
                        var var10 = false
                        for (i2 in e.inventory.contents) {
                            if (i2 != null) {

                                // 匹配节点给予积分
                                val node = i2.matchNode()
                                if (node != null) {
                                    it.givePoints(node.give_Price * i2.amount)
                                } else it.givePoints(Bank.getBankCache().DefaultGive_Price * i2.amount)

                                    // 添加物品
                                    // 检查是否存在物品
                                val a = check(it.ItemList, i2)
                                    // 没有储存过的情况下
                                if (!a) {
                                    val amt = i2.amount
                                    i2.amount = 1
                                    it.addItemPack(PlayerItemPack(UUID.randomUUID(), i2, amt))
                                }
                                var10 = true
                            }
                        }
                        if (var10) it.updateData()
                    }
                }
            }
        }, GeekItemRank.instance)
    }
    private fun check(data: MutableList<PlayerItemPack>, item: ItemStack): Boolean {
        data.forEach { playerItemPack ->
            if (playerItemPack.itemStack.itemMeta == item.itemMeta && playerItemPack.itemStack.type == item.type) {
                playerItemPack.amount += item.amount
                return true
            }
        }
        return false
    }

    override fun build() {
        TODO("Not yet implemented")
    }
}