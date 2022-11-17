package me.geek.rank.common.rank

import com.google.gson.annotations.Expose
import me.geek.rank.utils.serializeItemStacks
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
data class PlayerItemPack(
    override val itemUid: UUID,
    @Expose
    override val itemStack: ItemStack,
    override var amount: Int = 0,
    val itemStackString: String = itemStack.serializeItemStacks()
): ItemPackPlaceholder()

