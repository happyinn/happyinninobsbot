/*
 * 版权所有 2022 The Android Open Source Project
 *
 * 根据 Apache 许可证 2.0 版（"许可证"）授权；
 * 除非符合许可证要求，否则您不得使用此文件。
 * 您可以在以下位置获得许可证副本：
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，根据许可证分发的软件
 * 是基于"按原样"的基础上分发的，不附带任何明示或暗示的担保条件。
 * 请参阅许可证了解特定语言 governing permissions 和 limitations。
 */

package com.obsbot.happyinn.apps.happyinninobsbot

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy.Builder
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.obsbot.happyinn.apps.happyinninobsbot.sync.initializers.Sync
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Now in Android 应用的 Application 类
 * 使用 Hilt 进行依赖注入，并实现 ImageLoaderFactory 接口
 */
@HiltAndroidApp
class HioApplication : Application(), ImageLoaderFactory {
    /**
     * 注入的图片加载器，使用 Lazy 延迟加载
     */
    @Inject
    lateinit var imageLoader: Lazy<ImageLoader>

    /**
     * 注入的性能分析验证器日志记录器
     */
/*    @Inject
    lateinit var profileVerifierLogger: ProfileVerifierLogger*/

    /**
     * 应用创建时的回调方法
     * 负责初始化各种应用组件和服务
     */
    override fun onCreate() {
        super.onCreate()

        // 设置严格模式策略
        setStrictModePolicy()

        // 初始化同步系统；负责保持应用数据的最新状态
        Sync.initialize(context = this)
        // 记录性能分析验证器日志
//        profileVerifierLogger()
    }

    /**
     * 创建新的图片加载器实例
     *
     * @return ImageLoader 图片加载器实例
     */
    override fun newImageLoader(): ImageLoader = imageLoader.get()

    /**
     * 判断应用是否处于调试模式
     *
     * @return Boolean 如果应用可调试则返回 true，否则返回 false
     */
    private fun isDebuggable(): Boolean {
        return 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
    }

    /**
     * 设置主线程的严格模式策略
     * 检测主线程上的所有潜在问题，如网络和磁盘访问
     *
     * 如果发现问题，违规调用将被记录并且应用将被终止
     */
    private fun setStrictModePolicy() {
        if (isDebuggable()) {
            StrictMode.setThreadPolicy(
                Builder().detectAll().penaltyLog().build(),
            )
        }
    }
}
