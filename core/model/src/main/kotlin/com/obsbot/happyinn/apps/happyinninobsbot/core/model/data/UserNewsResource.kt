package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data

import kotlinx.datetime.Instant
import kotlin.collections.map

/**
 * 用户新闻资源数据�?
 *
 * 包含新闻资源的基本信息以及用户的个性化信息，如用户是否关注了该新闻资源的话题，
 * 以及用户是否收藏（书签）了这个新闻资源
 */
data class UserNewsResource internal constructor(
    /**
     * 新闻资源的唯一标识�?
     */
    val id: String,

    /**
     * 新闻标题
     */
    val title: String,

    /**
     * 新闻内容描述
     */
    val content: String,

    /**
     * 新闻的URL链接
     */
    val url: String,

    /**
     * 新闻头部图片URL，可能为�?
     */
    val headerImageUrl: String?,

    /**
     * 新闻发布日期时间
     */
    val publishDate: Instant,

    /**
     * 新闻类型标识
     */
    val type: String,

    /**
     * 新闻关联的可关注话题列表
     * 每个话题都包含是否被用户关注的状态信�?
     */
    val followableTopics: List<FollowableTopic>,

    /**
     * 标识用户是否收藏了该新闻资源
     * true表示已收藏，false表示未收�?
     */
    val isSaved: Boolean,

    /**
     * 标识用户是否已经观看过该新闻
     * true表示已观看，false表示未观�?
     */
    val hasBeenViewed: Boolean,
) {
    /**
     * 构造函数：根据新闻资源和用户数据创建用户新闻资源对�?
     *
     * @param newsResource 基础新闻资源对象
     * @param userData 用户数据，包含用户的关注话题、收藏新闻等信息
     */
    constructor(newsResource: NewsResource, userData: UserData) : this(
        id = newsResource.id,
        title = newsResource.title,
        content = newsResource.content,
        url = newsResource.url,
        headerImageUrl = newsResource.headerImageUrl,
        publishDate = newsResource.publishDate,
        type = newsResource.type,
        followableTopics = newsResource.topics.map { topic ->
            // 将每个话题转换为可关注话题，并检查用户是否已关注该话�?
            FollowableTopic(
                topic = topic,
                isFollowed = topic.id in userData.followedTopics,
            )
        },
        // 检查该新闻是否在用户的收藏列表�?
        isSaved = newsResource.id in userData.bookmarkedNewsResources,
        // 检查该新闻是否在用户已观看的新闻列表中
        hasBeenViewed = newsResource.id in userData.viewedNewsResources,
    )
}

/**
 * 扩展函数：将新闻资源列表映射为用户新闻资源列�?
 *
 * @param userData 用户数据，用于确定用户的个性化状�?
 * @return 用户新闻资源列表，包含每个新闻的用户个性化信息
 */
fun List<NewsResource>.mapToUserNewsResources(userData: UserData): List<UserNewsResource> =
    map { UserNewsResource(it, userData) }

