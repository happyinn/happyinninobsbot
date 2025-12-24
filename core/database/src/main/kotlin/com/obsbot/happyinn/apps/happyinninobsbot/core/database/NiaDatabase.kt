package com.obsbot.happyinn.apps.happyinninobsbot.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.VideosResourceDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.VideosResourceFtsDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.RecentSearchQueryDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.TopicDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.TopicFtsDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.VideosResourceEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.VideosResourceFtsEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.VideosResourceTopicCrossRef
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.RecentSearchQueryEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.TopicEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.TopicFtsEntity
import com.google.samples.apps.nowinandroid.core.database.util.InstantConverter

@Database(
    entities = [
        VideosResourceEntity::class,
        VideosResourceTopicCrossRef::class,
        VideosResourceFtsEntity::class,
        TopicEntity::class,
        TopicFtsEntity::class,
        RecentSearchQueryEntity::class,
    ],
    version = 14,
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
    ],
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class NiaDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun videosResourceDao(): VideosResourceDao
    abstract fun topicFtsDao(): TopicFtsDao
    abstract fun videosResourceFtsDao(): VideosResourceFtsDao
    abstract fun recentSearchQueryDao(): RecentSearchQueryDao
}
