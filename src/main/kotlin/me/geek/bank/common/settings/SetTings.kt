package me.geek.bank.common.settings

import me.geek.bank.GeekItemRank
import me.geek.bank.scheduler.sql.SqlConfig
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.configuration.Configuration.Companion.getObject

/**
 * 作者: 老廖
 * 时间: 2022/11/8
 *
 **/
object SetTings {

    var debug: Boolean = false
        private set
    var ConfigVersion: Double = 1.0
        private set
    var update: Int = -1
        private set

    lateinit var sqlConfig: SqlConfig

    @Config(value = "settings.yml", autoReload = true)
    lateinit var config: ConfigFile
        private set

    @Awake(LifeCycle.ACTIVE)
    fun init() {
        config.onReload { onLoadSetTings() }
    }
    @Synchronized
    fun onLoadSetTings() {
        debug = config.getBoolean("debug", false)
        ConfigVersion = config.getDouble("ConfigVersion")
        update = config.getInt("update.time")
        sqlConfig = config.getObject<SqlConfig>("data_storage", false).apply {
            sqlite = GeekItemRank.instance.dataFolder
        }


    }

}