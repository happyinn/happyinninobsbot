/*
 * Copyright 2022 The Android Open Source Project
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

/**
 * 视频资源数据�?
 *
 * 外部数据层表示的一个完整的NiA新闻资源（视频形式）
 * 包含视频的基本信息、内容和关联话题等数�?
 */
data class NewsResource(
    /**
     * 视频资源的唯一标识�?
     */
    val id: String,

    /**
     * 视频标题
     */
    val title: String,

    /**
     * 视频内容描述或正文内�?
     */
    val content: String,

    /**
     * 视频的访问URL地址
     */
    val url: String,

    /**
     * 视频头部图片的URL地址，可能为�?
     */
    val headerImageUrl: String?,

    /**
     * 视频发布时间
     */
    val publishDate: Instant,

    /**
     * 视频资源类型标识
     */
    val type: String,

    /**
     * 与该视频相关联的话题列表
     */
    val topics: List<Topic>,
)

