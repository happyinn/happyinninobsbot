package com.obsbot.happyinn.apps.happyinninobsbot.core.database.di

import android.content.Context
import androidx.room.Room
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.HioDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库模块，提供数据库实例的依赖注入配置
 */
@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    /**
     * 提供 Nia 数据库单例实例
     *
     * @param context 应用上下文
     * @return HioDatabase 数据库实例
     */
    @Provides
    @Singleton
    fun providesNiaDatabase(
        @ApplicationContext context: Context,
    ): HioDatabase = Room.databaseBuilder(
        context,
        HioDatabase::class.java,
        "hio-database",
    ).build()
}
