package com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository

import com.obsbot.happyinn.apps.happyinninobsbot.core.data.Syncable
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.NewsResource
import kotlinx.coroutines.flow.Flow

/**
 * [NewsResource] 查询参数的封装类
 */
data class NewsResourceQuery(
    /**
     * 要筛选的主题ID集合。Null表示匹配任何主题ID�?
     */
    val filterTopicIds: Set<String>? = null,
    /**
     * 要筛选的新闻ID集合。Null表示匹配任何新闻ID�?
     */
    val filterNewsIds: Set<String>? = null,
)

/**
 * [NewsResource] 的数据层实现接口
 */
interface NewsRepository : Syncable {
    /**
     * 返回与指�?[query] 参数匹配的可用新闻资源�?
     */
    fun getNewsResources(
        query: NewsResourceQuery = NewsResourceQuery(
            filterTopicIds = null,
            filterNewsIds = null,
        ),
    ): Flow<List<NewsResource>>
}

