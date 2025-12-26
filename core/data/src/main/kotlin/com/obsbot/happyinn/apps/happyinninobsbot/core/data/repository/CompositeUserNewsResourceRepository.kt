/*
 * 版权所�?2023 The Android Open Source Project
 *
 * 根据 Apache 许可�?2.0 版（"许可�?）授权；
 * 除非符合许可证要求，否则您不得使用此文件�?
 * 您可以在以下位置获得许可证副本：
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，根据许可证分发的软件
 * 是基�?按原�?的基础上分发的，不附带任何明示或暗示的担保条件�?
 * 请参阅许可证了解特定语言 governing permissions �?limitations�?
 */

package com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository

import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserNewsResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.mapToUserNewsResources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 通过组合 [newsRepository] �?[UserDataRepository] 来实�?[UserNewsResourceRepository] 接口
 */
//TODO 具体逻辑待深入学�?
class CompositeUserNewsResourceRepository @Inject constructor(
    val newsRepository: NewsRepository,
    val userDataRepository: UserDataRepository,
) : UserNewsResourceRepository {

    /**
     * 返回与给定查询条件匹配的可用新闻资源（与用户数据连接�?
     *
     * @param query 新闻资源查询条件
     * @return 包含用户新闻资源列表�?Flow
     */
    override fun observeAll(
        query: NewsResourceQuery,
    ): Flow<List<UserNewsResource>> =
        newsRepository.getNewsResources(query)
            .combine(userDataRepository.userData) { newsResources, userData ->
                newsResources.mapToUserNewsResources(userData)
            }

    /**
     * 返回用户关注主题的可用新闻资源（与用户数据连接）
     *
     * @return 包含用户关注主题相关新闻资源列表�?Flow
     */
    override fun observeAllForFollowedTopics(): Flow<List<UserNewsResource>> =
        userDataRepository.userData.map { it.followedTopics }.distinctUntilChanged()
            .flatMapLatest { followedTopics ->
                when {
                    // 如果没有关注的主题，返回空列�?
                    followedTopics.isEmpty() -> flowOf(emptyList())
                    // 否则根据关注的主题查询相关新闻资�?
                    else -> observeAll(NewsResourceQuery(filterTopicIds = followedTopics))
                }
            }

    /**
     * 返回用户收藏的新闻资源（与用户数据连接）
     *
     * @return 包含用户收藏新闻资源列表�?Flow
     */
    override fun observeAllBookmarked(): Flow<List<UserNewsResource>> =
        userDataRepository.userData.map { it.bookmarkedNewsResources }.distinctUntilChanged()
            .flatMapLatest { bookmarkedNewsResources ->
                when {
                    // 如果没有收藏的新闻资源，返回空列�?
                    bookmarkedNewsResources.isEmpty() -> flowOf(emptyList())
                    // 否则根据收藏的新闻ID查询相关资源
                    else -> observeAll(NewsResourceQuery(filterNewsIds = bookmarkedNewsResources))
                }
            }
}

