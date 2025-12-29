package com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository

import com.google.samples.apps.nowinandroid.core.data.model.asEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.Synchronizer
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.changeListSync
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.TopicDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.TopicEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.asExternalModel
import com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.ChangeListVersions
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.Topic
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.HioNetworkDataSource
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkChangeList
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

/**
 * 基于磁盘存储的TopicsRepository实现类。
 * 遵循离线优先架构设计，所有读取操作都直接从本地存储获取数据，以支持离线访问。
 * 同时提供与远程服务器的数据同步机制，确保数据最新性。
 */
internal class OfflineFirstTopicsRepository @Inject constructor(
    /**
     * 话题数据访问对象，负责与本地数据库进行交互
     * 提供话题数据的CRUD操作方法
     */
    private val topicDao: TopicDao,
    /**
     * 网络数据源，负责从远程服务器获取话题数据
     * 提供话题数据的网络请求方法
     */
    private val network: HioNetworkDataSource,
) : TopicsRepository {

    /**
     * 获取所有话题列表
     * 实现从本地数据库读取话题数据并转换为外部模型
     * 
     * @return 返回一个Flow流，包含所有话题的列表
     *         使用Flow可以实现数据的响应式更新，当数据库中的数据发生变化时，UI能自动更新
     */
    override fun getTopics(): Flow<List<Topic>> {
        // 从数据库获取话题实体列表
        val topicEntities = topicDao.getTopicEntities()
        // 将数据库实体映射转换为外部模型（领域模型）
        // 第一层map用于监听Flow数据流变化，第二层map用于转换每个实体对象
        return topicEntities
            .map { it.map(TopicEntity::asExternalModel) }
    }

    /**
     * 根据ID获取单个话题
     * 实现从本地数据库读取特定ID的话题数据
     * 
     * @param id 话题的唯一标识符
     * @return 返回一个Flow流，包含指定ID的话题
     *         如果找不到对应ID的话题，可能返回null或抛出异常
     */
    override fun getTopic(id: String): Flow<Topic> = 
        // 直接调用DAO方法获取特定ID的话题实体并转换为外部模型
        topicDao.getTopicEntity(id).map { it.asExternalModel() }

    /**
     * 与远程服务器同步话题数据
     * 实现基于变更列表的增量同步机制，只同步发生变化的数据
     * 
     * @param synchronizer 同步器实例，负责提供版本管理和同步协调功能
     * @return 返回布尔值，表示同步操作是否成功完成
     *         true表示同步成功，false表示同步失败
     */
/*    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        // 调用同步器的changeListSync方法执行增量同步
        synchronizer.changeListSync(
            // 版本读取器：从ChangeListVersions对象中提取topicVersion
            versionReader = ChangeListVersions::topicVersion,
            
            // 变更列表获取器：根据当前版本号获取自该版本以来的变更列表
            changeListFetcher = { currentVersion ->
                network.getTopicChangeList(after = currentVersion)
            },
            
            // 版本更新器：更新ChangeListVersions中的topicVersion为最新版本
            versionUpdater = { latestVersion ->
                copy(topicVersion = latestVersion)
            },
            
            // 模型删除器：删除本地数据库中已在服务器端删除的话题
            modelDeleter = topicDao::deleteTopics,
            
            // 模型更新器：获取并更新本地数据库中发生变化的话题
            modelUpdater = { changedIds ->
                // 根据变更ID列表从网络获取最新的话题数据
                val networkTopics = network.getTopics(ids = changedIds)
                // 将网络模型转换为数据库实体并批量插入或更新
                topicDao.upsertTopics(
                    entities = networkTopics.map(NetworkTopic::asEntity),
                )
            },
        )*/


    /**
     * 同步话题数据的方法
     *
     * @param synchronizer 同步器实例，用于执行实际的同步操作
     * @return 同步是否成功
     */
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        // 定义用于读取当前版本的函数
        val versionReaderFunction: (ChangeListVersions) -> Int = {
            // 从ChangeListVersions对象中提取topicVersion属性值
                changeListVersions -> changeListVersions.topicVersion
        }

        // 定义用于获取变更列表的函数
        val fetchChangeListFunction: suspend (Int) -> List<NetworkChangeList> = {
            // 根据当前版本号获取变更列表
                currentVersion -> network.getTopicChangeList(after = currentVersion)
        }

        // 定义用于更新版本号的函数
        val updateVersionFunction: ChangeListVersions.(Int) -> ChangeListVersions = {
            // 创建一个新的ChangeListVersions对象，更新topicVersion为最新版本
                latestVersion -> this.copy(topicVersion = latestVersion)
        }

        // 定义用于删除模型的函数（直接使用dao方法引用）
        val deleteModelsFunction: suspend (List<String>) -> Unit = topicDao::deleteTopics

        // 定义用于更新模型的函数
        val updateModelsFunction: suspend (List<String>) -> Unit = {
            // 处理变更的话题ID列表
                changedIds ->
            // 步骤1: 根据变更ID列表从网络获取最新的话题数据
            val networkTopics = network.getTopics(ids = changedIds)

            // 步骤2: 将网络模型转换为数据库实体
            val topicEntities = networkTopics.map { networkTopic ->
                networkTopic.asEntity()
            }

            // 步骤3: 批量插入或更新数据库
            topicDao.upsertTopics(entities = topicEntities)
        }

        // 调用synchronizer的changeListSync方法执行完整的同步流程
        // 将定义好的各个函数作为参数传入
        return synchronizer.changeListSync(
            versionReader = versionReaderFunction,
            changeListFetcher = fetchChangeListFunction,
            versionUpdater = updateVersionFunction,
            modelDeleter = deleteModelsFunction,
            modelUpdater = updateModelsFunction
        )
    }
}