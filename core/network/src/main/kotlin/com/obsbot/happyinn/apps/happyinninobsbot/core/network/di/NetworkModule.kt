package com.obsbot.happyinn.apps.happyinninobsbot.core.network.di

import android.content.Context
import androidx.tracing.trace
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.util.DebugLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.BuildConfig
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.demo.DemoAssetManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton
import kotlin.apply

/**
 * 网络模块 - 提供网络相关依赖的 Dagger Hilt 模块
 * 安装在 SingletonComponent 中，确保单例模式
 */
@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    /**
     * 提供网络 JSON 序列化器
     * 配置为忽略未知键，避免序列化异常
     */
    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * 提供演示资源管理器
     * 用于管理应用内的演示资源文件
     */
    @Provides
    @Singleton
    fun providesDemoAssetManager(
        @ApplicationContext context: Context,
    ): DemoAssetManager = DemoAssetManager(context.assets::open)

    /**
     * 提供 OkHttp 网络请求客户端工厂
     * 在 DEBUG 模式下启用详细的网络日志记录
     */
    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory = trace("NiaOkHttpClient") {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    },
            )
            .build()
    }

    /**
     * Since we're displaying SVGs in the app, Coil needs an ImageLoader which supports this
     * format. During Coil's initialization it will call `applicationContext.newImageLoader()` to
     * obtain an ImageLoader.
     *
     * @see <a href="https://github.com/coil-kt/coil/blob/main/coil-singleton/src/main/java/coil/Coil.kt">Coil</a>
     */
    /**
     * 提供图片加载器 ImageLoader
     * 支持 SVG 格式图片解码
     * 配置网络请求工厂和缓存策略
     * DEBUG 模式下启用调试日志
     */
    @Provides
    @Singleton
    fun imageLoader(
        // 使用 Lazy 延迟加载，避免在 Dagger 初始化时立即实例化
        okHttpCallFactory: dagger.Lazy<Call.Factory>,
        @ApplicationContext application: Context,
    ): ImageLoader = trace("NiaImageLoader") {
        ImageLoader.Builder(application)
            .callFactory { okHttpCallFactory.get() }
            .components { add(SvgDecoder.Factory()) }
            // 假设大多数内容图片都是版本化 URL，不严格遵守缓存头
            .respectCacheHeaders(false)
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()
    }
}
