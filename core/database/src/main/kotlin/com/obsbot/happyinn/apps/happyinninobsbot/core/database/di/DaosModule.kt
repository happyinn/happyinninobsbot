package com.obsbot.happyinn.apps.happyinninobsbot.core.database.di

import com.obsbot.happyinn.apps.happyinninobsbot.core.database.HioDatabase
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.media.DirectoryDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.media.MediumDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.media.MediumStateDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.NewsResourceDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.NewsResourceFtsDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.RecentSearchQueryDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.TopicDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.TopicFtsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * DAOs 模块，提供数据库访问对象的依赖注入配�?
 */
@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    /**
     * 提供主题 DAO 实例
     *
     * @param database Nia 数据库实�?
     * @return TopicDao 实例
     */
    @Provides
    fun providesTopicsDao(
        database: HioDatabase,
    ): TopicDao = database.topicDao()

    /**
     * 提供新闻资源 DAO 实例
     *
     * @param database Nia 数据库实�?
     * @return NewsResourceDao 实例
     */
    @Provides
    fun providesNewsResourceDao(
        database: HioDatabase,
    ): NewsResourceDao = database.newsResourceDao()

    /**
     * 提供主题全文搜索 DAO 实例
     *
     * @param database Nia 数据库实�?
     * @return TopicFtsDao 实例
     */
    @Provides
    fun providesTopicFtsDao(
        database: HioDatabase,
    ): TopicFtsDao = database.topicFtsDao()

    /**
     * 提供新闻资源全文搜索 DAO 实例
     *
     * @param database Nia 数据库实�?
     * @return NewsResourceFtsDao 实例
     */
    @Provides
    fun providesNewsResourceFtsDao(
        database: HioDatabase,
    ): NewsResourceFtsDao = database.newsResourceFtsDao()

    /**
     * 提供最近搜索查�?DAO 实例
     *
     * @param database Nia 数据库实�?
     * @return RecentSearchQueryDao 实例
     */
    @Provides
    fun providesRecentSearchQueryDao(
        database: HioDatabase,
    ): RecentSearchQueryDao = database.recentSearchQueryDao()



    @Provides
    fun providesMediumDao(
        database: HioDatabase,
    ): MediumDao = database.mediumDao()

    @Provides
    fun providesDirectoryDao(
        database: HioDatabase,
    ): DirectoryDao = database.directoryDao()

    @Provides
    fun providesMediumStateDao(
        database: HioDatabase,
    ): MediumStateDao = database.mediumStateDao()

}

