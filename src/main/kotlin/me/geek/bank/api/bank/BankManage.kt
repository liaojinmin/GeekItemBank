package me.geek.bank.api.bank

import me.geek.bank.common.data.GlobalData
import me.geek.bank.common.data.PlayerData
import me.geek.bank.common.data.SqlManager
import me.geek.bank.common.bank.PlayerItemPack
import me.geek.bank.common.settings.SetTings
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
object BankManage {

    /**
     * 玩家在线缓存
     */
    private val PlayerCache: MutableMap<UUID, PlayerData> = ConcurrentHashMap()
    fun getPlayerCache(): MutableMap<UUID, PlayerData>  { return PlayerCache }

    /**
     * 玩家取出物品暂存器
     */
    private val PlayerQuitItem: MutableMap<Player, PlayerItemPack> = ConcurrentHashMap()

    /**
     * 全局限制缓存
     */
    private val GlobalShopLimit: MutableList<GlobalData> = mutableListOf()

    fun getGlobalShopLimitCache(): MutableList<GlobalData> { return GlobalShopLimit }
    fun setGlobalShopLimitCache(data: MutableList<GlobalData>) { GlobalShopLimit.addAll(data) }
    fun addGlobalShopLimitCache(data: GlobalData, insert: Boolean = false) {
        GlobalShopLimit.add(data)
        if (insert) SqlManager.insertGlobalData(data)
    }

    fun GlobalData.update() {
        SqlManager.updateGlobalData(this)
    }
    fun MutableList<GlobalData>.update() {
        SqlManager.updateGlobalData(this)
    }





    fun Player.getQuitItem(): PlayerItemPack? {
        return PlayerQuitItem[this]
    }

    fun Player.addQuitItem(itemPack: PlayerItemPack) {
        PlayerQuitItem[this] = itemPack
    }

    fun Player.remQuitItem() {
        PlayerQuitItem.remove(this)
    }

    /**
     * 设置玩家数据
     */
    fun PlayerData.setPlayerData() {
        PlayerCache[this.playerUid] = this
    }

    /**
     * 删除玩家数据
     */
    fun PlayerData.remPlayerData() {
        PlayerCache.remove(this.playerUid)
    }

    /**
     * 往玩家数据中添加物品包
     */
    fun PlayerData.addItemPack(pack: PlayerItemPack) {
        PlayerCache[this.playerUid]?.ItemList?.add(pack)
    }

    /**
     * 往玩家数据中删除物品包
     */
    fun PlayerData.remItemPack(pack: PlayerItemPack) {
        PlayerCache[this.playerUid]?.ItemList?.remove(pack)
    }

    /**
     * 往玩家数据中删除物品包
     */
    fun Player.remItemPack(pack: PlayerItemPack) {
        PlayerCache[this.uniqueId]?.ItemList?.remove(pack)
    }

    /**
     * 获取玩家数据
     */
    fun getPlayerData(Uid: UUID): PlayerData? {
        return PlayerCache[Uid]
    }

    /**
     * 玩家扩展函数，获取玩家数据
     */
    fun Player.getPlayerData(): PlayerData? {
        return getPlayerData(this.uniqueId)
    }



    /* 数据操作 */

    /**
     * 更新整个玩家数据
     */
    fun PlayerData.updateData() {
        if (SetTings.update <= 0) {
            SqlManager.updatePlayerData(this)
        }
    }


}