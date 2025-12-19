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
package com.obsbot.happyinn.apps.happyinninobsbot.core.datastore

import androidx.datastore.core.DataMigration
import kotlin.collections.associateWith

/**
 * 将用户数据从使用列表(list)迁移到使用映射(map)的数据迁移对象
 */
internal object ListToMapMigration : DataMigration<UserPreferences> {

    /**
     * 清理操作，在迁移完成后调用
     * 此迁移不需要清理操作，所以返回Unit
     */
    override suspend fun cleanUp() = Unit

    /**
     * 执行数据迁移的核心方法
     * 将用户偏好设置中的列表数据结构转换为映射数据结构
     *
     * @param currentData 当前的用户偏好设置数据
     * @return UserPreferences 迁移后的用户偏好设置数据
     */
    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy {
            // 迁移关注的话题ID列表到映射
            followedTopicIds.clear()
            followedTopicIds.putAll(
                currentData.deprecatedFollowedTopicIdsList.associateWith { true },
            )
            deprecatedFollowedTopicIds.clear()

            // 迁移关注的作者ID列表到映射
            followedAuthorIds.clear()
            followedAuthorIds.putAll(
                currentData.deprecatedFollowedAuthorIdsList.associateWith { true },
            )
            deprecatedFollowedAuthorIds.clear()

            // 迁移书签新闻资源ID列表到映射
            bookmarkedVideosResourceIds.clear()
            bookmarkedVideosResourceIds.putAll(
                currentData.deprecatedBookmarkedVideosResourceIdsList.associateWith { true },
            )
            deprecatedBookmarkedVideosResourceIds.clear()

            // 标记迁移已完成
            hasDoneListToMapMigration = true
        }

    /**
     * 判断是否需要执行迁移
     *
     * @param currentData 当前的用户偏好设置数据
     * @return Boolean 如果尚未完成列表到映射的迁移则返回true，否则返回false
     */
    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean =
        !currentData.hasDoneListToMapMigration
}
