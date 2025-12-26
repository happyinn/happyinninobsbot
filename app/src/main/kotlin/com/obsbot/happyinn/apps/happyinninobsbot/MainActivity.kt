package com.obsbot.happyinn.apps.happyinninobsbot

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.trace
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioTheme
import com.obsbot.happyinn.apps.happyinninobsbot.util.isSystemInDarkTheme
import dagger.hilt.android.AndroidEntryPoint
import com.obsbot.happyinn.apps.happyinninobsbot.MainActivityUiState.Loading
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserNewsResourceRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.util.NetworkMonitor
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.util.TimeZoneMonitor
import com.obsbot.happyinn.apps.happyinninobsbot.ui.HioApp
import com.obsbot.happyinn.apps.happyinninobsbot.ui.rememberHioAppState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * 延迟注入 JankStats，用于在整个应用中跟踪卡顿情�?
     */
/*    @Inject
    lateinit var lazyStats: dagger.Lazy<JankStats>*/

    /**
     * 网络监控器，用于监控网络连接状�?
     */
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    /**
     * 时区监控器，用于监控时区变化
     */
    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    /**
     * 分析助手，用于收集和报告应用使用数据
     */
/*    @Inject
    lateinit var analyticsHelper: AnalyticsHelper*/

    /**
     * 用户新闻资源仓库，用于管理用户相关的新闻内容
     */
    @Inject
    lateinit var userNewsResourceRepository: UserNewsResourceRepository


    /**
     * �?Activity �?ViewModel，用于管�?UI 状�?
     */
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // 安装启动画面
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // 我们将其保持为可变状态，以便在组合中跟踪变化�?
        // 这允许我们对深色/浅色模式变化做出反应�?
        var themeSettings by mutableStateOf(
            ThemeSettings(
                darkTheme = resources.configuration.isSystemInDarkTheme,
                androidTheme = Loading.shouldUseAndroidTheme,
                disableDynamicTheming = Loading.shouldDisableDynamicTheming,
            ),
        )

        //TODO 待深入学�?
        /**
        * - lifecycleScope ：Activity的生命周期作用域，确保协程在Activity销毁时自动取消
        - launch ：启动新的协程进行异步操�?
        - repeatOnLifecycle(Lifecycle.State.STARTED) ：这是Android推荐的最佳实践，确保�?
        - 仅在Activity处于STARTED状态时执行和收集流
        - 在Activity进入STOPPED状态时自动暂停收集
        - 在Activity恢复到STARTED状态时重新开始收�?
        - 有效避免内存泄漏和不必要的后台处�?
        */
        lifecycleScope.launch {
            // 流操作和收集逻辑
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                /**
                 * - combine ：组合两个流（系统深色主题状态流和ViewModel中的UI状态流�?
                 *
                 * - 当任一输入流发出新值时，组合函数会执行并发出新的结果
                 * - ThemeSettings ：一个数据类，用于封装完整的主题配置
                 *
                 * - 整合了系统主题状态和用户偏好设置
                 * - onEach ：在每个发射值上执行副作用操作，这里更新了一个类变量 themeSettings
                 * - map ：转换操作，从完整的主题设置中提取 darkTheme 布尔值
                 * - distinctUntilChanged ：优化操作，确保只有当 darkTheme 值真正改变时才继续传递
                 *
                 * - 避免不必要的UI更新和配置更改
                 * - collect ：终端操作符，开始收集流并执行配置更改
                 * */
                combine(
                    isSystemInDarkTheme(),
                    viewModel.uiState,
                ){
                        systemDark, uiState ->
                    ThemeSettings(
                        darkTheme = uiState.shouldUseDarkTheme(systemDark),
                        androidTheme = uiState.shouldUseAndroidTheme,
                        disableDynamicTheming = uiState.shouldDisableDynamicTheming,
                    )
                }
                    .catch { exception ->
                        // 处理潜在异常
                        Log.e("MainActivity", "主题流组合失�?, exception")
                        // 提供默认主题设置
                        emit(ThemeSettings(darkTheme = resources.configuration.isSystemInDarkTheme, androidTheme = true, disableDynamicTheming = false))
                    }
                    .onEach { themeSettings = it }
                    .onEach { Log.d("MainActivity", "主题设置更新: $it") }
                    .map { it.darkTheme }
                    .distinctUntilChanged()
                    .collect {
                        /**
                         * - trace ：使用AndroidX Tracing API标记代码块，用于性能分析和调�?
                         *
                         * - 有助于在性能分析工具中识别和监控此代码块的执行情�?
                         * - enableEdgeToEdge ：启用边缘到边缘显示效果，允许内容延伸到系统UI区域（状态栏和导航栏�?
                         * - SystemBarStyle.auto ：根据条件自动选择合适的系统栏样�?
                         *
                         * - 接收浅色和深色两种样式参�?
                         * - 最后一个lambda参数�?{ darkTheme } ）决定使用哪种样�?
                         * - 状态栏特殊处理 ：使用完全透明的scrim ( Color.TRANSPARENT )
                         *
                         * - 这使得状态栏背景完全透明，让内容可以无缝延伸
                         * - 导航栏处�?：使用预定义的半透明scrim�?
                         *
                         * - lightScrim �?Color.argb(0xe6, 0xFF, 0xFF, 0xFF) - 89.4%不透明的白�?
                         * - darkScrim �?Color.argb(0x80, 0x1b, 0x1b, 0x1b) - 50%不透明的深灰色
                         * */
                        darkTheme ->
                        trace("hioEdgeToEdge"){
                            //TODO 该方法有待深入了�?
                            enableEdgeToEdge(
                                statusBarStyle = SystemBarStyle.auto(
                                    lightScrim = android.graphics.Color.TRANSPARENT,
                                    darkScrim = android.graphics.Color.TRANSPARENT,
                                ) {
                                    //尾随 Lambda 参数
                                    darkTheme
                                  },
                                navigationBarStyle = SystemBarStyle.auto(
                                    lightScrim = lightScrim,
                                    darkScrim = darkScrim,
                                ) { darkTheme },
                            )
                        }
                    }
            }

            // 保持启动画面显示直到 UI 状态加载完成。这个条件在每次应用需要重绘时都会被评估，
            // 所以应该快速执行以避免阻塞 UI�?
            splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.shouldKeepSplashScreen() }
        }

        setContent {

            // 记住应用状态，包括网络监控、用户新闻资源和时区监控
            val appState = rememberHioAppState(
                networkMonitor = networkMonitor,
                userNewsResourceRepository = userNewsResourceRepository,
                timeZoneMonitor = timeZoneMonitor,
            )

            // 收集当前时区状�?
            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()



            HioTheme (
                darkTheme = themeSettings.darkTheme,
                androidTheme = themeSettings.androidTheme,
                disableDynamicTheming = themeSettings.disableDynamicTheming,
            ){
                // 显示主应用界�?
                HioApp(appState)
            }
        }
    }

    /**
     * Activity 恢复时启用卡顿统计跟踪
     */
    override fun onResume() {
        super.onResume()
//        lazyStats.get().isTrackingEnabled = true
    }

    /**
     * Activity 暂停时禁用卡顿统计跟踪
     */
    override fun onPause() {
        super.onPause()
//        lazyStats.get().isTrackingEnabled = false
    }

}



/**
 * 默认浅色遮罩，由 androidx 和平台定义：
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * 默认深色遮罩，由 androidx 和平台定义：
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)



/**
 * 系统主题设置类�?
 * 这个包装类允许我们将所有变化组合在一起，防止不必要的重新组合�?
 */
data class ThemeSettings(
    val darkTheme: Boolean,
    val androidTheme: Boolean,
    val disableDynamicTheming: Boolean,
)