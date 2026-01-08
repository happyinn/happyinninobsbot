package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data

/**
 * 可关注的主题数据类
 *
 * 包含主题实体以及该主题是否被关注的状态信息
 *
 * @property topic 主题实体，可被关注或取消关注
 * @property isFollowed 标识该主题当前是否被关注的状态：
 *                      true 表示已关注，false 表示未关注
 */
// TODO 考虑将其更改为 UserTopic 并进行扁平化处理
data class FollowableTopic(
    val topic: Topic,
    val isFollowed: Boolean,
)
