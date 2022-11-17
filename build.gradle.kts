
val taboolibVersion: String by project

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("io.izzel.taboolib") version "1.50"
}

taboolib {
    install(
        "common",
        "common-5",
        "platform-bukkit",
        "module-configuration",
        "module-kether",
        "module-chat",
        "module-lang",
        "module-nms",
        "module-nms-util",
    )
    description {
        contributors {
            name("HSDLao_liao")
        }
        dependencies {
            bukkitApi("1.13")
            name("Vault").optional(true)
            name("PlaceholderAPI").optional(true)
            name("ItemsAdder").optional(true)
            name("MythicMobs").optional(true)
        }
    }

    relocate("me.geek.rank", group.toString())
    relocate("com.zaxxer.hikari", "com.zaxxer.hikari_4_0_3_rank")
    classifier = null
    version = taboolibVersion
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.tabooproject.org/repository/releases")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://jitpack.io")
    maven("https://maven.pkg.github.com/LoneDev6/API-ItemsAdder")
}


dependencies {
    taboo("ink.ptms:um:1.0.0-beta-20")
    compileOnly(kotlin("stdlib"))
    // Server Core
    compileOnly("com.zaxxer:HikariCP:4.0.3")
    compileOnly("ink.ptms.core:v11701:11701-minimize:mapped")
    compileOnly("ink.ptms.core:v11701:11701-minimize:universal")
    compileOnly("ink.ptms.core:v11604:11604")

    // Hook Plugins
    compileOnly("me.clip:placeholderapi:2.10.9") { isTransitive = false }
    compileOnly("com.github.MilkBowl:VaultAPI:-SNAPSHOT") { isTransitive = false }
    compileOnly("com.github.LoneDev6:api-itemsadder:3.2.3c")

    // Libraries
    compileOnly(fileTree("lib"))
}

