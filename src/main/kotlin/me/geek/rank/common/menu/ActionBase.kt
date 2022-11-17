package me.geek.rank.common.menu

import me.geek.rank.GeekItemRank
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync
import taboolib.platform.compat.replacePlaceholder
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/11/8
 *
 **/
abstract class ActionBase : Action {

    /**
     * 当前页面
     */
    var page = 0

    /**
     * 菜单页面缓存
     */
    val contents: MutableList<Array<ItemStack>> = ArrayList()

    /**
     * 图标索引缓存
     */
    val cache: MutableMap<String, String> = HashMap()

    /**
     * 实列化后执行动作
     */
    abstract fun action()

    /**
     * 菜单启动构建物品
     */
    abstract fun build()

    fun parsePlaceholder() {

            tag?.let {
                for ((index, value) in it.stringLayout.withIndex()) {
                    if (value != ' ') {
                        for (icon in it.micon) {
                            if (icon.cIcon == value) {
                                inv?.let { inventory ->
                                    inventory.contents[index]?.let { st ->
                                        GeekItemRank.debug("node-ActionBase:50 -> item[index] != null")
                                        val meta = st.itemMeta
                                        if (meta != null) {
                                            if (meta.hasDisplayName()) {
                                                GeekItemRank.debug("node-ActionBase:54 -> hasDisplayName()")
                                                meta.setDisplayName(meta.displayName.replacePlaceholder(player))
                                            }
                                            if (meta.hasLore()) {
                                                GeekItemRank.debug("node-ActionBase:54 -> hasLore()")
                                                val lore = meta.lore!!.replacePlaceholder(player)
                                                meta.lore = lore
                                                st.itemMeta = meta
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

    }

    /**
     *
     * @param index 图标索引位
     * @param Page 当前页面
     * @return 返回拼接字符串
     */
    fun key(index: Int, Page: Int): String {
        return "$index$Page"
    }

    /**
     *
     * @param index 图标索引位
     * @param item_id 唯一ID
     * @return 返回拼接字符串
     */
    fun value(index: Int, item_id: UUID): String {
        return "$index$item_id"
    }

    /**
     *
     * @param index 图标索引位
     * @param item_id 唯一ID
     * @return 返回拼接字符串
     */
    fun value(index: Int, item_id: String): String {
        return "$index$item_id"
    }


}