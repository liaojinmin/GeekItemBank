package me.geek.bank.common.menu.action

import me.geek.bank.GeekItemRank
import me.geek.bank.api.hook.HookPlugin
import me.geek.bank.api.bank.BankManage
import me.geek.bank.api.bank.BankManage.updateData
import me.geek.bank.common.data.GlobalData
import me.geek.bank.common.data.PointsData
import me.geek.bank.common.kether.KetherAPI
import me.geek.bank.common.menu.ActionBase
import me.geek.bank.common.menu.IconType
import me.geek.bank.common.menu.Menu
import me.geek.bank.common.menu.Menu.sound
import me.geek.bank.common.menu.Session
import me.geek.bank.common.shop.Shop
import me.geek.bank.common.shop.ShopItemNode
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.sendLang
import java.lang.IllegalArgumentException

/**
 * 作者: 老廖
 * 时间: 2022/11/10
 *
 **/
class PointsShop(
    override val player: Player,
    override val tag: Session,
    override val inv: Inventory
) : ActionBase() {

    private val playerData = BankManage.getPlayerData(player.uniqueId)
    private val itemPack = Shop.getShopCache()
    init {
        action()
    }

    override fun action() {
        Menu.isOpen.add(player)
        parsePlaceholder()
        build()
        if (contents.size != 0) {
            inv.contents = contents[0]
        }
        player.sound("BLOCK_NOTE_BLOCK_HARP",1f, 1f)
        player.openInventory(inv)

        Bukkit.getPluginManager().registerEvents(object : Listener {
            var cd: Long = 0
            @EventHandler
            fun onClick(e: InventoryClickEvent) {
                if (e.view.title != tag.title || e.view.player != player) return
                if (cd < System.currentTimeMillis()) {
                    cd = System.currentTimeMillis() + 300
                    if (e.rawSlot < 0) return
                    e.isCancelled = true
                    if (e.rawSlot < tag.stringLayout.length) {
                        val id = tag.stringLayout[e.rawSlot].toString()
                        for (micon in tag.micon) {
                            if (micon.icon == id) {
                                when (micon.type) {
                                    IconType.NEXT_PAGE -> {
                                        if (contents.size > page + 1) {
                                            page += 1
                                            inv.contents = contents[page]
                                            player.sound("BLOCK_SCAFFOLDING_BREAK",1f, 1f)
                                        } else {
                                            player.sound("BLOCK_NOTE_BLOCK_DIDGERIDOO",1f, 1f)
                                        }
                                        return
                                    }
                                    IconType.LAST_PAGE -> {
                                        if (page != 0) {
                                            page -= 1
                                            inv.contents = contents[page]
                                            player.sound("BLOCK_SCAFFOLDING_BREAK",1f, 1f)
                                        } else {
                                            player.sound("BLOCK_NOTE_BLOCK_DIDGERIDOO",1f, 1f)
                                        }
                                        return
                                    }
                                    IconType.ITEM -> {
                                        if (e.isLeftClick && e.currentItem != null) {
                                            if (itemPack.shopItemNode.isNotEmpty()) {
                                                for (i2 in itemPack.shopItemNode) {
                                                    if (value(e.rawSlot, i2.packID) == cache[key(e.rawSlot, page)]) {
                                                        playerData?.let { PlayerData ->
                                                            if (i2.price <= PlayerData.points) {

                                                                BankManage.getGlobalShopLimitCache().forEach { global ->
                                                                    if (i2.packID == global.ShopId) {

                                                                        i2.global?.let { sGlobalData ->
                                                                            if (sGlobalData.limit <= global.GBuy_amt) {
                                                                                player.sendLang("玩家-积分商店-全局限制不足", global.GBuy_amt)
                                                                                return
                                                                            }
                                                                        }

                                                                        if (runCheck(i2, global, true)) return

                                                                        // 如果能运行到这里，代表这个玩家没有购买过这个物品
                                                                        i2.player?.let { s ->
                                                                            PlayerData.pointsData.add(
                                                                                PointsData(
                                                                                    i2.packID,
                                                                                    1,
                                                                                    System.currentTimeMillis() + s.time
                                                                                )
                                                                            )
                                                                        }
                                                                        global.GBuy_amt += 1
                                                                        playerData.takePoints(i2.price.toDouble())
                                                                        PlayerData.updateData() // 更新数据库
                                                                        if (i2.reward.isNotEmpty()) {
                                                                            Shop.getRewardData(i2.reward)?.let { reward ->
                                                                                reward.runReward().also { node ->
                                                                                    KetherAPI.instantKether(player, node.command.replacePlaceholder(player))
                                                                                }
                                                                            } ?: GeekItemRank.debug("&c奖池错误")
                                                                        }
                                                                        if (i2.action.isNotEmpty()){
                                                                            player.sendLang("玩家-积分商店-购买成功", i2.displayName)
                                                                            KetherAPI.instantKether(player, i2.action.replacePlaceholder(player))
                                                                        }
                                                                        return
                                                                    }
                                                                }

                                                                if (runCheck(i2)) return


                                                                // 全新购买的情况下
                                                                i2.global?.let { s ->
                                                                    BankManage.addGlobalShopLimitCache(
                                                                        GlobalData(
                                                                            i2.packID,
                                                                            1,
                                                                            System.currentTimeMillis() + s.time
                                                                        ), true
                                                                    )
                                                                }
                                                                i2.player?.let { s ->
                                                                    PlayerData.pointsData.add(
                                                                        PointsData(
                                                                            i2.packID,
                                                                            1,
                                                                            System.currentTimeMillis() + s.time
                                                                        )
                                                                    )
                                                                }
                                                                playerData.takePoints(i2.price.toDouble())
                                                                PlayerData.updateData() // 更新数据库

                                                                if (i2.reward.isNotEmpty()) {
                                                                    Shop.getRewardData(i2.reward)?.let { reward ->
                                                                        reward.runReward().also { node ->
                                                                            KetherAPI.instantKether(player, node.command.replacePlaceholder(player))
                                                                        }
                                                                    } ?: GeekItemRank.debug("&c奖池错误")
                                                                }
                                                                if (i2.action.isNotEmpty()){
                                                                    player.sendLang("玩家-积分商店-购买成功", i2.displayName)
                                                                    KetherAPI.instantKether(player, i2.action.replacePlaceholder(player))
                                                                }

                                                            } else player.sendLang("玩家-积分商店-积分不足", i2.price-PlayerData.points)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else -> {}
                                }
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

                player.updateInventory()
                if (player == e.player) {
                    HandlerList.unregisterAll(this)
                    Menu.isOpen.removeIf { it === player }
                }
            }
        }, GeekItemRank.instance)
    }
    private fun runCheck(node: ShopItemNode, global: GlobalData? = null, isGlobal: Boolean = false): Boolean {
        playerData?.pointsData?.forEach {
            if (node.packID == it.ShopId) { // 匹配到玩家有过购买
                node.player?.let { sPlayerData ->
                    if (sPlayerData.limit <= it.Buy_amt) {
                        player.sendLang("玩家-积分商店-个人限制不足", it.Buy_amt)
                        return true
                    }
                }
                if (isGlobal) global!!.GBuy_amt += 1
                it.Buy_amt += 1
                playerData.takePoints(node.price.toDouble())

                playerData.updateData() // 更新数据库
                if (node.reward.isNotEmpty()) {
                    Shop.getRewardData(node.reward)?.let { reward ->
                        reward.runReward().also { node ->
                            KetherAPI.instantKether(player, node.command.replacePlaceholder(player))
                        }
                    } ?: GeekItemRank.debug("&c奖池错误")
                }
                if (node.action.isNotEmpty()){
                    player.sendLang("玩家-积分商店-购买成功", node.displayName)
                    KetherAPI.instantKether(player, node.action.replacePlaceholder(player))
                }
                return true
            }
        }
        return false
    }

    override fun build() {
        var item = inv.contents
        var itemSize = itemPack.shopItemNode.size
        if (itemSize == 0) return
        while (itemSize > 0) {
            for ((index, value) in tag.stringLayout.withIndex()) {
                if (value != ' ') {
                    tag.micon.forEach { micon ->
                        if (micon.cIcon == value && micon.type == IconType.ITEM) {
                            if (itemSize > 0) {
                                val packIndex = itemPack.shopItemNode.size - itemSize
                                val node = itemPack.shopItemNode[packIndex]
                                cache[key(index, contents.size)] = value(index, node.packID)
                                val itemStack = try {
                                    if (node.mate.contains("IA:", ignoreCase = true) && HookPlugin.itemsAdder.isHook
                                    ) {
                                        HookPlugin.itemsAdder.getItem(node.mate.substring(3))
                                    } else {
                                        ItemStack(Material.valueOf(node.mate.uppercase()), 1, micon.data.toShort())
                                    }
                                } catch (ing: IllegalArgumentException) {
                                    ItemStack(Material.BOOK, 1)
                                }
                                val itemMeta = itemStack.itemMeta
                                if (itemMeta != null) {
                                    itemMeta.setDisplayName(micon.display.replace("[item_name]", node.displayName))
                                    itemMeta.lore = node.parseItemInfo(micon.lore, playerData?.pointsData).apply { addAll(0, node.lore) }
                                }
                                itemStack.itemMeta = itemMeta
                                item[index] = itemStack
                                itemSize--
                            }
                        }
                    }
                }
            }
            contents.add(item)
            item = inv.contents
        }
    }

}