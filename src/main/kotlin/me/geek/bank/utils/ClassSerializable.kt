package me.geek.bank.utils

import com.google.gson.*
import com.google.gson.annotations.Expose
import me.geek.bank.common.data.PlayerData
import me.geek.bank.common.data.PointsData
import me.geek.bank.common.bank.PlayerItemPack
import java.lang.reflect.Type
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/11/14
 *
 **/
object ClassSerializable {
    fun gsonSerialize(data: PlayerData): ByteArray {
        return toJson(data).toByteArray(charset = Charsets.UTF_8)
    }
    private fun toJson(data: PlayerData): String {
        return GsonBuilder()
            .setExclusionStrategies(A())
            .create().toJson(data)
    }
    fun gsonUnSerialize(objs: ByteArray): Any {
        val gson = GsonBuilder().setExclusionStrategies(A())
        gson.registerTypeAdapter(PlayerData::class.java, UnSerializePlayerData())
        return gson.create().fromJson(String(objs, charset = Charsets.UTF_8), PlayerData::class.java)
    }

    class A : ExclusionStrategy {
        override fun shouldSkipField(f: FieldAttributes): Boolean {
            return f.getAnnotation(Expose::class.java) != null
        }

        override fun shouldSkipClass(clazz: Class<*>): Boolean {
            return clazz.getAnnotation(Expose::class.java) != null
        }
    }

    class UnSerializePlayerData: JsonDeserializer<PlayerData> {
        override fun deserialize(json: JsonElement, p1: Type, p2: JsonDeserializationContext?): PlayerData {
            val jsonObject = json.asJsonObject
            val name = jsonObject.get("playerName").asString
            val uuid = UUID.fromString(jsonObject.get("playerUid").asString)
            val points = jsonObject.get("points").asDouble
            val day = jsonObject.get("Day").asInt

            val item = mutableListOf<PlayerItemPack>().apply {
                val item = jsonObject.get("ItemList").asJsonArray
                if (item.size() != 0) {
                    item.forEach {
                        val a = it.asJsonObject
                        add(PlayerItemPack(UUID.fromString(a.get("itemUid").asString),
                            a.get("itemStackString").asString.deserializeItemStack()!!,
                            a.get("amount").asInt))
                    }
                }
            }
            val shop = mutableListOf<PointsData>().apply {
                val shop = jsonObject.get("pointsData").asJsonArray
                if (shop.size() != 0) {
                    shop.forEach {
                        val a = it.asJsonObject
                        add(PointsData(a.get("ShopId").asString,
                            a.get("Buy_amt").asInt,
                            a.get("resTime").asLong))
                    }
                }
            }
            return PlayerData(name, uuid, points, day, item, shop)
        }

    }

}