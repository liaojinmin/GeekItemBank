package me.geek.rank.common.data

import me.geek.rank.common.settings.SetTings
import me.geek.rank.scheduler.sql.*
import me.geek.rank.utils.ClassSerializable

import org.bukkit.entity.Player
import taboolib.common.platform.function.submitAsync
import java.sql.Connection


/**
 * 作者: 老廖
 * 时间: 2022/11/14
 *
 **/
object SqlManager {

    private val dataSub by lazy {
        if (SetTings.sqlConfig.use_type.equals("mysql", ignoreCase = true)){
            return@lazy Mysql(SetTings.sqlConfig)
        } else return@lazy Sqlite(SetTings.sqlConfig)
    }

    fun getConnection(): Connection {
        return dataSub.getConnection()
    }
    fun closeData() {
        dataSub.onClose()
    }
    fun start() {
        dataSub.onStart()
        if (dataSub.isActive) {
            dataSub.createTab {
                getConnection().use {
                    createStatement().action { statement ->
                        if (dataSub is Mysql) {
                            statement.addBatch(SqlTab.MYSQL_1.tab)
                            statement.addBatch(SqlTab.MYSQL_2.tab)
                        } else {
                            statement.addBatch("PRAGMA foreign_keys = ON;")
                            statement.addBatch("PRAGMA encoding = 'UTF-8';")
                        }
                        statement.executeBatch()
                    }
                }
            }
        }
    }

    /* 玩家数据 - 增删改查 */

    private fun insertPlayerData(data: PlayerData) {
        submitAsync {
            getConnection().use {
                this.prepareStatement(
                    "INSERT INTO pack_data(`player_uuid`,`username`,`data`) VALUES(?,?,?)"
                ).actions { p ->
                    p.setString(1, data.playerUid.toString())
                    p.setString(2, data.playerName)
                    p.setBytes(3, data.toByteArray())
                    p.executeUpdate()
                }
            }
        }
    }


    fun updatePlayerData(data: PlayerData) {
        submitAsync {
            getConnection().use {
                this.prepareStatement(
                    "UPDATE `pack_data` SET `data`=? WHERE `player_uuid`=?;"
                ).actions { p ->
                    p.setBytes(1, data.toByteArray())
                    p.setString(2, data.playerUid.toString())
                    p.executeUpdate()
                }
            }
        }
    }

    @Synchronized
    fun selectPlayerData(player: Player): PlayerData {
        var data: PlayerData? = null
            getConnection().use {
                this.prepareStatement(
                    "SELECT `data` FROM `pack_data` WHERE player_uuid=?;"
                ).actions { p ->
                    p.setString(1, player.uniqueId.toString())
                    val res = p.executeQuery()
                    if (res.next()) {
                        val var10 = ClassSerializable.gsonUnSerialize(res.getBytes("data"))
                        if (var10 is PlayerData) data = var10
                    } else data = player.defaultData().apply { insertPlayerData(this) }
                }
            }
        return data!!
    }

    private fun Player.defaultData(): PlayerData {
        return PlayerData(this.displayName, this.uniqueId, 0.0)
    }


    fun insertGlobalData(data: GlobalData) {
        getConnection().use {
            this.prepareStatement("INSERT INTO global_data(`shop`,`amt`,`time`) VALUES(?,?,?);"
            ).actions { p ->
                p.setString(1, data.ShopId)
                p.setInt(2, data.GBuy_amt)
                p.setString(3, data.resTime.toString())
                p.execute()
            }
        }
    }
    fun updateGlobalData(data: GlobalData) {
        getConnection().use {
            this.prepareStatement("UPDATE `global_data` SET `amt`=? WHERE `shop`=?;"
            ).actions { p ->
                p.setString(2, data.ShopId)
                p.setInt(1, data.GBuy_amt)
                p.execute()
            }
        }
    }
    fun updateGlobalData(data: Collection<GlobalData>) {
        getConnection().use {
            this.prepareStatement("UPDATE `global_data` SET `amt`=? WHERE `shop`=?;"
            ).actions { p ->
                data.forEach {
                    p.setString(2, it.ShopId)
                    p.setInt(1, it.GBuy_amt)
                    p.addBatch()
                }
                p.executeBatch()
            }
        }
    }
    fun deleteGlobalData(data: Collection<String>) {
        getConnection().use {
            this.prepareStatement("DELETE FROM `global_data` WHERE `shop`=?;"
            ).actions { p ->
                data.forEach {
                    p.setString(1, it)
                    p.addBatch()
                }
                p.executeBatch()
            }
        }
    }
    fun selectGlobalData(): MutableList<GlobalData> {
        val data: MutableList<GlobalData> = mutableListOf()
        getConnection().use {
            this.prepareStatement(
                "SELECT `shop`,`amt`,`time` FROM `global_data` WHERE id;"
            ).actions { p ->
                val res = p.executeQuery()
                while (res.next()) {
                    val shop = res.getString("shop")
                    val amt = res.getInt("amt")
                    val time = res.getString("time")
                    data.add(GlobalData(shop, amt, time.toLong()))
                }
            }
        }
        return data
    }



    enum class SqlTab(val tab: String) {
        MYSQL_1("CREATE TABLE IF NOT EXISTS `global_data` (" +
                " `id` integer NOT NULL AUTO_INCREMENT, " +
                " `shop` varchar(16) NOT NULL," +
                " `amt` integer NOT NULL," +
                " `time` BIGINT(20) NOT NULL," +
                "PRIMARY KEY (`id`, `shop`)" +
                ");"),
        MYSQL_2("CREATE TABLE IF NOT EXISTS `pack_data` (" +
                " `player_uuid` CHAR(36) NOT NULL UNIQUE," +
                " `username` varchar(16) NOT NULL," +
                " `data` longblob NOT NULL," +
                "PRIMARY KEY (`player_uuid`, `username`)" +
                ");"),
    }

}