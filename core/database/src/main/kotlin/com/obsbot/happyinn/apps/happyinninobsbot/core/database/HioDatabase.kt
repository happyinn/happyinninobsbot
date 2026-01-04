package com.obsbot.happyinn.apps.happyinninobsbot.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.NewsResourceDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.NewsResourceFtsDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.RecentSearchQueryDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.TopicDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.TopicFtsDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.NewsResourceEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.NewsResourceFtsEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.NewsResourceTopicCrossRef
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.RecentSearchQueryEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.TopicEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.TopicFtsEntity
import com.google.samples.apps.nowinandroid.core.database.util.InstantConverter
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.media.DirectoryDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.media.MediumDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.media.MediumStateDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.AudioStreamInfoEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.DirectoryEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.MediumEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.MediumStateEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.SubtitleStreamInfoEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.VideoStreamInfoEntity
@Database(
    entities = [
        NewsResourceEntity::class,
        NewsResourceTopicCrossRef::class,
        NewsResourceFtsEntity::class,
        TopicEntity::class,
        TopicFtsEntity::class,
        RecentSearchQueryEntity::class,

        // 添加媒体相关实体
        MediumEntity::class,
        DirectoryEntity::class,
        MediumStateEntity::class,
        VideoStreamInfoEntity::class,
        AudioStreamInfoEntity::class,
        SubtitleStreamInfoEntity::class,
    ],
    /*version = 14,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = DatabaseMigrations.Schema2to3::class),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11, spec = DatabaseMigrations.Schema10to11::class),
        AutoMigration(from = 11, to = 12, spec = DatabaseMigrations.Schema11to12::class),
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14),
    ],*/
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class HioDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun newsResourceDao(): NewsResourceDao
    abstract fun topicFtsDao(): TopicFtsDao
    abstract fun newsResourceFtsDao(): NewsResourceFtsDao
    abstract fun recentSearchQueryDao(): RecentSearchQueryDao
    abstract fun mediumDao(): MediumDao
    abstract fun directoryDao(): DirectoryDao
    abstract fun mediumStateDao(): MediumStateDao

}