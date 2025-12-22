package com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository

import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserVideosResource
import kotlinx.coroutines.flow.Flow

/**
 * [UserVideosResource] 的数据层实现接口
 */
interface UserVideosResourceRepository {
    /**
     * 返回可用的新闻资源流
     *
     * @param query 新闻资源查询条件，默认查询所有资源
     * @return 包含用户新闻资源列表的 Flow
     */
    fun observeAll(
        query: VideosResourceQuery = VideosResourceQuery(
            filterTopicIds = null,
            filterVideosIds = null,
        ),
    ): Flow<List<UserVideosResource>>

    /**
     * 返回用户关注主题的新闻资源流
     *
     * @return 包含用户关注主题相关新闻资源列表的 Flow
     */
    fun observeAllForFollowedTopics(): Flow<List<UserVideosResource>>

    /**
     * 返回用户收藏的新闻资源流
     *
     * @return 包含用户收藏新闻资源列表的 Flow
     */
    fun observeAllBookmarked(): Flow<List<UserVideosResource>>
}
