package com.obsbot.happyinn.apps.happyinninobsbot.core.data.model


import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.NewsResourceEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.NewsResourceTopicCrossRef
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.TopicEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.NewsResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkNewsResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkTopic
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.asExternalModel

/**
 * 将网络新闻资源转换为数据库实体
 */
fun NetworkNewsResource.asEntity() = NewsResourceEntity(
    id = id,
    title = title,
    content = content,
    url = url,
    headerImageUrl = headerImageUrl,
    publishDate = publishDate,
    type = type,
)

/**
 * 为满足插入[NewsResourceEntity]到数据库时的外键约束而创建的空壳[TopicEntity]
 */
fun NetworkNewsResource.topicEntityShells() =
    topics.map { topicId ->
        TopicEntity(
            id = topicId,
            name = "",
            url = "",
            imageUrl = "",
            shortDescription = "",
            longDescription = "",
        )
    }

/**
 * 创建新闻资源和主题之间的交叉引用关系
 */
fun NetworkNewsResource.topicCrossReferences(): List<NewsResourceTopicCrossRef> =
    topics.map { topicId ->
        NewsResourceTopicCrossRef(
            newsResourceId = id,
            topicId = topicId,
        )
    }

/**
 * 将网络新闻资源转换为外部模型
 *
 * @param topics 网络主题列表
 * @return 新闻资源外部模型
 */
fun NetworkNewsResource.asExternalModel(topics: List<NetworkTopic>) =
    NewsResource(
        id = id,
        title = title,
        content = content,
        url = url,
        headerImageUrl = headerImageUrl,
        publishDate = publishDate,
        type = type,
        topics = topics
            .filter { networkTopic -> this.topics.contains(networkTopic.id) }
            .map(NetworkTopic::asExternalModel),
    )
