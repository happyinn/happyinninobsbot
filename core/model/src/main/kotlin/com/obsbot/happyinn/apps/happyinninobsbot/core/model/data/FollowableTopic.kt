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
