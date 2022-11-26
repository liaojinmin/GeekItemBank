package me.geek.bank.common.bank

import com.google.gson.annotations.Expose
import me.geek.bank.common.bank.Bank.matchNode
import org.jetbrains.annotations.NotNull
import taboolib.module.nms.i18n.I18n

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
abstract class ItemPackPlaceholder : ItemPack {



    @Expose
    private val itemName = Regex("(\\{|\\[)(item_name|物品名称)(}|])")


    @Expose
    private val amounts = Regex("(\\{|\\[)(amount|数量)(}|])")


    @Expose
    private val givePrice = Regex("(\\{|\\[)(givePrice|存入价格)(}|])")

    @Expose
    private val takePrice = Regex("(\\{|\\[)(takePrice|取出价格)(}|])")

    fun parseItemInfo(@NotNull iconLore: List<String>): MutableList<String> {
        val list = mutableListOf<String>()
        val nodes: BankItemNode? = this.itemStack.matchNode()
        iconLore.forEach {
            when {
                it.contains(itemName) -> {
                    val meta = this.itemStack.itemMeta
                    if (meta != null) {
                        val display = if (meta.hasDisplayName()) { meta.displayName } else I18n.instance.getName(this.itemStack)
                        list.add(it.replace(itemName, display))
                    }
                }
                it.contains(amounts) -> list.add(it.replace(amounts, this.amount.toString()))
                it.contains(givePrice) -> {
                    if (nodes == null) {
                        list.add(it.replace(givePrice, Bank.getBankCache().DefaultGive_Price.toString()))
                    } else list.add(it.replace(givePrice, nodes.give_Price.toString()))
                }
                it.contains(takePrice) -> {
                    if (nodes == null) {
                        list.add(it.replace(takePrice, Bank.getBankCache().DefaultTake_Price.toString()))
                    } else list.add(it.replace(takePrice, nodes.take_Price.toString()))
                }
                else -> list.add(it)
            }
        }
        return list
    }
    private fun getNode(): BankItemNode? {
        Bank.getBankCache().ItemDataNode.forEach { node ->
            if (node.itemStack == this.itemStack) {
               return node
            }
        }
        return null
    }
}