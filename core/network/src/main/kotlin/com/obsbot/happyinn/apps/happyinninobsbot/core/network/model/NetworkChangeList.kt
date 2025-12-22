package com.obsbot.happyinn.apps.happyinninobsbot.core.network.model

import kotlinx.serialization.Serializable
import kotlin.OptIn
import kotlinx.serialization.InternalSerializationApi

/**
 * 网络层模型变更列表的数据表示
 *
 * 变更列表是一种服务器端类似Map的数据结构表示形式，用于存储模型ID与其元数据之间的映射关系。
 * 在单个变更列表中，每个模型ID只能出现一次。
 */
@OptIn(InternalSerializationApi::class)
@Serializable
data class NetworkChangeList(
    /**
     * 发生变更的模型的唯一标识符
     */
    val id: String,

    /**
     * 集合中的唯一连续单调递增版本号，用于描述集合中模型间变更的相对时间点
     */
    val changeListVersion: Int,

    /**
     * 概述对模型的更新操作类型：删除或更新（包括创建）
     */
    val isDelete: Boolean,
)
