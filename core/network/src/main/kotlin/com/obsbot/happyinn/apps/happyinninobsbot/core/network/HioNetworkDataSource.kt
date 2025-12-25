/*
 * 版权所有 2022 The Android Open Source Project
 *
 * 根据 Apache 许可证 2.0 版（"许可证"）授权；
 * 除非符合许可证要求，否则您不得使用此文件。
 * 您可以在以下位置获得许可证副本：
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，根据许可证分发的软件
 * 是基于"按原样"的基础上分发的，不附带任何明示或暗示的担保条件。
 * 请参阅许可证了解特定语言 governing permissions 和 limitations。
 */

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
