package me.geek.bank.common.menu
import org.bukkit.inventory.ItemStack

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
data class Session(
    val session: String,
    val title: String,
    val stringLayout: String,
    val size: Int,
    val bindings: String,
    val micon: Collection<Micon>,
    val type: String,
    val IconItem: Array<ItemStack>
) {



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Session
        if (session != other.session) return false
        if (title != other.title) return false
        if (stringLayout != other.stringLayout) return false
        if (size != other.size) return false
        if (micon != other.micon) return false
        if (type != other.type) return false
        if (!IconItem.contentEquals(other.IconItem)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = session.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + stringLayout.hashCode()
        result = 31 * result + size
        result = 31 * result + micon.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + IconItem.contentHashCode()
        return result
    }
}
