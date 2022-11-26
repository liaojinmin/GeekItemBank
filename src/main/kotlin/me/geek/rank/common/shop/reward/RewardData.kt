package me.geek.rank.common.shop.reward

import taboolib.common.util.randomDouble

/**
 * 作者: 老廖
 * 时间: 2022/11/17
 *
 **/
class RewardData(
    val node: String,
    private val prizesNode: MutableList<PrizesNode>
) {
    fun runReward(): PrizesNode {
        // 取总概率 start
        var ac = 0.0
        this.prizesNode.forEach {
            ac += it.chance
        }
        // 取总概率 end

        // 计算每个奖励概率区间 start
        val sort: MutableList<Double> = ArrayList<Double>(this.prizesNode.size).apply {
            var temp = 0.0
            this@RewardData.prizesNode.forEach {
                temp += it.chance
                add(temp / ac)
            }
        }
        // 计算每个奖励概率区间 end
        // 加入到概率区间中，排序后，返回的下标则是 prizes 中奖的下标
        val random = randomDouble()
        sort.add(random)
        sort.sort()
        return this.prizesNode[sort.indexOf(random)]
    }
}