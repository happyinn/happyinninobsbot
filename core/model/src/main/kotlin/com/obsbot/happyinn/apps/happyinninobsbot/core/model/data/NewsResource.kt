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
 * 新闻资源数据类
 *
 * 外部数据层表示的一个完整的NiA新闻资源（新闻形式）
 * 包含新闻的基本信息、内容和关联话题等数据
 */
data class NewsResource(
    /**
     * 新闻资源的唯一标识�?
     */
    val id: String,

    /**
     * 新闻标题
     */
    val title: String,

    /**
     * 新闻内容描述或正文内�?
     */
    val content: String,

    /**
     * 新闻的访问URL地址
     */
    val url: String,

    /**
     * 新闻头部图片的URL地址，可能为�?
     */
    val headerImageUrl: String?,

    /**
     * 新闻发布时间
     */
    val publishDate: Instant,

    /**
     * 新闻资源类型标识
     */
    val type: String,

    /**
     * 与该新闻相关联的话题列表
     */
    val topics: List<Topic>,
)

