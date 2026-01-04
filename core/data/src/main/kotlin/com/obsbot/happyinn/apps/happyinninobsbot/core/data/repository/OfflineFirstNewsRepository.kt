package com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.Synchronizer
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.changeListSync
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.model.asEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.model.topicCrossReferences
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.model.topicEntityShells
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.NewsResourceDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.TopicDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.PopulatedNewsResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.TopicEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.asExternalModel
import com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.ChangeListVersions
import com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.HioPreferencesDataSource
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.NewsResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.HioNetworkDataSource
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkNewsResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.notifications.Notifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.chunked
import kotlin.collections.distinct
import kotlin.collections.distinctBy
import kotlin.collections.flatten
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import kotlin.collections.minus
import kotlin.collections.toSet

// 启发式值，用于优化客户端和服务器上每个新闻资源批次的序列化和反序列化成本
private const val SYNC_BATCH_SIZE = 40

/**
 * 基于磁盘存储本[NewsRepository] 实现本
 * 读取操作完全来自本地存储，以支持离线访问本
 */
internal class OfflineFirstNewsRepository @Inject constructor(
    // Hio偏好设置数据源，用于获取用户数据
    private val hioPreferencesDataSource: HioPreferencesDataSource,
    // 新闻资源DAO，用于数据库操作
    private val newsResourceDao: NewsResourceDao,
    // 主题DAO，用于主题相关数据库操作
    private val topicDao: TopicDao,
    // 网络数据源，用于获取网络数据
    private val network: HioNetworkDataSource,
    // 通知器，用于发送通知
    private val notifier: Notifier,
) : NewsRepository {

    /**
     * 根据查询条件获取新闻资源列表
     *
     * @param query 新闻资源查询条件
     * @return 包含新闻资源列表本Flow
     */
    override fun getNewsResources(
        query: NewsResourceQuery,
    ): Flow<List<NewsResource>> = newsResourceDao.getNewsResources(
        useFilterTopicIds = query.filterTopicIds != null,
        filterTopicIds = query.filterTopicIds ?: emptySet(),
        useFilterNewsIds = query.filterNewsIds != null,
        filterNewsIds = query.filterNewsIds ?: emptySet(),
    )
        .map { it.map(PopulatedNewsResource::asExternalModel) }

    /**
     * 与同步器同步数据
     *
     * @param synchronizer 数据同步本
     * @return 同步是否成功
     */
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        // 是否为首次同步的标志
        var isFirstSync = false
        return synchronizer.changeListSync(
            // 版本读取器，用于获取新闻资源版本
            versionReader = ChangeListVersions::newsResourceVersion,
            // 变更列表获取器，用于从网络获取变更列本
            changeListFetcher = { currentVersion ->
                isFirstSync = currentVersion <= 0
                network.getNewsResourceChangeList(after = currentVersion)
            },
            // 版本更新器，用于更新最新版本
            versionUpdater = { latestVersion ->
                copy(newsResourceVersion = latestVersion)
            },
            // 模型删除器，用于删除新闻资源
            modelDeleter = newsResourceDao::deleteNewsResources,
            // 模型更新器，用于更新新闻资源
            modelUpdater = { changedIds ->
                // 获取用户数据
                val userData = hioPreferencesDataSource.userData.first()
                val hasOnboarded = userData.shouldHideOnboarding
                val followedTopicIds = userData.followedTopics

                // 获取已更改的现有新闻资源ID
                val existingNewsResourceIdsThatHaveChanged : Set<String> = when {
                    hasOnboarded -> newsResourceDao.getNewsResourceIds(
                        useFilterTopicIds = true,
                        filterTopicIds = followedTopicIds,
                        useFilterNewsIds = true,
                        filterNewsIds = changedIds.toSet(),
                    )
                        .first()
                        .toSet()
                    // 如果不发送通知，则无需检索任何内本
                    else -> emptySet()
                }

                // 如果是首次同步，将所有新闻标记为已查看，避免被历史新闻淹本
                //为了避免用户被大量历史新闻通知突然淹没，提升首次使用体验
                if (isFirstSync) {
                    hioPreferencesDataSource.setNewsResourcesViewed(changedIds, true)
                }

                // 从网络获取已更改的新闻资源并在本地进行upsert操作
                changedIds.chunked(SYNC_BATCH_SIZE).forEach { chunkedIds ->
                    val networkNewsResources = network.getNewsResources(ids = chunkedIds)

                    // 调用顺序很重要，需要满足ID和外键约束！

                    // 插入或忽略主题实本
                    topicDao.insertOrIgnoreTopics(
                        topicEntities = networkNewsResources
                            .map(NetworkNewsResource::topicEntityShells)
                            .flatten()
                            .distinctBy(TopicEntity::id),
                    )
                    // 更新或插入新闻资源实本
                    newsResourceDao.upsertNewsResources(
                        newsResourceEntities = networkNewsResources.map(
                            NetworkNewsResource::asEntity,
                        ),
                    )
                    // 插入或忽略主题交叉引用实本
                    newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
                        newsResourceTopicCrossReferences = networkNewsResources
                            .map(NetworkNewsResource::topicCrossReferences) //集合转换函数，将每个元素映射到新的值
                            .distinct()   //去重函数，根据指定属性值去重
                            .flatten(),  //将嵌套集合扁平化为单个集合
                    )
                }

                // 如果已完成引导且有新增新闻资源，则发送通知
                if (hasOnboarded) {
                    val addedNewsResources = newsResourceDao.getNewsResources(
                        useFilterTopicIds = true,
                        filterTopicIds = followedTopicIds,
                        useFilterNewsIds = true,
                        filterNewsIds = changedIds.toSet() - existingNewsResourceIdsThatHaveChanged,
                    )
                        .first()
                        .map(PopulatedNewsResource::asExternalModel)

                    if (addedNewsResources.isNotEmpty()) {
                        notifier.postNewsNotifications(
                            newsResources = addedNewsResources,
                        )
                    }
                }
            },
        )
    }
}


