package me.geek.rank.common.rank

import me.geek.rank.GeekItemRank
import me.geek.rank.api.hook.HookPlugin
import me.geek.rank.utils.FileUtils
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.SecuredFile
import taboolib.platform.util.buildItem
import java.io.File
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
object Rank {

    // 银行物品积分配置缓存
    private lateinit var RANK_CACHE: RankItemData

    fun getRankCache(): RankItemData {
        return RANK_CACHE
    }

    fun ItemStack.matchNode(): RankItemNode? {
        getRankCache().ItemDataNode.forEach {
            if (it.itemStack.itemMeta == this.itemMeta && it.itemStack.type == this.type) {
                return it
            }
        }
        return null
    }

    fun onLoad() {
        val rank = FileUtils.forFile(saveDefaultRank)
        val item = mutableListOf<RankItemNode>()
        measureTimeMillis {
            rank.forEach {
                val var1 = SecuredFile.loadConfiguration(it)
                val give = var1.getDouble("DefaultGive_Price")
                val take = var1.getDouble("DefaultTake_Price")
                var1.getConfigurationSection("bankInfo")?.let { section ->
                    section.getKeys(false).forEach { key ->
                        val mate = section.getString("$key.material") ?: "STONE"
                        val g = section.getDouble("$key.give_Price")
                        val t = section.getDouble("$key.take_Price")
                        item.add(RankItemNode(key, buildItems(section.getString("$key.type") ?: "Minecraft", mate), g, t))
                    }
                }
                RANK_CACHE = RankItemData(give, take, item)
            }
        }.also {
            GeekItemRank.say("§7已加载 &f${item.size} &7个物品配置... §8(耗时 $it Ms)")
        }

    }





    private fun buildItems(type: String, mate: String): ItemStack {
        return when {
            type.contains(IA) -> {
                HookPlugin.itemsAdder.getItem(mate)
            }
            type.contains(MM) -> {
                HookPlugin.mythicMobs.getItem(mate)
            }
            else -> {
                buildItem(XMaterial.STONE) {
                    val ac = mate.split(":")
                    setMaterial(XMaterial.valueOf(ac[0].uppercase()))
                    if (ac.size == 2) {
                        damage = ac[1].toIntOrNull() ?: 0
                    }

                }
            }
        }

    }







    private val IA = Regex("(IA|ia|ItemsAdder)")
    private val MM = Regex("(MM|mm|MythicMobs)")

    private val saveDefaultRank by lazy {
        val dir = File(GeekItemRank.instance.dataFolder, "rank")
        if (!dir.exists()) {
            releaseResourceFile("rank/Item.yml", true)
        }
        dir
    }
}