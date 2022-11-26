package me.geek.rank.common.shop

import me.geek.rank.GeekItemRank
import me.geek.rank.common.shop.reward.PrizesNode
import me.geek.rank.common.shop.reward.RewardData
import me.geek.rank.utils.Expiry
import me.geek.rank.utils.FileUtils
import me.geek.rank.utils.colorify
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.SecuredFile
import java.io.File
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/11/13
 *
 **/
object Shop {

    private val REWARD_CACHE: MutableMap<String, RewardData> = mutableMapOf()
    fun getRewardData(node: String): RewardData? {
        return REWARD_CACHE[node]
    }


    // 商店物品积分配置缓存
    private lateinit var SHOP_CACHE: ShopItemData

    fun getShopCache() : ShopItemData {
        return SHOP_CACHE
    }

    fun onLoad() {
        val shop = FileUtils.forFile(saveDefaultShop)
        val item = mutableListOf<ShopItemNode>()
        measureTimeMillis {
            for (it in shop) {
                val var1 = SecuredFile.loadConfiguration(it)

                if (var1.getString("type") != null && var1.getString("type") == "reward") {
                    val node = var1.getString("NodeName") ?: "错误的节点名称配置"
                    var1.getConfigurationSection("Prizes")?.let { section ->
                        val prizes = mutableListOf<PrizesNode>().apply {
                            section.getKeys(false).forEach { key ->
                                val name = section.getString("$key.displayName")?.colorify() ?: "错误的名称配置"
                                val chance = section.getDouble("$key.Chance")
                                val command = section.getString("$key.commands")?.colorify()
                                add(PrizesNode(name, chance, command!!))
                            }
                        }
                        GeekItemRank.debug("$node 奖池大小: ${prizes.size}")
                        REWARD_CACHE[node] = RewardData(node, prizes)
                    }
                    continue
                }

                val p = var1.getInt("DefaultPlayer_limit")
                val p2 = var1.getString("DefaultPlayer_time") ?: "7d"
                val g = var1.getInt("DefaultGlobal_limit")
                val g2 = var1.getString("DefaultGlobal_time") ?: "7d"
                var1.getConfigurationSection("shopInfo")?.let { section ->
                    section.getKeys(false).forEach { key ->
                        val displayName = section.getString("$key.displayName")?.colorify() ?: "错误的商品命名"
                        val lore = section.getStringList("$key.lore").colorify()
                        val mate = section.getString("$key.mate") ?: "STONE"
                        val packId = section.getString("$key.packId") ?: ""
                        val price = section.getInt("$key.Price")

                        val global: SGlobalLimit? = section.getString("$key.Limit.Global.time")?.let {
                            if (section.getInt("$key.Limit.Global.limit") <= 0) return
                            SGlobalLimit(section.getInt("$key.Limit.Global.limit"), Expiry.getExpiryMillis(section.getString("$key.Limit.Global.time") ?: "7天", false))
                        }

                        val player: SPlayerLimit? = section.getString("$key.Limit.Player.time")?.let {
                            if (section.getInt("$key.Limit.Player.limit") <= 0) return
                             SPlayerLimit(section.getInt("$key.Limit.Player.limit"), Expiry.getExpiryMillis(section.getString("$key.Limit.Player.time") ?: "7天", false))
                        }

                        val action = section.getString("$key.action") ?: ""
                        val reward = section.getString("$key.reward") ?: ""
                        item.add(ShopItemNode(displayName.colorify(), lore, mate, packId, price, global, player, action.colorify(), reward))
                    }
                }
                SHOP_CACHE = ShopItemData(p, Expiry.getExpiryMillis(p2, false), g, Expiry.getExpiryMillis(g2, false), item)
            }
        }.also {
            GeekItemRank.say("§7已加载 &f${item.size} &7个商店物品配置... §8(耗时 $it Ms)")
            GeekItemRank.say("§7已加载 &f${REWARD_CACHE.size} &7个奖池配置... §8(耗时 $it Ms)")
        }
    }


    private val saveDefaultShop by lazy {
        val dir = File(GeekItemRank.instance.dataFolder, "shop")
        if (!dir.exists()) {
            releaseResourceFile("shop/item.yml", true)
            releaseResourceFile("shop/reward/def.yml", true)
        }
        dir
    }
}