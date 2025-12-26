package com.obsbot.happyinn.apps.happyinninobsbot.core.network

import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkChangeList
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkNewsResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkTopic


/**
 * 表示与NIA后端网络调用的接口
 */
interface HioNetworkDataSource {
    /**
     * 获取主题列表
     *
     * @param ids 可选的主题ID列表，用于过滤结果
     * @return 网络主题列表
     */
    suspend fun getTopics(ids: List<String>? = null): List<NetworkTopic>

    /**
     * 获取新闻资源列表
     *
     * @param ids 可选的新闻资源ID列表，用于过滤结果
     * @return 网络新闻资源列表
     */
    suspend fun getNewsResources(ids: List<String>? = null): List<NetworkNewsResource>

    /**
     * 获取主题变更列表
     *
     * @param after 可选的时间戳，用于获取在此之后的变更
     * @return 网络变更列表
     */
    suspend fun getTopicChangeList(after: Int? = null): List<NetworkChangeList>

    /**
     * 获取新闻资源变更列表
     *
     * @param after 可选的时间戳，用于获取在此之后的变更
     * @return 网络变更列表
     */
    suspend fun getNewsResourceChangeList(after: Int? = null): List<NetworkChangeList>
}
