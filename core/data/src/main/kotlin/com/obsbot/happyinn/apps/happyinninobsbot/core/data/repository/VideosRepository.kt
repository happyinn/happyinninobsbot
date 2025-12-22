package com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository

import com.obsbot.happyinn.apps.happyinninobsbot.core.data.Syncable
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.VideosResource
import kotlinx.coroutines.flow.Flow

/**
 * [VideosResource] 查询参数的封装类
 */
data class VideosResourceQuery(
    /**
     * 要筛选的主题ID集合。Null表示匹配任何主题ID。
     */
    val filterTopicIds: Set<String>? = null,
    /**
     * 要筛选的新闻ID集合。Null表示匹配任何新闻ID。
     */
    val filterVideosIds: Set<String>? = null,
)

/**
 * [VideosResource] 的数据层实现接口
 */
interface VideosRepository : Syncable {
    /**
     * 返回与指定 [query] 参数匹配的可用新闻资源。
     */
    fun getVideosResources(
        query: VideosResourceQuery = VideosResourceQuery(
            filterTopicIds = null,
            filterVideosIds = null,
        ),
    ): Flow<List<VideosResource>>
}
