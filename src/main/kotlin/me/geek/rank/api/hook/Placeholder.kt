package me.geek.rank.api.hook

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.geek.rank.GeekItemRank
import me.geek.rank.api.rank.RankManage.getPlayerData
import me.geek.rank.scheduler.task.PointsTopTask
import org.bukkit.entity.Player
import java.util.regex.Pattern

/**
 * 作者: 老廖
 * 时间: 2022/11/8
 *
 **/
class Placeholder : PlaceholderExpansion() {

    override fun onPlaceholderRequest(player: Player, params: String): String {
        val index = params.filter { it.isDigit() }.toInt()
        return when (params) {
            "points" -> player.getPlayerData()!!.pointsFormat()
            "double" -> player.getPlayerData()!!.pointsFormat(false)
            "top_amt_$index" -> PointsTopTask.getPointsTop[index]?.let { it.split(";")[1] } ?: "暂无"
            "top_name_$index" -> PointsTopTask.getPointsTop[index]?.let { it.split(";")[0] } ?: "暂无"
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