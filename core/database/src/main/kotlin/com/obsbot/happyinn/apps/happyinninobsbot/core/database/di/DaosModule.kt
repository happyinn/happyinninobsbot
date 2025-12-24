package com.obsbot.happyinn.apps.happyinninobsbot.core.database.di

import com.obsbot.happyinn.apps.happyinninobsbot.core.database.NiaDatabase
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.VideosResourceDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.VideosResourceFtsDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.RecentSearchQueryDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.TopicDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.TopicFtsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * DAOs 模块，提供数据库访问对象的依赖注入配置
 */
@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    /**
     * 提供主题 DAO 实例
     *
     * @param database Nia 数据库实例
     * @return TopicDao 实例
     */
    @Provides
    fun providesTopicsDao(
        database: NiaDatabase,
    ): TopicDao = database.topicDao()

    /**
     * 提供新闻资源 DAO 实例
     *
     * @param database Nia 数据库实例
     * @return VideosResourceDao 实例
     */
    @Provides
    fun providesVideosResourceDao(
        database: NiaDatabase,
    ): VideosResourceDao = database.videosResourceDao()

    /**
     * 提供主题全文搜索 DAO 实例
     *
     * @param database Nia 数据库实例
     * @return TopicFtsDao 实例
     */
    @Provides
    fun providesTopicFtsDao(
        database: NiaDatabase,
    ): TopicFtsDao = database.topicFtsDao()

    /**
     * 提供新闻资源全文搜索 DAO 实例
     *
     * @param database Nia 数据库实例
     * @return VideosResourceFtsDao 实例
     */
    @Provides
    fun providesVideosResourceFtsDao(
        database: NiaDatabase,
    ): VideosResourceFtsDao = database.videosResourceFtsDao()

    /**
     * 提供最近搜索查询 DAO 实例
     *
     * @param database Nia 数据库实例
     * @return RecentSearchQueryDao 实例
     */
    @Provides
    fun providesRecentSearchQueryDao(
        database: NiaDatabase,
    ): RecentSearchQueryDao = database.recentSearchQueryDao()
}
