package me.geek.bank.common.kether

import me.geek.bank.GeekItemRank
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.mirrorNow
import taboolib.library.kether.LocalizedException
import taboolib.module.kether.KetherShell
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * 作者: 老廖
 * 时间: 2022/8/8
 * 复用 Arasple 代码
 **/
object KetherAPI {

    @JvmStatic
    fun eval(player: Player, script: String): CompletableFuture<Any?> {
        return mirrorNow("GeekItemRank:Script") {
            return@mirrorNow try {
                KetherShell.eval(script, namespace = listOf("GeekItemRank")) {
                    sender = adaptPlayer(player)
                }
            } catch (e: LocalizedException) {
                GeekItemRank.say("§8解析 kether 出错 LocalizedException")
                CompletableFuture.completedFuture(false)
            }
        }
    }

    /**
     * 执行 Kether 脚本
     *
     * @param player 玩家 (sender)
     * @param script kether 脚本
     * @param timeout 超时时间 (ms)
     * @return 执行结果
     */
    @JvmStatic
    fun instantKether(player: Player, script: String, timeout: Long = 100): EvalResult {
        return try {
            EvalResult(eval(player, script).get(timeout, TimeUnit.MILLISECONDS))
        } catch (e: TimeoutException) {
            GeekItemRank.say("§8Timeout while parsing kether shell:")
            e.localizedMessage?.split("\n")?.forEach { GeekItemRank.say("         §8$it") }
            EvalResult.FALSE
        }
    }

    fun toBoolean(obj: Any?): Boolean {
        return if (obj == null) {
            false
        } else {
            if (obj is Boolean) obj else obj.toString().trim { it <= ' ' }.matches("^(1|true|yes)$".toRegex())
        }
    }
}