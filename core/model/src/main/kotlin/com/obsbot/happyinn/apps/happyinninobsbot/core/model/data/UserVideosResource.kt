/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data

import kotlinx.datetime.Instant
import kotlin.collections.map

/**
 * 用户视频资源数据类
 *
 * 包含视频资源的基本信息以及用户的个性化信息，如用户是否关注了该视频资源的话题，
 * 以及用户是否收藏（书签）了这个视频资源
 */
data class UserVideosResource internal constructor(
    /**
     * 视频资源的唯一标识符
     */
    val id: String,

    /**
     * 视频标题
     */
    val title: String,

    /**
     * 视频内容描述
     */
    val content: String,

    /**
     * 视频的URL链接
     */
    val url: String,

    /**
     * 视频头部图片URL，可能为空
     */
    val headerImageUrl: String?,

    /**
     * 视频发布日期时间
     */
    val publishDate: Instant,

    /**
     * 视频类型标识
     */
    val type: String,

    /**
     * 视频关联的可关注话题列表
     * 每个话题都包含是否被用户关注的状态信息
     */
    val followableTopics: List<FollowableTopic>,

    /**
     * 标识用户是否收藏了该视频资源
     * true表示已收藏，false表示未收藏
     */
    val isSaved: Boolean,

    /**
     * 标识用户是否已经观看过该视频
     * true表示已观看，false表示未观看
     */
    val hasBeenViewed: Boolean,
) {
    /**
     * 构造函数：根据视频资源和用户数据创建用户视频资源对象
     *
     * @param VideosResource 基础视频资源对象
     * @param userData 用户数据，包含用户的关注话题、收藏视频等信息
     */
    constructor(VideosResource: VideosResource, userData: UserData) : this(
        id = VideosResource.id,
        title = VideosResource.title,
        content = VideosResource.content,
        url = VideosResource.url,
        headerImageUrl = VideosResource.headerImageUrl,
        publishDate = VideosResource.publishDate,
        type = VideosResource.type,
        followableTopics = VideosResource.topics.map { topic ->
            // 将每个话题转换为可关注话题，并检查用户是否已关注该话题
            FollowableTopic(
                topic = topic,
                isFollowed = topic.id in userData.followedTopics,
            )
        },
        // 检查该视频是否在用户的收藏列表中
        isSaved = VideosResource.id in userData.bookmarkedVideosResources,
        // 检查该视频是否在用户已观看的视频列表中
        hasBeenViewed = VideosResource.id in userData.viewedVideosResources,
    )
}

/**
 * 扩展函数：将视频资源列表映射为用户视频资源列表
 *
 * @param userData 用户数据，用于确定用户的个性化状态
 * @return 用户视频资源列表，包含每个视频的用户个性化信息
 */
fun List<VideosResource>.mapToUserVideosResources(userData: UserData): List<UserVideosResource> =
    map { UserVideosResource(it, userData) }
