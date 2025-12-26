/*
 * 版权所有 2023 The Android Open Source Project
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

package com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository

import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.NewsResourceDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.NewsResourceFtsDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.TopicDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.TopicFtsDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.PopulatedNewsResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.asExternalModel
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.asFtsEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.SearchResult
import com.obsbot.happyinn.apps.happyinninobsbot.network.Dispatcher
import com.obsbot.happyinn.apps.happyinninobsbot.network.HioDispatchers.IO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 搜索内容仓库的默认实现
 */
internal class DefaultSearchContentsRepository @Inject constructor(
    // 新闻资源DAO，用于访问新闻资源数据
    private val newsResourceDao: NewsResourceDao,
    // 新闻资源全文搜索DAO，用于新闻资源的全文搜索
    private val newsResourceFtsDao: NewsResourceFtsDao,
    // 主题DAO，用于访问主题数据
    private val topicDao: TopicDao,
    // 主题全文搜索DAO，用于主题的全文搜索
    private val topicFtsDao: TopicFtsDao,
    // IO调度器，用于执行IO密集型任务
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : SearchContentsRepository {

    /**
     * 填充全文搜索数据
     * 将新闻资源和主题数据插入到对应的全文搜索表中
     */
    override suspend fun populateFtsData() {
        withContext(ioDispatcher) {
            // 插入所有新闻资源到新闻资源全文搜索表
            newsResourceFtsDao.insertAll(
                newsResourceDao.getNewsResources(
                    useFilterTopicIds = false,
                    useFilterNewsIds = false,
                )
                    .first()
                    .map(PopulatedNewsResource::asFtsEntity),
            )
            // 插入所有主题到主题全文搜索表
            topicFtsDao.insertAll(topicDao.getOneOffTopicEntities().map { it.asFtsEntity() })
        }
    }

    /**
     * 根据搜索查询获取搜索结果
     *
     * @param searchQuery 搜索查询字符串
     * @return 包含搜索结果的Flow
     */
    override fun searchContents(searchQuery: String): Flow<SearchResult> {
        // 用星号包围查询词，以便匹配单词中间的查询
        val newsResourceIds = newsResourceFtsDao.searchAllNewsResources("*$searchQuery*")
        val topicIds = topicFtsDao.searchAllTopics("*$searchQuery*")

        // 新闻资源流处理
        val newsResourcesFlow = newsResourceIds
            .mapLatest { it.toSet() }  // 将ID列表转换为集合
            .distinctUntilChanged()    // 只有当数据真正变化时才继续
            .flatMapLatest {           // 根据ID获取完整的新闻资源数据
                newsResourceDao.getNewsResources(useFilterNewsIds = true, filterNewsIds = it)
            }

        // 主题流处理
        val topicsFlow = topicIds
            .mapLatest { it.toSet() }  // 将ID列表转换为集合
            .distinctUntilChanged()    // 只有当数据真正变化时才继续
            .flatMapLatest(topicDao::getTopicEntities)  // 根据ID获取完整的主题数据

        // 合并新闻资源和主题流，构建搜索结果
        return combine(newsResourcesFlow, topicsFlow) { newsResources, topics ->
            SearchResult(
                topics = topics.map { it.asExternalModel() },
                newsResources = newsResources.map { it.asExternalModel() },
            )
        }
    }

    /**
     * 获取搜索内容总数
     *
     * @return 包含新闻资源和主题总数的Flow
     */
    override fun getSearchContentsCount(): Flow<Int> =
        combine(
            newsResourceFtsDao.getCount(),  // 获取新闻资源搜索条目数
            topicFtsDao.getCount(),         // 获取主题搜索条目数
        ) { newsResourceCount, topicsCount ->
            newsResourceCount + topicsCount  // 返回总和
        }
}
