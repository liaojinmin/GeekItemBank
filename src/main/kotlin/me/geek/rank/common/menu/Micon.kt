package me.geek.rank.common.menu

import me.geek.rank.utils.colorify
import taboolib.library.configuration.ConfigurationSection
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
data class Micon(
    val icon: String,
    val cIcon: Char,
    val type: IconType,
    val mats: String,
    val data: Int,
    val display: String,
    val lore: List<String>,
) {
    constructor(icon: String, obj: ConfigurationSection) : this(
        icon,
        cIcon = icon[0],
        IconType.valueOf(obj.getString("Type", "NORMAL")!!.uppercase(Locale.ROOT)),
        obj.getString("display.mats","PAPER")!!,
        obj.getInt("display.data",0),
        obj.getString("display.name", " ")!!.colorify(),
        obj.getStringList("display.lore").joinToString().colorify().split(", "),
    )
}
