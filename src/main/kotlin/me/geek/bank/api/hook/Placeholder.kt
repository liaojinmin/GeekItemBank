package me.geek.bank.api.hook

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.geek.bank.GeekItemRank
import me.geek.bank.api.bank.BankManage.getPlayerData
import me.geek.bank.scheduler.task.PointsTopTask
import org.bukkit.entity.Player

/**
 * 作者: 老廖
 * 时间: 2022/11/8
 *
 **/
class Placeholder : PlaceholderExpansion() {
    val regex = Regex("(top_name/?+|top_amt/?+)")

    override fun onPlaceholderRequest(player: Player, params: String): String {
        var index = 0
        if (params.contains(regex)) {
            index = params.filter { it.isDigit() }.toInt()
        }
        return when (params) {
            "points" -> player.getPlayerData()!!.pointsFormat()
            "double" -> player.getPlayerData()!!.pointsFormat(false)
            "top_amt_$index" -> PointsTopTask.getPointsTop()[index]?.let { it.split(";")[1] } ?: "暂无"
            "top_name_$index" -> PointsTopTask.getPointsTop()[index]?.let { it.split(";")[0] } ?: "暂无"
            else -> return "0"
        }
    }



    override fun getIdentifier(): String {
        return "GeekItemRank"
    }

    override fun getAuthor(): String {
        return "极客天上工作室"
    }

    override fun getVersion(): String {
        return GeekItemRank.VERSION.toString()
    }


}