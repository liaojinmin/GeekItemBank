package me.geek.bank.scheduler.task

import me.geek.bank.GeekItemRank
import me.geek.bank.api.bank.BankManage
import me.geek.bank.common.data.SqlManager
import me.geek.bank.common.settings.SetTings
import me.geek.bank.scheduler.sql.actions
import me.geek.bank.scheduler.sql.use
import taboolib.common.platform.function.submitAsync
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/11/14
 *
 **/
class PlayerUpTask {
    // 此方法不打算使用
    init {
        submitAsync(delay = SetTings.update.toLong() * 20, period = SetTings.update.toLong() * 20) {
            var res = 0
            measureTimeMillis {
                val data = BankManage.getPlayerCache().entries.iterator()
                SqlManager.getConnection().use {
                    this.prepareStatement(
                        "UPDATE `pack_data` SET `data`=? WHERE `player_uuid`=?;"
                    ).actions { p ->
                        while (data.hasNext()) {
                            val entry = data.next().value
                            p.setBytes(1, entry.toByteArray())
                            p.setString(2, entry.playerUid.toString())
                            p.addBatch()
                        }
                        res = p.executeBatch().size
                    }
                }
            }.also {
                GeekItemRank.debug("&7数据维护事务 &8| &f成功更新 $res 条玩家数据数据... §8(耗时 $it Ms)")
            }
        }
    }
}