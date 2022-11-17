package me.geek.rank.utils

import java.io.File

/**
 * 作者: 老廖
 * 时间: 2022/11/7
 *
 **/
object FileUtils {
    fun forFile(file: File): List<File> {
        return mutableListOf<File>().run {
            if (file.isDirectory) {
                file.listFiles()?.forEach {
                    addAll(forFile(it))
                }
            } else if (file.exists() && file.absolutePath.endsWith(".yml")) {
                add(file)
            }
            this
        }
    }
}