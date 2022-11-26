package me.geek.bank.common.data


import com.google.gson.annotations.Expose
import me.geek.bank.common.bank.PlayerItemPack
import me.geek.bank.common.bank.Bank
import me.geek.bank.common.bank.Bank.matchNode
import me.geek.bank.utils.ClassSerializable
import java.math.BigDecimal
import java.util.*
import java.util.regex.Pattern

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
data class PlayerData(
    val playerName: String,

    val playerUid: UUID,

    // 不要直接修改该值
    var points: Double = 0.0,

    var Day: Int = 0,

    val ItemList: MutableList<PlayerItemPack> = mutableListOf(),

    val pointsData: MutableList<PointsData> = mutableListOf()
) {
    fun givePoints(points: Double) {
        val b1 = BigDecimal(points.toString())
        val b2 = BigDecimal(this.points.toString())
        this.points = b1.add(b2).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
    }
    fun takePoints(points: Double) {
        val b1 = BigDecimal(points.toString())
        val b2 = BigDecimal(this.points.toString())
        this.points = b2.subtract(b1).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
    }


    fun getDoublePoints(): Double {
        var data = 0.0
        if (this.ItemList.isNotEmpty()) {
            this.ItemList.forEach {
                val node = it.itemStack.matchNode()
                val amt = if (node != null) { node.give_Price * 0.1 } else Bank.getBankCache().DefaultGive_Price * 0.1
                data += (it.amount * amt)
            }
        }
        return BigDecimal(data).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
    }

    @Expose
    private val regex = Pattern.compile("\\d+\\.?\\d?\\d")

    fun toByteArray(): ByteArray {
        return ClassSerializable.gsonSerialize(this)
    }
    fun pointsFormat(isPoints: Boolean = true): String {
        val matcher = regex.matcher(if (isPoints) this.points.toString() else getDoublePoints().toString())
        var var1 = ""
        if (matcher.find()) {
            var1 = matcher.group()
        }
        return var1
    }

}
