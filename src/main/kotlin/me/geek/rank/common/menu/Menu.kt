package me.geek.rank.common.menu

import me.geek.rank.GeekItemRank
import me.geek.rank.common.menu.action.PointsShop
import me.geek.rank.common.menu.action.RankMenu
import me.geek.rank.utils.FileUtils.forFile
import me.geek.rank.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XSound
import taboolib.module.configuration.SecuredFile
import taboolib.module.configuration.util.getMap
import java.io.File
import java.lang.IllegalArgumentException
import java.util.ArrayList
import java.util.HashMap
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
object Menu {

    private val AIR = ItemStack(Material.AIR)

    // key = 菜单名称 , value = 会话菜单
    private val MenuCache: MutableMap<String, Session> = HashMap()
    fun getMenuCache(): MutableMap<String, Session> {
        return MenuCache
    }

    // 缓存的菜单打开指令 key = 菜单绑定的命令  value = 菜单名称
    private val MenuCmd: MutableMap<String, String> = HashMap()

    @JvmField
    val isOpen: MutableList<Player> = ArrayList()

    /**
     * @param MenuID 菜单名称
     */
    fun Player.openMenu(MenuID: String) {
        val sess = MenuCache[MenuID]!!
        when (sess.type) {
            "rank" -> RankMenu(this, sess, build(this, MenuID))
            "shop" -> PointsShop(this, sess, build(this, MenuID))
        }
    }


    fun closeGui() {
        Bukkit.getOnlinePlayers().forEach { player: Player ->
            if (isOpen.contains(player)) {
                player.closeInventory()
            }
        }
    }

    /**
     * @param cmd 菜单绑定的指令
     * @return 菜单名称
     */
    fun getMenuCommand(cmd: String): String? {
        return MenuCmd[cmd]
    }
    fun Player.sound(name: String, volume: Float, potch: Float) {
        val sound: XSound = try {
            XSound.valueOf(name)
        } catch (e: Throwable) {
            GeekItemRank.say("未知音效: $name")
            return
        }
        sound.play(this, volume, potch)
    }

    /**
     * 为玩家构建指定页数的的界面
     * @param player 目标玩家
     * @param MenuID 菜单名称
     * @return 返回界面
     */
    @JvmStatic
    fun build(player: Player, MenuID: String): Inventory {

        val tag = MenuCache[MenuID]!!
        val item = tag.IconItem
        val inventory = Bukkit.createInventory(player, tag.size, tag.title)
        if (item.isNotEmpty()) {
            inventory.contents = item
        }
        return inventory
    }


    fun loadMenu() {
        val list = mutableListOf<File>()
        measureTimeMillis {
            list.also {
                it.addAll(forFile(saveDefaultMenu))
            }
            val icon = mutableListOf<Micon>()
            var menu: SecuredFile
            var menuTag: String
            var title: String
            var type: String
            var layout: String
            var size: Int
            var bindings: String
            list.forEach { file ->
                icon.clear()
                menu = SecuredFile.loadConfiguration(file)
                menuTag = file.name.substring(0, file.name.indexOf("."))
                title = menu.getString("TITLE")!!.colorify()
                type = menu.getString("TYPE")!!
                layout = menu.getStringList("Layout").toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace(", ", "")
                size = menu.getStringList("Layout").size * 9
                bindings = menu.getString("Bindings.Commands") ?: ""
                menu.getMap<String, ConfigurationSection>("Icons").forEach { (name, obj) ->
                    icon.add(Micon(name, obj))
                }
                val listIcon = ArrayList(icon)
                MenuCache[menuTag] = Session(menuTag, title, layout, size, bindings, listIcon, type, builds(listIcon, layout, size))
                MenuCmd[bindings] = menuTag
            }

        }.also {
            GeekItemRank.say("§7菜单界面加载完成... §8(耗时 $it ms)");
        }
    }









    private fun builds(var1: List<Micon>, Layout: String, size: Int): Array<ItemStack> {
        val item = mutableListOf<ItemStack>()
        try {
            var index = 0
            while (index < size) {
                if (Layout[index] != ' ') {
                    val iconId = Layout[index].toString()
                    item.add(index, item(iconId, var1))
                } else {
                    item.add(index, AIR)
                }
                index++
            }
        } catch (ignored: StringIndexOutOfBoundsException) { }
        return item.toTypedArray()
    }
    private fun item(iconID: String, miconObj: List<Micon>): ItemStack {
        for (icon in miconObj) {
            if (icon.icon == iconID) {
                if (icon.type == IconType.ITEM) {
                    return AIR
                }
                val itemStack = try {
                    ItemStack(Material.valueOf(icon.mats), 1, icon.data.toShort())
                } catch (ing: IllegalArgumentException) {
                    ItemStack(Material.STONE, 1)
                }

                val itemMeta = itemStack.itemMeta
                if (itemMeta != null) {
                    itemMeta.setDisplayName(icon.display.colorify())
                    if (icon.lore.size == 1 && icon.lore[0].isEmpty()) {
                        itemMeta.lore = null
                    } else {
                        itemMeta.lore = icon.lore
                    }
                    itemStack.itemMeta = itemMeta
                }
                return itemStack
            }
        }
        return AIR
    }


    private val saveDefaultMenu by lazy {
        val dir = File(GeekItemRank.instance.dataFolder, "menu")
        if (!dir.exists()) {
            arrayOf(
                "menu/def.yml",
                "menu/shop.yml",
            ).forEach { releaseResourceFile(it, true) }
        }
        dir
    }
}