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
import kotlin.collections.map

/**
 * 将保存的ID从[Int]类型迁移到[String]类型的数据迁移对象
 */
internal object IntToStringIdsMigration : DataMigration<UserPreferences> {

    /**
     * 清理操作，在迁移完成后调用
     * 此迁移不需要清理操作，所以返回Unit
     */
    override suspend fun cleanUp() = Unit

    /**
     * 执行数据迁移的核心方法
     * 将用户偏好设置中的整数ID转换为字符串ID
     *
     * @param currentData 当前的用户偏好设置数据
     * @return UserPreferences 迁移后的用户偏好设置数据
     */
    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy {
            // 迁移关注的话题ID
            deprecatedFollowedTopicIds.clear()
            deprecatedFollowedTopicIds.addAll(
                currentData.deprecatedIntFollowedTopicIdsList.map(Int::toString),
            )
            deprecatedIntFollowedTopicIds.clear()

            // 迁移关注的作者ID
            deprecatedFollowedAuthorIds.clear()
            deprecatedFollowedAuthorIds.addAll(
                currentData.deprecatedIntFollowedAuthorIdsList.map(Int::toString),
            )
            deprecatedIntFollowedAuthorIds.clear()

            // 标记迁移已完成
            hasDoneIntToStringIdMigration = true
        }

    /**
     * 判断是否需要执行迁移
     *
     * @param currentData 当前的用户偏好设置数据
     * @return Boolean 如果尚未完成整数到字符串ID的迁移则返回true，否则返回false
     */
    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean =
        !currentData.hasDoneIntToStringIdMigration
}
