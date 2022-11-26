package me.geek.bank.common.menu.action

import me.geek.bank.GeekItemRank
import me.geek.bank.api.bank.BankManage
import me.geek.bank.api.bank.BankManage.addQuitItem
import me.geek.bank.common.menu.ActionBase
import me.geek.bank.common.menu.IconType
import me.geek.bank.common.menu.Menu
import me.geek.bank.common.menu.Menu.sound
import me.geek.bank.common.menu.Session
import me.geek.bank.utils.catcher.Chat
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory

import taboolib.module.nms.i18n.I18n


/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
class RankMenu(
    override val player: Player,
    override val tag: Session,
    override val inv: Inventory
): ActionBase() {

    private val playerData = BankManage.getPlayerData(player.uniqueId)

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
                                        if (e.isRightClick && e.currentItem != null) {
                                            playerData?.let {
                                                it.ItemList.forEach { pack ->
                                                    if (value(e.rawSlot, pack.itemUid) == cache[key(e.rawSlot, page)]) {
                                                        player.closeInventory()
                                                        this@RankMenu.cache.clear()
                                                        this@RankMenu.inv.clear()
                                                        this@RankMenu.contents.clear()
                                                        player.addQuitItem(pack)
                                                        Chat(player).start()
                                                    }
                                                }
                                            }
                                        }
                                        if (e.isLeftClick && e.currentItem != null) {
                                            playerData?.let {
                                                it.ItemList.forEach { pack ->
                                                    if (value(e.rawSlot, pack.itemUid) == cache[key(e.rawSlot, page)]) {
                                                        player.closeInventory()
                                                        this@RankMenu.cache.clear()
                                                        this@RankMenu.inv.clear()
                                                        this@RankMenu.contents.clear()
                                                        RankAddItem(player, pack.itemStack)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    IconType.ADD_ITEM -> {
                                        if (e.isLeftClick && e.currentItem != null) {
                                            playerData?.let {
                                                player.closeInventory()
                                                this@RankMenu.cache.clear()
                                                this@RankMenu.inv.clear()
                                                this@RankMenu.contents.clear()
                                                RankAddItem(player)
                                            }
                                        }
                                    }
                                    IconType.SHOP -> {
                                        Menu.getMenuCache().map {
                                            if (it.value.type == "shop") {
                                                player.closeInventory()
                                                this@RankMenu.cache.clear()
                                                this@RankMenu.inv.clear()
                                                this@RankMenu.contents.clear()
                                                PointsShop(player, it.value,  Menu.build(player, it.value.session))
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






    override fun build() {
        this.playerData?.let {
            var item = inv.contents
            if (it.ItemList.isEmpty()) return
            var playerItemSize = it.ItemList.size

            while (playerItemSize > 0) {
                for ((index, value) in tag.stringLayout.withIndex()) {
                    if (value != ' ') {
                        tag.micon.forEach { micon ->
                            if (micon.cIcon == value && micon.type == IconType.ITEM) {
                                if (playerItemSize > 0) {

                                    val itemIndex = it.ItemList.size - playerItemSize
                                    cache[key(index, contents.size)] = value(index, it.ItemList[itemIndex].itemUid)

                                    val itemStack = it.ItemList[itemIndex].itemStack.clone()

                                    val itemMeta = itemStack.itemMeta

                                    if (itemMeta != null) {

                                        val locLore = it.ItemList[itemIndex].parseItemInfo(micon.lore)

                                        if (itemMeta.hasDisplayName()) {
                                            itemMeta.setDisplayName(micon.display.replace("[item_name]",itemMeta.displayName))
                                        } else itemMeta.setDisplayName(micon.display.replace("[item_name]", I18n.instance.getName(itemStack)))

                                        if (itemMeta.hasLore()) {
                                            locLore.addAll(0, itemMeta.lore!!)
                                            itemMeta.lore = locLore
                                        } else {
                                            itemMeta.lore = locLore
                                        }
                                    }
                                    itemStack.itemMeta = itemMeta
                                    item[index] = itemStack
                                    playerItemSize--
                                }
                            }
                        }
                    }
                }
                contents.add(item)
                item = inv.contents
                GeekItemRank.debug("页面大小: ${contents.size}")
            }
        }
    }
}