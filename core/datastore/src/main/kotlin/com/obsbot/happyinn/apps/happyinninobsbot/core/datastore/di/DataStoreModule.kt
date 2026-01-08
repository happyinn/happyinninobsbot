package com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.di
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.google.samples.apps.nowinandroid.core.network.di.ApplicationScope
import com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.IntToStringIdsMigration
import com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.UserPreferences
import com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.UserPreferencesSerializer
import com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.serializer.PlayerPreferencesSerializer
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.PlayerPreferences
import com.obsbot.happyinn.apps.happyinninobsbot.network.Dispatcher
import com.obsbot.happyinn.apps.happyinninobsbot.network.HioDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton
import com.obsbot.happyinn.apps.happyinninobsbot.network.HioDispatchers.IO

private const val PLAYER_PREFERENCES_DATASTORE_FILE = "player_preferences.json"


/**
 * DataStore依赖注入模块
 * 提供用户偏好设置DataStore的创建和配置
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * 提供用户偏好设置的DataStore实例
     *
     * @param context 应用上下文，用于获取DataStore文件存储位置
     * @param ioDispatcher IO调度器，用于执行DataStore的IO操作
     * @param scope 应用作用域，用于DataStore的协程作用域
     * @param userPreferencesSerializer 用户偏好设置序列化器
     * @return DataStore<UserPreferences> 用户偏好设置DataStore实例
     */
    @Provides
    @Singleton
    internal fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserPreferencesSerializer,
    ): DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
            migrations = listOf(
                IntToStringIdsMigration,
            ),
        ) {
            context.dataStoreFile("user_preferences.pb")
        }

    @Provides
    @Singleton
    fun providePlayerPreferencesDataStore(
        @ApplicationContext applicationContext: Context,
        @Dispatcher(IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
    ): DataStore<PlayerPreferences> {
        return DataStoreFactory.create(
            serializer = PlayerPreferencesSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
            produceFile = { applicationContext.dataStoreFile(PLAYER_PREFERENCES_DATASTORE_FILE) },
        )
    }
}
