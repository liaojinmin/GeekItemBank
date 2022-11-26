package me.geek.bank.command

import me.geek.bank.GeekItemRank
import me.geek.bank.api.bank.BankManage
import me.geek.bank.api.bank.BankManage.addItemPack
import me.geek.bank.api.bank.BankManage.getPlayerData
import me.geek.bank.api.bank.BankManage.updateData
import me.geek.bank.common.bank.PlayerItemPack
import me.geek.bank.common.bank.Bank
import me.geek.bank.common.bank.Bank.matchNode
import org.bukkit.Bukkit

import org.bukkit.Material

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import taboolib.common.platform.command.*
import taboolib.common.platform.function.adaptCommandSender


import taboolib.module.chat.TellrawJson
import java.util.*


@CommandHeader(name = "GeekItemBank", aliases = ["gkitem", "bank"], permissionDefault = PermissionDefault.TRUE )
object CmdCore {



    @CommandBody(permission = "GeekCraft.bank.command.admin")
    val points = subCommand {
        dynamic("动作") {
            suggestion<CommandSender> { _, _ ->
                listOf("give","take","set")
            }
            dynamic("玩家") {
                suggestion<CommandSender>(uncheck = true) {_, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic("数量") {
                    suggestion<CommandSender>(uncheck = true) {_, _ ->
                        listOf("1","5","10","20","50","100")
                    }
                    execute<CommandSender> { sender, context, _ ->
                        val text = context.args()
                        if (text.size >= 4) {
                            val player = Bukkit.getOfflinePlayer(text[2])
                            val amt = text[3].toDouble()
                            if (player.isOnline) {
                                when (text[1]) {
                                    "give" -> player.player?.let {
                                        it.getPlayerData()!!.givePoints(amt)
                                        sender.sendMessage("§8[§6GeekItemBank§8] §a成功给予 §f${text[2]} §8* §f$amt §a积分")
                                    }
                                    "take" -> player.player?.let {
                                        it.getPlayerData()!!.takePoints(amt)
                                        sender.sendMessage("§8[§6GeekItemBank§8] §a成功扣除 §f${text[2]} §8* §f$amt §a积分")
                                    }
                                    "set" -> player.player?.let {
                                        it.getPlayerData()!!.points = amt
                                        sender.sendMessage("§8[§6GeekItemBank§8] §a成功设置 §f${text[2]} §a的积分为 §f$amt")
                                    }
                                }
                            }
                        } else sender.sendMessage("§8[§6GeekItemBank§8] §c错误的参数....")
                    }
                }
            }
        }
    }



    @CommandBody
    val main = mainCommand {
        execute { sender, _, _ ->
            createHelp(sender)
        }
    }
    @CommandBody(permission = "GeekCraft.bank.command.admin")
    val reload = subCommand {
        execute<CommandSender> { _, _, _ ->
            GeekItemRank.onReload()
        }
    }

    @CommandBody(permission = "GeekCraft.bank.command.admin")
    val test = subCommand {
        execute<Player> { sender, _, _ ->
            val item = sender.inventory.itemInMainHand.clone()
            if (item.type != Material.AIR) {
                sender.inventory.setItemInMainHand(null)

                val node = item.matchNode()
                BankManage.getPlayerData(sender.uniqueId)?.let {
                    // 给予积分
                    if (node != null) {
                        it.points += node.give_Price * item.amount
                    } else {
                        it.points += Bank.getBankCache().DefaultGive_Price * item.amount
                    }
                    // 检查是否存在物品
                    it.ItemList.forEach { playerItemPack ->
                        if (playerItemPack.itemStack.itemMeta == item.itemMeta && playerItemPack.itemStack.type == item.type) {
                            playerItemPack.amount += item.amount
                            return@execute
                        }
                    }
                    val amt = item.amount
                    item.amount = 1
                    it.addItemPack(PlayerItemPack(UUID.randomUUID(), item, amt))
                    it.updateData()
                }
            }
        }
    }




    private fun createHelp(sender: CommandSender) {
        val s = adaptCommandSender(sender)
        s.sendMessage("")
        TellrawJson()
            .append("  ").append("§f§lGeekItemRank§8-§6Pro")
            .hoverText("§7现代化高级物资银行系统 By GeekCraft.ink")
            .append(" ").append("§f${GeekItemRank.VERSION}")
            .hoverText("""
                §7插件版本: §f${GeekItemRank.VERSION}
            """.trimIndent()).sendTo(s)
        s.sendMessage("")
        s.sendMessage("  §7指令: §f/gkitem §8[...]")
        if (sender.hasPermission("mail.command.admin")) {
         //   s.sendLang("CMD-HELP-ADMIN")
        }
      //  s.sendLang("CMD-HELP-PLAYER")
    }
}