package com.obsbot.happyinn.apps.happyinninobsbot.feature.player

import android.annotation.SuppressLint
// 导入 SuppressLint 注解：用于抑制特定的 lint 警告
import android.app.AppOpsManager
// 导入 AppOpsManager 类：用于管理应用操作权限
import android.app.PendingIntent
// 导入 PendingIntent 类：用于延迟执行的 Intent
import android.app.PictureInPictureParams
// 导入 PictureInPictureParams 类：用于配置画中画参数
import android.app.RemoteAction
// 导入 RemoteAction 类：用于定义远程操作
import android.content.BroadcastReceiver
// 导入 BroadcastReceiver 类：用于接收广播
import android.content.ComponentName
// 导入 ComponentName 类：用于标识应用组件
import android.content.Context
// 导入 Context 类：Android 上下文
import android.content.Intent
// 导入 Intent 类：用于启动组件和传递数据
import android.content.IntentFilter
// 导入 IntentFilter 类：用于过滤广播
import android.content.pm.ActivityInfo
// 导入 ActivityInfo 类：用于设置 Activity 方向
import android.content.pm.PackageManager
// 导入 PackageManager 类：用于获取应用信息
import android.content.res.Configuration
// 导入 Configuration 类：用于获取设备配置信息
import android.graphics.Color
// 导入 Color 类：用于颜色定义
import android.graphics.Rect
// 导入 Rect 类：用于定义矩形区域
import android.graphics.Typeface
// 导入 Typeface 类：用于字体设置
import android.graphics.drawable.Icon
// 导入 Icon 类：用于图标定义
import android.media.AudioManager
// 导入 AudioManager 类：用于音频管理
import android.media.audiofx.LoudnessEnhancer
// 导入 LoudnessEnhancer 类：用于音量增强
import android.net.Uri
// 导入 Uri 类：用于统一资源标识符
import android.os.Build
// 导入 Build 类：用于获取设备信息和 API 级别
import android.os.Bundle
// 导入 Bundle 类：用于存储和恢复数据
import android.os.Process
// 导入 Process 类：用于进程管理
import android.util.Rational
// 导入 Rational 类：用于表示分数
import android.util.TypedValue
// 导入 TypedValue 类：用于类型值转换
import android.view.Gravity
// 导入 Gravity 类：用于布局重力
import android.view.KeyEvent
// 导入 KeyEvent 类：用于键盘事件
import android.view.View
// 导入 View 类：Android 视图基类
import android.view.ViewGroup.LayoutParams
// 导入 LayoutParams 类：用于视图布局参数
import android.view.WindowManager
// 导入 WindowManager 类：用于窗口管理
import android.view.accessibility.CaptioningManager
// 导入 CaptioningManager 类：用于字幕管理
import android.widget.FrameLayout
// 导入 FrameLayout 类：帧布局
import android.widget.ImageButton
// 导入 ImageButton 类：图片按钮
import android.widget.LinearLayout
// 导入 LinearLayout 类：线性布局
import android.widget.TextView
// 导入 TextView 类：文本视图
import android.widget.Toast
// 导入 Toast 类：用于显示短消息
import androidx.activity.addCallback
// 导入 addCallback 扩展函数：用于添加返回按钮回调
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
// 导入 OpenDocument 类：用于打开文档
import androidx.activity.viewModels
// 导入 viewModels 扩展函数：用于获取 ViewModel
import androidx.annotation.DrawableRes
// 导入 DrawableRes 注解：用于标识 Drawable 资源
import androidx.annotation.RequiresApi
// 导入 RequiresApi 注解：用于标识需要特定 API 级别
import androidx.appcompat.app.AppCompatActivity
// 导入 AppCompatActivity 类：兼容包的 Activity 基类
import androidx.appcompat.app.AppCompatDelegate
// 导入 AppCompatDelegate 类：用于 AppCompat 代理
import androidx.core.net.toUri
// 导入 toUri 扩展函数：用于转换为 Uri
import androidx.core.view.WindowCompat
// 导入 WindowCompat 类：用于窗口兼容
import androidx.lifecycle.lifecycleScope
// 导入 lifecycleScope 扩展属性：生命周期绑定的协程作用域
import androidx.media3.common.C
// 导入 C 类：Media3 库的常量类
import androidx.media3.common.MediaItem
// 导入 MediaItem 类：Media3 媒体项
import androidx.media3.common.MediaMetadata
// 导入 MediaMetadata 类：媒体元数据
import androidx.media3.common.MimeTypes
// 导入 MimeTypes 类：媒体类型
import androidx.media3.common.PlaybackException
// 导入 PlaybackException 类：播放异常
import androidx.media3.common.Player
// 导入 Player 类：Media3 播放器接口
import androidx.media3.common.VideoSize
// 导入 VideoSize 类：视频尺寸
import androidx.media3.session.MediaController
// 导入 MediaController 类：Media3 媒体控制器
import androidx.media3.session.SessionToken
// 导入 SessionToken 类：媒体会话令牌
import androidx.media3.ui.AspectRatioFrameLayout
// 导入 AspectRatioFrameLayout 类：保持宽高比的帧布局
import androidx.media3.ui.CaptionStyleCompat
// 导入 CaptionStyleCompat 类：字幕样式
import androidx.media3.ui.PlayerView
// 导入 PlayerView 类：Media3 播放器视图
import androidx.media3.ui.SubtitleView
// 导入 SubtitleView 类：字幕视图
import androidx.media3.ui.TimeBar
// 导入 TimeBar 类：时间条
import com.google.android.material.color.DynamicColors
// 导入 DynamicColors 类：动态颜色
import com.google.android.material.dialog.MaterialAlertDialogBuilder
// 导入 MaterialAlertDialogBuilder 类：Material 对话框构建器
import com.google.common.util.concurrent.ListenableFuture
// 导入 ListenableFuture 类：可监听的 Future
import com.obsbot.happyinn.apps.happyinninobsbot.Utils
// 导入 Utils 类：工具类
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.DarkThemeConfig
// 导入 DarkThemeConfig 类：深色主题配置
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.ControlButtonsPosition
// 导入 ControlButtonsPosition 类：控制按钮位置
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.LoopMode
// 导入 LoopMode 类：循环模式
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.VideoZoom
// 导入 VideoZoom 类：视频缩放
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.getMediaContentUri
// 导入 getMediaContentUri 扩展函数：获取媒体内容 Uri
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.isDeviceTvBox
// 导入 isDeviceTvBox 扩展函数：判断是否为 TV 盒子
import dagger.hilt.android.AndroidEntryPoint
// 导入 AndroidEntryPoint 注解：Hilt 注入入口

import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.databinding.ActivityPlayerBinding
// 导入 ActivityPlayerBinding 类：Activity 布局绑定
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.dialogs.PlaybackSpeedControlsDialogFragment
// 导入 PlaybackSpeedControlsDialogFragment 类：播放速度控制对话框
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.dialogs.TrackSelectionDialogFragment
// 导入 TrackSelectionDialogFragment 类：轨道选择对话框
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.dialogs.VideoZoomOptionsDialogFragment
// 导入 VideoZoomOptionsDialogFragment 类：视频缩放选项对话框
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.dialogs.nameRes
// 导入 nameRes 扩展属性：名称资源
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.isPortrait
// 导入 isPortrait 扩展属性：判断是否为竖屏
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.next
// 导入 next 扩展函数：获取下一个值
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.seekBack
// 导入 seekBack 扩展函数：向后跳转
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.seekForward
// 导入 seekForward 扩展函数：向前跳转
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.setImageDrawable
// 导入 setImageDrawable 扩展函数：设置图片
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.shouldFastSeek
// 导入 shouldFastSeek 扩展函数：是否应该快速跳转
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.toActivityOrientation
// 导入 toActivityOrientation 扩展函数：转换为 Activity 方向
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.toTypeface
// 导入 toTypeface 扩展函数：转换为字体
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.togglePlayPause
// 导入 togglePlayPause 扩展函数：切换播放/暂停
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.toggleSystemBars
// 导入 toggleSystemBars 扩展函数：切换系统栏显示
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.uriToSubtitleConfiguration
// 导入 uriToSubtitleConfiguration 扩展函数：转换为字幕配置
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.PlayerService
// 导入 PlayerService 类：播放器服务
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.addSubtitleTrack
// 导入 addSubtitleTrack 扩展函数：添加字幕轨道
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.getAudioSessionId
// 导入 getAudioSessionId 扩展函数：获取音频会话 ID
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.getSkipSilenceEnabled
// 导入 getSkipSilenceEnabled 扩展函数：获取跳过静音状态
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.stopPlayerSession
// 导入 stopPlayerSession 扩展函数：停止播放会话
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.switchAudioTrack
// 导入 switchAudioTrack 扩展函数：切换音频轨道
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.switchSubtitleTrack
// 导入 switchSubtitleTrack 扩展函数：切换字幕轨道
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils.BrightnessManager
// 导入 BrightnessManager 类：亮度管理器
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils.PlayerApi
// 导入 PlayerApi 类：播放器 API
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils.PlayerGestureHelper
// 导入 PlayerGestureHelper 类：手势帮助类
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils.VolumeManager
// 导入 VolumeManager 类：音量管理器
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils.toMillis
// 导入 toMillis 扩展属性：转换为毫秒
import kotlin.apply
// 导入 apply 函数：作用域函数
import kotlinx.coroutines.Dispatchers
// 导入 Dispatchers 类：协程调度器
import kotlinx.coroutines.Job
// 导入 Job 类：协程任务
import kotlinx.coroutines.delay
// 导入 delay 函数：协程延迟
import kotlinx.coroutines.guava.await
// 导入 await 扩展函数：用于 ListenableFuture
import kotlinx.coroutines.launch
// 导入 launch 函数：启动协程
import kotlinx.coroutines.withContext
// 导入 withContext 函数：切换协程上下文
import timber.log.Timber
// 导入 Timber 类：日志工具
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R as coreUiR

// 导入核心 UI 模块的资源类，并指定别名

@SuppressLint("UnsafeOptInUsageError")
// 抑制不安全 API 使用警告
@AndroidEntryPoint
// Hilt 注入入口注解
/**
 * 播放器 Activity
 * 负责渲染播放界面、响应手势/按键、控制 MediaController 并处理画中画、亮度音量、字幕/音轨切换等交互。
 * 通过 Hilt 注入 ViewModel 和各类管理器，关联 PlayerService 完成播放。
 */
class PlayerActivity : AppCompatActivity() {
    // 类声明：继承自 AppCompatActivity

    /** ViewBinding：对应 `activity_player.xml`，用于访问布局中的所有视图 */
    lateinit var binding: ActivityPlayerBinding
    // 延迟初始化的 ViewBinding 对象，用于访问布局视图

    /** 播放器 ViewModel，负责读取和保存播放相关的偏好与状态 */
    private val viewModel: PlayerViewModel by viewModels()

    // 通过 Hilt 注入的 ViewModel，用于管理播放状态和偏好设置
    /** 全局应用偏好（主题、动态色、悬浮按钮等） */
    private val userData get() = viewModel.appPrefs.value

    // 全局应用偏好，通过 ViewModel 获取
    /** 播放器偏好设置（快进方式、字幕样式、缩放模式等） */
    private val playerPreferences get() = viewModel.playerPrefs.value
    // 播放器偏好设置，通过 ViewModel 获取

    /** 当前此次播放会话是否因自然播放完成（STATE_ENDED）结束 */
    private var isPlaybackFinished = false
    // 标记播放是否自然结束

    /** 媒体项是否已准备完成（用于防抖一些手势逻辑） */
    var isMediaItemReady = false

    // 标记媒体项是否准备完成
    /** 控制是否锁定播放器 UI 控件（锁定时仅显示解锁区域） */
    var isControlsLocked = false

    // 标记播放器控件是否锁定
    /** 是否已经渲染出至少一帧视频画面（用于拖动进度条时的防抖） */
    private var isFrameRendered = false

    // 标记是否已渲染出视频帧
    /** 拖动进度开始时，是否处于播放状态 */
    private var isPlayingOnScrubStart: Boolean = false

    // 标记拖动进度开始时是否正在播放
    /** 上一次拖动进度条时的位置，用于判断快进/后退方向 */
    private var previousScrubPosition = 0L

    // 上一次拖动进度的位置
    /** 本次拖动开始时的播放位置，用于显示相对偏移 */
    private var scrubStartPosition: Long = -1L

    // 拖动开始时的播放位置
    /** 记录当前屏幕方向，方便退出时恢复 */
    private var currentOrientation: Int? = null

    // 当前屏幕方向
    /** 延迟隐藏音量手势提示的协程任务 */
    private var hideVolumeIndicatorJob: Job? = null

    // 用于延迟隐藏音量手势提示的协程任务
    /** 延迟隐藏亮度手势提示的协程任务 */
    private var hideBrightnessIndicatorJob: Job? = null

    // 用于延迟隐藏亮度手势提示的协程任务
    /** 延迟隐藏信息提示布局的协程任务 */
    private var hideInfoLayoutJob: Job? = null
    // 用于延迟隐藏信息提示布局的协程任务

    /** 是否允许后台播放（点击“后台播放”按钮或开启自动后台播放） */
    private var playInBackground: Boolean = false

    // 标记是否允许后台播放
    /** 标记当前 Intent 是否为新 Intent，用于决定是否重建播放列表 */
    private var isIntentNew: Boolean = true
    // 标记当前 Intent 是否为新 Intent

    /** 当前是否处于画中画模式 */
    private var isPipActive: Boolean = false
    // 标记是否处于画中画模式

    /**
     * 是否应该启用“快速快进/后退”
     * 由用户偏好 + 当前媒体时长共同决定
     */
    private val shouldFastSeek: Boolean
        get() = playerPreferences.shouldFastSeek(mediaController?.duration ?: C.TIME_UNSET)
    // 计算是否应该启用快速快进/后退

    /**
     * Player
     */
    /** 异步创建 MediaController 的 Future，用于连接 PlayerService */
    private var controllerFuture: ListenableFuture<MediaController>? = null
    // 用于异步创建 MediaController 的 Future 对象
    /** 实际用于控制播放的 MediaController（连接到 PlayerService） */
    private var mediaController: MediaController? = null
    // 实际用于控制播放的 MediaController 对象
    /** 手势帮助类，负责处理单击/双击/长按/滑动等手势 */
    private lateinit var playerGestureHelper: PlayerGestureHelper
    // 手势帮助类，处理各种手势事件
    /** 对外 API 封装，用于读取外部传入的标题、进度、字幕等参数 */
    private lateinit var playerApi: PlayerApi
    // 对外 API 封装，处理外部传入的参数
    /** 音量管理器，处理系统音量和增益效果 */
    private lateinit var volumeManager: VolumeManager
    // 音量管理器，处理音量调整
    /** 亮度管理器，处理屏幕亮度调整 */
    private lateinit var brightnessManager: BrightnessManager
    // 亮度管理器，处理亮度调整
    /** 画中画广播接收器，用于响应 PIP 控制按钮 */
    private var pipBroadcastReceiver: BroadcastReceiver? = null
    // 画中画广播接收器，响应画中画控制按钮

    /**
     * Listeners
     */
    /** 播放状态监听器，集中处理 MediaController 的各种回调 */
    private val playbackStateListener: Player.Listener = playbackStateListener()
    // 播放状态监听器，处理 MediaController 的各种回调
    /** 记录当前为哪个 MediaItem 打开本地字幕选择器（避免切换媒体时错位） */
    private var subtitleFileLauncherLaunchedForMediaItem: MediaItem? = null
    // 记录当前为哪个 MediaItem 打开本地字幕选择器

    // 注册活动结果监听器，用于选择本地字幕文件
    private val subtitleFileLauncher = registerForActivityResult(OpenDocument()) { uri ->
        // 当选择了文件时触发
        if (uri != null && subtitleFileLauncherLaunchedForMediaItem != null) {
            // 获取持久化的 Uri 权限
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // 在协程中处理
            lifecycleScope.launch {
                // 确保 MediaController 已初始化
                maybeInitControllerFuture()
                // 添加字幕轨道
                controllerFuture?.await()?.addSubtitleTrack(uri)
            }
        }
    }

    /**
     * 各种播放器控制视图的引用
     */
    private lateinit var audioTrackButton: ImageButton  // 音频轨道按钮

    private lateinit var backButton: ImageButton // 返回按钮

    private lateinit var exoContentFrameLayout: AspectRatioFrameLayout  // 内容帧布局

    private lateinit var lockControlsButton: ImageButton  // 锁定控件按钮

    private lateinit var playbackSpeedButton: ImageButton // 播放速度按钮

    private lateinit var playerLockControls: FrameLayout // 锁定状态控件

    private lateinit var playerUnlockControls: FrameLayout // 解锁状态控件

    private lateinit var playerCenterControls: LinearLayout // 中央控制按钮

    private lateinit var screenRotateButton: ImageButton  // 屏幕旋转按钮

    private lateinit var pipButton: ImageButton // 画中画按钮

    private lateinit var seekBar: TimeBar // 进度条

    private lateinit var subtitleTrackButton: ImageButton // 字幕轨道按钮

    private lateinit var unlockControlsButton: ImageButton // 解锁控件按钮

    private lateinit var videoTitleTextView: TextView // 视频标题文本

    private lateinit var videoZoomButton: ImageButton // 视频缩放按钮

    private lateinit var playInBackgroundButton: ImageButton // 后台播放按钮

    private lateinit var loopModeButton: ImageButton // 循环模式按钮

    private lateinit var extraControls: LinearLayout // 额外控制按钮


    /**
     * 设备是否支持画中画功能（API 26+ 且具备 PIP 特性）
     */
    private val isPipSupported: Boolean by lazy {
        // 检查 API 级别和系统特性
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && packageManager.hasSystemFeature(
            PackageManager.FEATURE_PICTURE_IN_PICTURE
        )
    }

    // 懒加载，判断设备是否支持画中画
    /**
     * 当前应用是否已在系统设置中开启画中画权限
     */
    private val isPipEnabled: Boolean
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 获取 AppOpsManager
                val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // 检查画中画权限（Android 10+）
                    appOps?.unsafeCheckOpNoThrow(
                        AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                        Process.myUid(),
                        packageName
                    ) == AppOpsManager.MODE_ALLOWED
                } else {
                    // 检查画中画权限（Android 8.0-9）
                    @Suppress("DEPRECATION")
                    appOps?.checkOpNoThrow(
                        AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                        Process.myUid(),
                        packageName
                    ) == AppOpsManager.MODE_ALLOWED
                }
            } else {
                // 不支持画中画
                false
            }
        }
    // 判断应用是否已开启画中画权限

    /**
     * Activity 创建入口：
     * - 根据用户偏好设置夜间模式和动态色
     * - 配置刘海区域显示策略和沉浸式系统栏
     * - 初始化 ViewBinding 和所有控制按钮
     * - 创建音量/亮度/手势管理器
     * - 自定义返回键逻辑（TV 设备上先隐藏控制条，再退出）
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // 重写 onCreate 方法，Activity 创建时调用
        super.onCreate(savedInstanceState)
        // 调用父类的 onCreate 方法

        // 根据用户偏好设置夜间模式
        AppCompatDelegate.setDefaultNightMode(
            when (userData.darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                // 跟随系统主题
                DarkThemeConfig.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                // 亮色主题
                DarkThemeConfig.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                // 暗色主题
            },
        )

        // 如果启用动态颜色，应用动态颜色
        if (userData.useDynamicColor) {
            DynamicColors.applyToActivityIfAvailable(this)
        }

        // 配置刘海区域显示策略：允许窗口扩展到短边的刘海区域
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        // 设置窗口不适合系统窗口，实现沉浸式体验
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 初始化 ViewBinding
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        // 设置内容视图
        setContentView(binding.root)

        // 初始化视图：从 PlayerView 中获取各个控制按钮和控件
        audioTrackButton = binding.playerView.findViewById(R.id.btn_audio_track)
        backButton = binding.playerView.findViewById(R.id.back_button)
        exoContentFrameLayout = binding.playerView.findViewById(R.id.exo_content_frame)
        lockControlsButton = binding.playerView.findViewById(R.id.btn_lock_controls)
        playbackSpeedButton = binding.playerView.findViewById(R.id.btn_playback_speed)
        playerLockControls = binding.playerView.findViewById(R.id.player_lock_controls)
        playerUnlockControls = binding.playerView.findViewById(R.id.player_unlock_controls)
        playerCenterControls = binding.playerView.findViewById(R.id.player_center_controls)
        screenRotateButton = binding.playerView.findViewById(R.id.screen_rotate)
        pipButton = binding.playerView.findViewById(R.id.btn_pip)
        seekBar = binding.playerView.findViewById(R.id.exo_progress)
        subtitleTrackButton = binding.playerView.findViewById(R.id.btn_subtitle_track)
        unlockControlsButton = binding.playerView.findViewById(R.id.btn_unlock_controls)
        videoTitleTextView = binding.playerView.findViewById(R.id.video_name)
        videoZoomButton = binding.playerView.findViewById(R.id.btn_video_zoom)
        playInBackgroundButton = binding.playerView.findViewById(R.id.btn_background)
        loopModeButton = binding.playerView.findViewById(R.id.btn_loop_mode)
        extraControls = binding.playerView.findViewById(R.id.extra_controls)

        // 根据用户偏好设置控制按钮位置
        if (playerPreferences.controlButtonsPosition == ControlButtonsPosition.RIGHT) {
            extraControls.gravity = Gravity.END
            // 控制按钮靠右显示
        }

        // 如果设备不支持画中画，隐藏画中画按钮
        if (!isPipSupported) {
            pipButton.visibility = View.GONE
        }

        // 注册进度条拖动监听器：按下时暂停播放，拖动时显示时间信息，松开后恢复播放
        seekBar.addListener(
            object : TimeBar.OnScrubListener {
                // 进度条拖动开始时触发
                override fun onScrubStart(timeBar: TimeBar, position: Long) {
                    // 确保 MediaController 不为空
                    mediaController?.run {
                        // 记录拖动开始时的播放状态
                        if (isPlaying) {
                            isPlayingOnScrubStart = true
                            // 暂停播放
                            pause()
                        }
                        // 标记已渲染帧
                        isFrameRendered = true
                        // 记录拖动开始位置
                        scrubStartPosition = currentPosition
                        previousScrubPosition = currentPosition
                        // 跳转到指定位置
                        scrub(position)
                        // 显示播放信息
                        showPlayerInfo(
                            info = Utils.formatDurationMillis(position),
                            subInfo = "[${Utils.formatDurationMillisSign(position - scrubStartPosition)}]",
                        )
                    }
                }


                // 进度条拖动中触发
                override fun onScrubMove(timeBar: TimeBar, position: Long) {
                    // 跳转到指定位置
                    scrub(position)
                    // 显示播放信息
                    showPlayerInfo(
                        info = Utils.formatDurationMillis(position),
                        subInfo = "[${Utils.formatDurationMillisSign(position - scrubStartPosition)}]",
                    )
                }

                // 进度条拖动结束时触发
                override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                    // 隐藏播放信息
                    hidePlayerInfo(0L)
                    // 重置拖动开始位置
                    scrubStartPosition = -1L
                    // 如果拖动开始时正在播放，恢复播放
                    if (isPlayingOnScrubStart) {
                        mediaController?.play()
                    }
                }
            },
        )

        // 初始化音量和亮度管理器
        volumeManager =
            VolumeManager(audioManager = getSystemService(AUDIO_SERVICE) as AudioManager)
        brightnessManager = BrightnessManager(activity = this)
        // 初始化手势帮助类：双击/长按/滑动等全部交给 PlayerGestureHelper 处理
        playerGestureHelper = PlayerGestureHelper(
            viewModel = viewModel,
            activity = this,
            volumeManager = volumeManager,
            brightnessManager = brightnessManager,
            onScaleChanged = { scale ->
                // 当缩放变化时，保存缩放比例
                mediaController?.currentMediaItem?.mediaId?.let {
                    viewModel.updateMediumZoom(uri = it, zoom = scale)
                }
            },
        )

        // 初始化 PlayerApi，用于读取外部传入的播放参数（title/position/subs）
        playerApi = PlayerApi(this)

        // 自定义系统返回键逻辑：
        // - TV 盒子且控制条可见时，先隐藏控制条
        // - 否则结束 Activity 并通知 Service 停止播放会话
        onBackPressedDispatcher.addCallback {
            // 当按下返回键时触发
            if (binding.playerView.isControllerFullyVisible && mediaController?.isPlaying == true && isDeviceTvBox()) {
                // 如果是 TV 盒子，且控制条可见，先隐藏控制条
                binding.playerView.hideController()
            } else {
                // 否则结束 Activity 并停止播放会话
                finishAndStopPlayerSession()
            }
        }
    }

    /**
     * Activity 显示到前台：
     * - 恢复自定义亮度（若开启记忆亮度）
     * - 初始化 MediaController 并关联到 PlayerView
     * - 应用屏幕方向、缩放模式、循环模式、音量增强等偏好
     * - 启动实际播放逻辑
     */
    override fun onStart() {
        // 重写 onStart 方法，Activity 显示到前台时调用
        super.onStart()
        // 如果用户开启“记忆播放器亮度”，则恢复上一次的亮度值
        if (playerPreferences.rememberPlayerBrightness) {
            brightnessManager.setBrightness(playerPreferences.playerBrightness)
        }

        // 在协程中执行初始化操作，避免阻塞主线程
        lifecycleScope.launch {
            // 延迟初始化 MediaController（首次进入或 Service 尚未连接）
            maybeInitControllerFuture()
            // 等待 MediaController 初始化完成
            mediaController = controllerFuture?.await()

            // 设置屏幕方向
            setOrientation()
            // 应用视频缩放模式
            applyVideoZoom(videoZoom = playerPreferences.playerVideoZoom)
            // 应用保存的视频缩放比例
            mediaController?.currentMediaItem?.mediaId?.let {
                applyVideoScale(videoScale = viewModel.getVideoState(it)?.videoScale ?: 1f)
            }

            // 配置 PlayerView、系统栏、循环模式等，并启动播放
            mediaController?.run {
                // 将 MediaController 关联到 PlayerView
                binding.playerView.player = this
                // 标记媒体项是否已准备完成
                isMediaItemReady = currentMediaItem != null
                // 切换系统栏显示状态
                toggleSystemBars(showBars = binding.playerView.isControllerFullyVisible)
                // 更新视频标题
                videoTitleTextView.text = currentMediaItem?.mediaMetadata?.title
                // 应用循环模式
                applyLoopMode(playerPreferences.loopMode)
                // 如果启用音量增强，创建 LoudnessEnhancer
                if (playerPreferences.shouldUseVolumeBoost) {
                    try {
                        volumeManager.loudnessEnhancer = LoudnessEnhancer(getAudioSessionId())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                // 更新屏幕常亮标志
                updateKeepScreenOnFlag()
                // 添加播放状态监听器
                addListener(playbackStateListener)
                // 启动实际播放逻辑
                startPlayback()
            }
            // 重置字幕文件选择器状态
            subtitleFileLauncherLaunchedForMediaItem = null
        }
        // 初始化 PlayerView
        initializePlayerView()
    }

    /**
     * Activity 退出前台：
     * - 解绑 PlayerView 与 MediaController
     * - 保存播放状态（playWhenReady / skipSilence）
     * - 根据“后台播放”与 PIP 状态决定是否停止播放会话
     * - 释放 MediaController 资源
     */
    override fun onStop() {
        // 重写 onStop 方法，Activity 退出前台时调用
        // 解绑 PlayerView 与 MediaController
        binding.playerView.player = null
        // 隐藏音量和亮度手势提示
        binding.volumeGestureLayout.visibility = View.GONE
        binding.brightnessGestureLayout.visibility = View.GONE
        // 保存当前屏幕方向
        currentOrientation = requestedOrientation
        // 保存播放状态
        mediaController?.run {
            // 保存播放状态
            viewModel.playWhenReady = playWhenReady
            // 异步保存跳过静音状态
            lifecycleScope.launch {
                viewModel.skipSilenceEnabled = getSkipSilenceEnabled()
            }
            // 移除播放状态监听器
            removeListener(playbackStateListener)
        }
        // 判断是否应该后台播放
        val shouldPlayInBackground = playInBackground || playerPreferences.autoBackgroundPlay
        // 如果正在选择本地字幕或不应该后台播放，则暂停播放
        if (subtitleFileLauncherLaunchedForMediaItem != null || !shouldPlayInBackground) {
            mediaController?.pause()
        }

        // 如果处于画中画模式
        if (isPipActive) {
            // 结束 Activity
            finish()
            // 如果不应该后台播放，停止播放会话
            if (!shouldPlayInBackground) {
                mediaController?.stopPlayerSession()
            }
        }

        // 释放 MediaController 资源
        controllerFuture?.run {
            MediaController.releaseFuture(this)
            controllerFuture = null
        }
        // 调用父类的 onStop 方法
        super.onStop()
    }

    /**
     * 懒加载 MediaController：
     * 仅在 controllerFuture 为空时创建，与 PlayerService 建立连接
     */
    private fun maybeInitControllerFuture() {
        // 如果 controllerFuture 为空，创建新的 MediaController
        if (controllerFuture == null) {
            // 创建 SessionToken，用于连接 PlayerService
            val sessionToken = SessionToken(
                applicationContext,
                ComponentName(applicationContext, PlayerService::class.java)
            )
            // 异步构建 MediaController
            controllerFuture =
                MediaController.Builder(applicationContext, sessionToken).buildAsync()
        }
    }

    @SuppressLint("NewApi", "MissingSuperCall")
    /**
     * 用户离开当前 Activity（Home 键等）时回调：
     * 在 Android 8.0~12 且满足条件时自动进入画中画模式
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT in Build.VERSION_CODES.O..<Build.VERSION_CODES.S &&
            isPipSupported &&
            playerPreferences.autoPip &&
            mediaController?.isPlaying == true &&
            !isControlsLocked
        ) {
            try {
                this.enterPictureInPictureMode(updatePictureInPictureParams())
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    /**
     * 画中画模式切换回调：
     * - 进入 PIP：缩小字幕字号、隐藏解锁控件、注册广播控制播放
     * - 退出 PIP：恢复字幕字号和控件可见性、注销广播
     */
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isPipActive = isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            binding.playerView.subtitleView?.setFractionalTextSize(SubtitleView.DEFAULT_TEXT_SIZE_FRACTION)
            playerUnlockControls.visibility = View.INVISIBLE
            pipBroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent == null || intent.action != PIP_INTENT_ACTION) return
                    when (intent.getIntExtra(PIP_INTENT_ACTION_CODE, 0)) {
                        PIP_ACTION_PLAY -> mediaController?.play()
                        PIP_ACTION_PAUSE -> mediaController?.pause()
                        PIP_ACTION_NEXT -> mediaController?.seekToNext()
                        PIP_ACTION_PREVIOUS -> mediaController?.seekToPrevious()
                    }
                    if (isInPictureInPictureMode && !isFinishing && !isDestroyed) {
                        updatePictureInPictureParams()
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    pipBroadcastReceiver,
                    IntentFilter(PIP_INTENT_ACTION),
                    RECEIVER_NOT_EXPORTED
                )
            } else {
                registerReceiver(pipBroadcastReceiver, IntentFilter(PIP_INTENT_ACTION))
            }
        } else {
            binding.playerView.subtitleView?.setFixedTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                playerPreferences.subtitleTextSize.toFloat()
            )
            if (!isControlsLocked) {
                playerUnlockControls.visibility = View.VISIBLE
            }
            pipBroadcastReceiver?.let {
                unregisterReceiver(it)
                pipBroadcastReceiver = null
            }
        }
    }

    /**
     * 更新画中画参数：
     * - 计算视频实际宽高比和显示区域裁剪矩形
     * - 配置 PIP 操作按钮（上一首/播放暂停/下一首）
     * - 在 Android 12+ 上支持自动进入 PIP 和无缝缩放
     * 更新画中画参数配置
     * 该函数负责配置画中画模式的各种参数，包括视频宽高比、显示区域裁剪、控制按钮等
     *
     * @param enableAutoEnter 是否启用自动进入画中画模式，默认值为当前播放状态
     * @return 配置好的 PictureInPictureParams 对象
     */
    @RequiresApi(Build.VERSION_CODES.O)  // 该功能需要 API 26+ 支持
    private fun updatePictureInPictureParams(enableAutoEnter: Boolean = mediaController?.isPlaying == true): PictureInPictureParams {
        // 获取播放器视图的当前宽度和高度
        val playerViewWidth = binding.playerView.width
        val playerViewHeight = binding.playerView.height

        // 验证播放器视图尺寸是否有效
        if (playerViewWidth <= 0 || playerViewHeight <= 0) {
            // 如果尺寸无效，记录警告日志并返回默认构建的参数
            Timber.w("Invalid playerView dimensions: $playerViewWidth x $playerViewHeight")
            return PictureInPictureParams.Builder().build()
        }

        // 根据播放器视图的宽高计算显示比例
        val displayAspectRatio = Rational(playerViewWidth, playerViewHeight)

        // 使用 PictureInPictureParams.Builder 构建画中画参数
        return PictureInPictureParams.Builder().apply {
            // 计算当前视频的实际宽高比
            val aspectRatio = calculateVideoAspectRatio()
            if (aspectRatio != null) {
                // 计算视频在画中画窗口中的裁剪区域，以减少黑边并尽量填满窗口
                val sourceRectHint = calculateSourceRectHint(displayAspectRatio, aspectRatio)
                // 设置画中画窗口的宽高比
                setAspectRatio(aspectRatio)
                // 设置源矩形提示，用于优化视频在画中画窗口中的显示效果
                setSourceRectHint(sourceRectHint)
            }

            // Android 12 (S) 及以上版本支持无缝调整大小和自动进入画中画功能
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // 设置是否启用无缝调整大小功能（当用户偏好开启且允许自动进入时）
                setSeamlessResizeEnabled(playerPreferences.autoPip && enableAutoEnter)
                // 设置是否启用自动进入画中画模式（当用户偏好开启且允许自动进入时）
                setAutoEnterEnabled(playerPreferences.autoPip && enableAutoEnter)
            }

            // 设置画中画窗口中的控制按钮
            setActions(
                listOf(
                    // 上一首按钮
                    createPipAction(
                        context = this@PlayerActivity,  // 当前 Activity 上下文
                        title = "skip to previous",     // 按钮标题
                        icon = coreUiR.drawable.ic_skip_prev,  // 按钮图标
                        actionCode = PIP_ACTION_PREVIOUS,       // 操作代码
                    ),
                    // 播放/暂停按钮 - 根据当前播放状态显示相应图标和操作
                    if (mediaController?.isPlaying == true) {
                        // 如果正在播放，显示暂停按钮
                        createPipAction(
                            context = this@PlayerActivity,
                            title = "pause",
                            icon = coreUiR.drawable.ic_pause,
                            actionCode = PIP_ACTION_PAUSE,
                        )
                    } else {
                        // 如果暂停状态，显示播放按钮
                        createPipAction(
                            context = this@PlayerActivity,
                            title = "play",
                            icon = coreUiR.drawable.ic_play,
                            actionCode = PIP_ACTION_PLAY,
                        )
                    },
                    // 下一首按钮
                    createPipAction(
                        context = this@PlayerActivity,
                        title = "skip to next",
                        icon = coreUiR.drawable.ic_skip_next,
                        actionCode = PIP_ACTION_NEXT,
                    ),
                ),
            )
        }.build().also { params ->
            // 构建完成后，尝试设置画中画参数
            try {
                // 确保 Activity 未结束且未销毁时才设置参数
                if (!isFinishing && !isDestroyed) {
                    setPictureInPictureParams(params)
                }
            } catch (e: IllegalStateException) {
                // 捕获并记录设置参数失败的异常
                Timber.e(e, "Failed to set picture-in-picture params")
            }
        }
    }


    /**
     * 计算当前视频的宽高比（过滤掉异常比例）
     */
    private fun calculateVideoAspectRatio(): Rational? {
        return binding.playerView.player?.videoSize?.let { videoSize ->
            if (videoSize.width == 0 || videoSize.height == 0) return@let null

            Rational(
                videoSize.width,
                videoSize.height,
            ).takeIf { it.toFloat() in 0.5f..2.39f }
        }
    }

    /**
     * 根据显示区域比例和视频比例计算 PIP 裁剪区域
     * 目的是减少黑边、尽量填满画中画窗口
     */
    private fun calculateSourceRectHint(displayAspectRatio: Rational, aspectRatio: Rational): Rect {
        val playerWidth = binding.playerView.width.toFloat()
        val playerHeight = binding.playerView.height.toFloat()

        return if (displayAspectRatio < aspectRatio) {
            val space = ((playerHeight - (playerWidth / aspectRatio.toFloat())) / 2).toInt()
            Rect(
                0,
                space,
                playerWidth.toInt(),
                (playerWidth / aspectRatio.toFloat()).toInt() + space
            )
        } else {
            val space = ((playerWidth - (playerHeight * aspectRatio.toFloat())) / 2).toInt()
            Rect(
                space,
                0,
                (playerHeight * aspectRatio.toFloat()).toInt() + space,
                playerHeight.toInt()
            )
        }
    }

    /**
     * 根据用户偏好和当前视频尺寸设置屏幕方向：
     * - 配合自定义的 ScreenOrientation 枚举
     * - 支持自动/横屏/竖屏/跟随视频方向等模式
     */
    private fun setOrientation() {
        requestedOrientation =
            currentOrientation ?: playerPreferences.playerScreenOrientation.toActivityOrientation(
                videoOrientation = mediaController?.videoSize?.let { videoSize ->
                    when {
                        videoSize.width == 0 || videoSize.height == 0 -> null
                        videoSize.isPortrait -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                        else -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    }
                },
            )
    }

    /**
     * 初始化 PlayerView：
     * - 配置缓冲指示、控制条超时、控制条可见性回调（联动系统栏）
     * - 应用字幕样式（系统样式或自定义样式）
     * - 绑定音轨/字幕轨道/播放速度/锁定控制/缩放/PIP/后台播放/返回按钮等点击事件
     */
    private fun initializePlayerView() {
        binding.playerView.apply {
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            controllerShowTimeoutMs = playerPreferences.controllerAutoHideTimeout.toMillis
            setControllerVisibilityListener(
                PlayerView.ControllerVisibilityListener { visibility ->
                    toggleSystemBars(showBars = visibility == View.VISIBLE && !isControlsLocked)
                },
            )

            subtitleView?.apply {
                val captioningManager = getSystemService(CAPTIONING_SERVICE) as CaptioningManager
                if (playerPreferences.useSystemCaptionStyle) {
                    val systemCaptionStyle =
                        CaptionStyleCompat.createFromCaptionStyle(captioningManager.userStyle)
                    setStyle(systemCaptionStyle)
                } else {
                    val userStyle = CaptionStyleCompat(
                        Color.WHITE,
                        Color.BLACK.takeIf { playerPreferences.subtitleBackground }
                            ?: Color.TRANSPARENT,
                        Color.TRANSPARENT,
                        CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
                        Color.BLACK,
                        Typeface.create(
                            playerPreferences.subtitleFont.toTypeface(),
                            Typeface.BOLD.takeIf { playerPreferences.subtitleTextBold }
                                ?: Typeface.NORMAL,
                        ),
                    )
                    setStyle(userStyle)
                    setFixedTextSize(
                        TypedValue.COMPLEX_UNIT_SP,
                        playerPreferences.subtitleTextSize.toFloat()
                    )
                }
                setApplyEmbeddedStyles(playerPreferences.applyEmbeddedStyles)
            }
        }

        audioTrackButton.setOnClickListener {
            TrackSelectionDialogFragment(
                type = C.TRACK_TYPE_AUDIO,
                tracks = mediaController?.currentTracks ?: return@setOnClickListener,
                onTrackSelected = { mediaController?.switchAudioTrack(it) },
            ).show(supportFragmentManager, "TrackSelectionDialog")
        }

        subtitleTrackButton.setOnClickListener {
            TrackSelectionDialogFragment(
                type = C.TRACK_TYPE_TEXT,
                tracks = mediaController?.currentTracks ?: return@setOnClickListener,
                onTrackSelected = { mediaController?.switchSubtitleTrack(it) },
                onOpenLocalTrackClicked = {
                    subtitleFileLauncherLaunchedForMediaItem = mediaController?.currentMediaItem
                    subtitleFileLauncher.launch(
                        arrayOf(
                            MimeTypes.APPLICATION_SUBRIP,
                            MimeTypes.APPLICATION_TTML,
                            MimeTypes.TEXT_VTT,
                            MimeTypes.TEXT_SSA,
                            MimeTypes.BASE_TYPE_APPLICATION + "/octet-stream",
                            MimeTypes.BASE_TYPE_TEXT + "/*",
                        ),
                    )
                },
            ).show(supportFragmentManager, "TrackSelectionDialog")
        }

        playbackSpeedButton.setOnClickListener {
            PlaybackSpeedControlsDialogFragment(
                mediaController = mediaController ?: return@setOnClickListener,
            ).show(supportFragmentManager, "PlaybackSpeedSelectionDialog")
        }

        lockControlsButton.setOnClickListener {
            playerUnlockControls.visibility = View.INVISIBLE
            playerLockControls.visibility = View.VISIBLE
            isControlsLocked = true
            toggleSystemBars(showBars = false)
        }
        unlockControlsButton.setOnClickListener {
            playerLockControls.visibility = View.INVISIBLE
            playerUnlockControls.visibility = View.VISIBLE
            isControlsLocked = false
            binding.playerView.showController()
            toggleSystemBars(showBars = true)
        }
        videoZoomButton.setOnClickListener {
            val videoZoom = playerPreferences.playerVideoZoom.next()
            changeAndSaveVideoZoom(videoZoom = videoZoom)
        }

        videoZoomButton.setOnLongClickListener {
            VideoZoomOptionsDialogFragment(
                currentVideoZoom = playerPreferences.playerVideoZoom,
                onVideoZoomOptionSelected = { changeAndSaveVideoZoom(videoZoom = it) },
            ).show(supportFragmentManager, "VideoZoomOptionsDialog")
            true
        }
        screenRotateButton.setOnClickListener {
            requestedOrientation = when (resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                else -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
        }
        pipButton.setOnClickListener {
            if (isPipSupported && !isPipEnabled) {
                Toast.makeText(this, coreUiR.string.enable_pip_from_settings, Toast.LENGTH_SHORT)
                    .show()
                try {
                    Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS").apply {
                        data = "package:$packageName".toUri()
                        startActivity(this@apply)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isPipSupported) {
                this.enterPictureInPictureMode(updatePictureInPictureParams())
            }
        }
        playInBackgroundButton.setOnClickListener {
            playInBackground = true
            finish()
        }
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        updateLoopModeIcon(playerPreferences.loopMode)
        loopModeButton.setOnClickListener {
            val currentLoopMode = playerPreferences.loopMode
            val nextLoopMode = when (currentLoopMode) {
                LoopMode.OFF -> LoopMode.ONE
                LoopMode.ONE -> LoopMode.ALL
                LoopMode.ALL -> LoopMode.OFF
            }

            viewModel.setLoopMode(nextLoopMode)
            updateLoopModeIcon(nextLoopMode)
            applyLoopMode(nextLoopMode)
            showPlayerInfo(
                info = when (nextLoopMode) {
                    LoopMode.OFF -> getString(coreUiR.string.loop_mode_off)
                    LoopMode.ONE -> getString(coreUiR.string.loop_mode_one)
                    LoopMode.ALL -> getString(coreUiR.string.loop_mode_all)
                },
            )
        }
    }

    private fun updateLoopModeIcon(loopMode: LoopMode) {
        val iconResId = when (loopMode) {
            LoopMode.OFF -> coreUiR.drawable.ic_loop_off
            LoopMode.ONE -> coreUiR.drawable.ic_loop_one
            LoopMode.ALL -> coreUiR.drawable.ic_loop_all
        }
        loopModeButton.setImageResource(iconResId)
    }

    private fun applyLoopMode(loopMode: LoopMode) {
        mediaController?.repeatMode = when (loopMode) {
            LoopMode.OFF -> Player.REPEAT_MODE_OFF
            LoopMode.ONE -> Player.REPEAT_MODE_ONE
            LoopMode.ALL -> Player.REPEAT_MODE_ALL
        }
    }

    /**
     * 启动/恢复播放：
     * - 若从后台返回且播放列表已存在，则只恢复 playWhenReady
     * - 否则重新构建播放列表并从指定 URI 开始播放
     */
    private fun startPlayback() {
        val uri = intent.data ?: return

        val returningFromBackground = !isIntentNew && mediaController?.currentMediaItem != null
        val isNewUriTheCurrentMediaItem =
            mediaController?.currentMediaItem?.localConfiguration?.uri.toString() == uri.toString()

        if (returningFromBackground || isNewUriTheCurrentMediaItem) {
            mediaController?.prepare()
            mediaController?.playWhenReady = viewModel.playWhenReady
            return
        }

        isIntentNew = false

        lifecycleScope.launch {
            playVideo(uri)
        }
    }

    /**
     * 根据传入的 URI 构建播放列表并启动播放：
     * - 通过扩展 `getMediaContentUri` 解析真实媒体 URI
     * - 调用 UseCase 获取排序后的播放列表
     * - 将当前 URI 插入到列表头部（若不存在）
     * - 设置 MediaItem 列表并应用外部 API 传入的标题与字幕
     */
    private suspend fun playVideo(uri: Uri) = withContext(Dispatchers.Default) {
        // 解析传入的 URI，获取真实的媒体内容 URI
        val mediaContentUri = getMediaContentUri(uri)

        // 根据媒体内容 URI 获取播放列表
        val playlist = mediaContentUri?.let { mediaUri ->
            // 通过 ViewModel 获取基于当前媒体 URI 的播放列表
            viewModel.getPlaylistFromUri(mediaUri)
                .map { it.uriString }  // 将播放列表项转换为 URI 字符串列表
                .toMutableList()      // 转换为可变列表以便后续操作
                .apply {
                    // 检查当前媒体 URI 是否已经在播放列表中
                    if (!contains(mediaUri.toString())) {
                        // 如果不在列表中，则将其添加到列表开头作为第一个播放项
                        add(index = 0, element = mediaUri.toString())
                    }
                }
        } ?: listOf(uri.toString())  // 如果无法解析媒体内容 URI，则使用原始 URI 作为单一元素列表

        // 查找当前要播放的媒体项在播放列表中的索引位置
        val mediaItemIndexToPlay = playlist.indexOfFirst {
            // 匹配当前要播放的 URI（优先使用解析后的媒体内容 URI，否则使用原始 URI）
            it == (mediaContentUri ?: uri).toString()
        }.takeIf { it >= 0 } ?: 0  // 如果找到了匹配项则返回索引，否则默认为 0

        // 构建媒体项列表，为每个 URI 创建相应的 MediaItem 对象
        val mediaItems = playlist.mapIndexed { index, uri ->
            MediaItem.Builder().apply {
                // 设置媒体项的 URI
                setUri(uri)
                // 设置媒体项的唯一标识符（使用 URI 作为 ID）
                setMediaId(uri)

                // 如果当前索引是要播放的媒体项索引，则应用额外的元数据和字幕配置
                if (index == mediaItemIndexToPlay) {
                    // 设置媒体元数据（主要是标题）
                    setMediaMetadata(
                        MediaMetadata.Builder().apply {
                            setTitle(playerApi.title)  // 使用外部 API 提供的标题
                        }.build(),
                    )

                    // 将外部 API 提供的字幕信息转换为 MediaItem 的字幕配置
                    val apiSubs = playerApi.getSubs().map { subtitle ->
                        uriToSubtitleConfiguration(
                            uri = subtitle.uri,  // 字幕 URI
                            subtitleEncoding = playerPreferences.subtitleTextEncoding,  // 字幕编码
                            isSelected = subtitle.isSelected,  // 是否默认选中
                        )
                    }
                    // 设置字幕配置
                    setSubtitleConfigurations(apiSubs)
                }
            }.build()  // 构建最终的 MediaItem 对象
        }

        // 切换到主线程执行播放器相关操作
        withContext(Dispatchers.Main) {
            mediaController?.run {
                // 设置播放列表项，指定要播放的起始项索引和初始播放位置
                setMediaItems(
                    mediaItems,                    // 媒体项列表
                    mediaItemIndexToPlay,          // 起始播放项索引
                    playerApi.position?.toLong() ?: C.TIME_UNSET  // 初始播放位置，如果没有则使用默认值
                )
                // 恢复之前的播放状态（是否准备好后自动播放）
                playWhenReady = viewModel.playWhenReady
                // 准备播放器开始播放
                prepare()
            }
        }
    }


    /**
     * 创建播放状态监听器：
     * - 媒体切换时更新 Intent.data，便于恢复播放
     * - 元数据变化时更新标题文本
     * - 播放/暂停变化时控制常亮和 PIP 参数
     * - 视频尺寸变化时更新 PIP 裁剪和屏幕方向、缩放
     * - 出错时弹出错误对话框并提供“退出/播放下一个”选项
     * - 结束或自动下一首时根据循环模式决定是否结束会话
     */
    private fun playbackStateListener() = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            intent.data = mediaItem?.localConfiguration?.uri
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            videoTitleTextView.text = mediaMetadata.title
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            updateKeepScreenOnFlag()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isPipSupported) {
                updatePictureInPictureParams()
            }
        }

        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            super.onAudioSessionIdChanged(audioSessionId)
            volumeManager.loudnessEnhancer?.release()

            if (playerPreferences.shouldUseVolumeBoost) {
                try {
                    volumeManager.loudnessEnhancer = LoudnessEnhancer(audioSessionId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
            if (videoSize.width != 0 && videoSize.height != 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isPipSupported) {
                    updatePictureInPictureParams()
                }
                setOrientation()
            }
            lifecycleScope.launch {
                val videoScale =
                    mediaController?.currentMediaItem?.mediaId?.let { viewModel.getVideoState(it)?.videoScale }
                        ?: 1f
                applyVideoZoom(videoZoom = playerPreferences.playerVideoZoom)
                applyVideoScale(videoScale = videoScale)
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Timber.e(error)
            val alertDialog = MaterialAlertDialogBuilder(this@PlayerActivity).apply {
                setTitle(getString(coreUiR.string.error_playing_video))
                setMessage(error.message ?: getString(coreUiR.string.unknown_error))
                setNegativeButton(getString(coreUiR.string.exit)) { _, _ ->
                    finish()
                }
                if (mediaController?.hasNextMediaItem() == true) {
                    setPositiveButton(getString(coreUiR.string.play_next_video)) { dialog, _ ->
                        dialog.dismiss()
                        mediaController?.seekToNext()
                    }
                }
            }.create()

            alertDialog.show()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_ENDED -> {
                    isPlaybackFinished = mediaController?.playbackState == Player.STATE_ENDED
                    finishAndStopPlayerSession()
                }

                Player.STATE_READY -> {
                    binding.playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                    isMediaItemReady = true
                    isFrameRendered = true
                }

                else -> {}
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)

            if (reason == Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM) {
                if (mediaController?.repeatMode != Player.REPEAT_MODE_OFF) return
                isPlaybackFinished = true
                finishAndStopPlayerSession()
            }
        }
    }

    /**
     * 结束 Activity：
     * - 若是通过外部 API 启动并要求返回结果，则封装播放结果（时长/进度/结束原因）
     * - 最后调用系统的 finish()
     */
    override fun finish() {
        if (playerApi.shouldReturnResult) {
            val result = playerApi.getResult(
                isPlaybackFinished = isPlaybackFinished,
                duration = mediaController?.duration ?: C.TIME_UNSET,
                position = mediaController?.currentPosition ?: C.TIME_UNSET,
            )
            setResult(RESULT_OK, result)
        }
        super.finish()
    }

    /**
     * 处理新的播放 Intent：
     * - 更新当前 Intent 并重置方向
     * - 若 MediaController 已就绪，则立即调用 startPlayback() 以播放新 URI
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.data != null) {
            currentOrientation = null
            setIntent(intent)
            isIntentNew = true
            if (mediaController != null) {
                startPlayback()
            }
        }
    }

    /**
     * 处理遥控器/键盘按下事件：
     * - 方向键/音量键：调节音量并显示音量手势 UI
     * - 媒体播放键：播放/暂停
     * - DPAD 左右 / 快进快退键：以 10 秒为步长快进/后退，并显示时间信息
     * - 回车/确认键：显示控制条
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            // 处理音量增加键和上方向键
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_DPAD_UP,
                -> {
                // 当控制条不可见或按的是音量键时才处理
                if (!binding.playerView.isControllerFullyVisible || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    // 增加系统音量
                    volumeManager.increaseVolume(playerPreferences.showSystemVolumePanel)
                    // 显示音量手势提示界面
                    showVolumeGestureLayout()
                    return true  // 消费此事件
                }
            }

            // 处理音量减小键和下方向键
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_DPAD_DOWN,
                -> {
                // 当控制条不可见或按的是音量键时才处理
                if (!binding.playerView.isControllerFullyVisible || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    // 减少系统音量
                    volumeManager.decreaseVolume(playerPreferences.showSystemVolumePanel)
                    // 显示音量手势提示界面
                    showVolumeGestureLayout()
                    return true  // 消费此事件
                }
            }

            // 处理媒体播放/暂停相关按键
            KeyEvent.KEYCODE_MEDIA_PLAY,      // 媒体播放键
            KeyEvent.KEYCODE_MEDIA_PAUSE,     // 媒体暂停键
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, // 媒体播放/暂停键
            KeyEvent.KEYCODE_BUTTON_SELECT,   // 选择键（如遥控器上的确定键）
                -> {
                // 根据不同按键类型执行相应的播放/暂停操作
                when {
                    // 如果按的是暂停键，则暂停播放
                    keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE -> mediaController?.pause()
                    // 如果按的是播放键，则开始播放
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY -> mediaController?.play()
                    // 如果当前正在播放，则暂停播放
                    mediaController?.isPlaying == true -> mediaController?.pause()
                    // 其他情况（如当前暂停），则开始播放
                    else -> mediaController?.play()
                }
                return true  // 消费此事件
            }

            // 处理游戏手柄开始键、A键和空格键
            KeyEvent.KEYCODE_BUTTON_START,    // 游戏手柄开始键
            KeyEvent.KEYCODE_BUTTON_A,        // 游戏手柄 A 键
            KeyEvent.KEYCODE_SPACE,           // 空格键
                -> {
                // 仅在控制条不可见时才处理
                if (!binding.playerView.isControllerFullyVisible) {
                    // 切换播放/暂停状态
                    binding.playerView.togglePlayPause()
                    return true  // 消费此事件
                }
            }

            // 处理向左快退相关按键
            KeyEvent.KEYCODE_DPAD_LEFT,       // 左方向键
            KeyEvent.KEYCODE_BUTTON_L2,       // 游戏手柄 L2 键
            KeyEvent.KEYCODE_MEDIA_REWIND,    // 媒体重绕键
                -> {
                // 仅在控制条不可见或按的是重绕键时才处理
                if (!binding.playerView.isControllerFullyVisible || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                    mediaController?.run {
                        // 如果还没有记录拖动开始位置，则记录当前位置
                        if (scrubStartPosition == -1L) {
                            scrubStartPosition = currentPosition
                        }
                        // 计算向后跳转的位置（当前位置减去10秒），确保不小于0
                        val position = (currentPosition - 10_000).coerceAtLeast(0L)
                        // 执行向后跳转操作，根据偏好决定是否快速对齐关键帧
                        seekBack(position, shouldFastSeek)
                        // 显示跳转信息（当前时间及相对于拖动开始位置的偏移）
                        showPlayerInfo(
                            info = Utils.formatDurationMillis(position),
                            subInfo = "[${Utils.formatDurationMillisSign(position - scrubStartPosition)}]",
                        )
                        return true  // 消费此事件
                    }
                }
            }

            // 处理向右快进相关按键
            KeyEvent.KEYCODE_DPAD_RIGHT,      // 右方向键
            KeyEvent.KEYCODE_BUTTON_R2,       // 游戏手柄 R2 键
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD, // 媒体快进键
                -> {
                // 仅在控制条不可见或按的是快进键时才处理
                if (!binding.playerView.isControllerFullyVisible || keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
                    mediaController?.run {
                        // 如果还没有记录拖动开始位置，则记录当前位置
                        if (scrubStartPosition == -1L) {
                            scrubStartPosition = currentPosition
                        }
                        // 计算向前跳转的位置（当前位置加上10秒），确保不超过总时长
                        val position = (currentPosition + 10_000).coerceAtMost(duration)
                        // 执行向前跳转操作，根据偏好决定是否快速对齐关键帧
                        seekForward(position, shouldFastSeek)
                        // 显示跳转信息（当前时间及相对于拖动开始位置的偏移）
                        showPlayerInfo(
                            info = Utils.formatDurationMillis(position),
                            subInfo = "[${Utils.formatDurationMillisSign(position - scrubStartPosition)}]",
                        )
                        return true  // 消费此事件
                    }
                }
            }

            // 处理显示控制条的相关按键
            KeyEvent.KEYCODE_ENTER,           // 回车键
            KeyEvent.KEYCODE_DPAD_CENTER,     // 中心方向键（如十字键中心）
            KeyEvent.KEYCODE_NUMPAD_ENTER,    // 数字键盘回车键
                -> {
                // 仅在控制条不可见时才处理
                if (!binding.playerView.isControllerFullyVisible) {
                    // 显示播放器控制条
                    binding.playerView.showController()
                    return true  // 消费此事件
                }
            }

        }
        // 如果没有匹配的按键，调用父类的处理方法
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 处理遥控器/键盘抬起事件：
     * - 抬起音量/方向键时隐藏音量 UI
     * - 抬起左右键/快进快退键时隐藏时间信息
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            // 处理音量键和方向键抬起事件
            KeyEvent.KEYCODE_VOLUME_UP,       // 音量增加键抬起
            KeyEvent.KEYCODE_VOLUME_DOWN,     // 音量减小键抬起
            KeyEvent.KEYCODE_DPAD_UP,         // 上方向键抬起
            KeyEvent.KEYCODE_DPAD_DOWN,       // 下方向键抬起
                -> {
                // 隐藏音量手势提示界面
                hideVolumeGestureLayout()
                return true  // 消费此事件
            }

            // 处理快进/快退相关按键抬起事件
            KeyEvent.KEYCODE_DPAD_LEFT,       // 左方向键抬起
            KeyEvent.KEYCODE_BUTTON_L2,       // 游戏手柄 L2 键抬起
            KeyEvent.KEYCODE_MEDIA_REWIND,    // 媒体重绕键抬起
            KeyEvent.KEYCODE_DPAD_RIGHT,      // 右方向键抬起
            KeyEvent.KEYCODE_BUTTON_R2,       // 游戏手柄 R2 键抬起
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD, // 媒体快进键抬起
                -> {
                // 隐藏播放信息提示界面
                hidePlayerInfo()
                return true  // 消费此事件
            }
        }
        // 如果没有匹配的按键，调用父类的处理方法
        return super.onKeyUp(keyCode, event)
    }


    /**
     * 拖动进度条时的实际 Seek 操作：
     * - 根据前一次位置判断是前进还是后退
     * - 使用快进/后退扩展控制是否对齐关键帧
     */
    private fun scrub(position: Long) {
        if (isFrameRendered) {
            isFrameRendered = false
            if (position > previousScrubPosition) {
                mediaController?.seekForward(position, shouldFastSeek)
            } else {
                mediaController?.seekBack(position, shouldFastSeek)
            }
            previousScrubPosition = position
        }
    }

    /** 显示音量手势指示布局，并更新当前音量百分比 */
    fun showVolumeGestureLayout() {
        hideVolumeIndicatorJob?.cancel()
        with(binding) {
            volumeGestureLayout.visibility = View.VISIBLE
            volumeProgressBar.max = volumeManager.maxVolume.times(100)
            volumeProgressBar.progress = volumeManager.currentVolume.times(100).toInt()
            volumeProgressText.text = volumeManager.volumePercentage.toString()
        }
    }

    /** 显示亮度手势指示布局，并更新当前亮度百分比 */
    fun showBrightnessGestureLayout() {
        hideBrightnessIndicatorJob?.cancel()
        with(binding) {
            brightnessGestureLayout.visibility = View.VISIBLE
            brightnessProgressBar.max = brightnessManager.maxBrightness.times(100).toInt()
            brightnessProgressBar.progress = brightnessManager.currentBrightness.times(100).toInt()
            brightnessProgressText.text = brightnessManager.brightnessPercentage.toString()
        }
    }

    /**
     * 显示中部信息提示（进度/快进快退偏移等）
     * @param info 主信息文本
     * @param subInfo 副信息文本（可选）
     */
    fun showPlayerInfo(info: String, subInfo: String? = null) {
        hideInfoLayoutJob?.cancel()
        with(binding) {
            infoLayout.visibility = View.VISIBLE
            infoText.text = info
            infoSubtext.visibility = View.GONE.takeIf { subInfo == null } ?: View.VISIBLE
            infoSubtext.text = subInfo
        }
    }

    /** 显示顶部信息提示（例如长按加速时的当前倍速） */
    fun showTopInfo(info: String) {
        with(binding) {
            topInfoLayout.visibility = View.VISIBLE
            topInfoText.text = info
        }
    }

    /** 延迟隐藏音量手势布局 */
    fun hideVolumeGestureLayout(delayTimeMillis: Long = HIDE_DELAY_MILLIS) {
        if (binding.volumeGestureLayout.visibility != View.VISIBLE) return
        hideVolumeIndicatorJob = lifecycleScope.launch {
            delay(delayTimeMillis)
            binding.volumeGestureLayout.visibility = View.GONE
        }
    }

    /** 延迟隐藏亮度手势布局，并在需要时保存记忆亮度 */
    fun hideBrightnessGestureLayout(delayTimeMillis: Long = HIDE_DELAY_MILLIS) {
        if (binding.brightnessGestureLayout.visibility != View.VISIBLE) return
        hideBrightnessIndicatorJob = lifecycleScope.launch {
            delay(delayTimeMillis)
            binding.brightnessGestureLayout.visibility = View.GONE
        }
        if (playerPreferences.rememberPlayerBrightness) {
            viewModel.setPlayerBrightness(window.attributes.screenBrightness)
        }
    }

    /** 延迟隐藏中部信息提示布局 */
    fun hidePlayerInfo(delayTimeMillis: Long = HIDE_DELAY_MILLIS) {
        if (binding.infoLayout.visibility != View.VISIBLE) return
        hideInfoLayoutJob = lifecycleScope.launch {
            delay(delayTimeMillis)
            binding.infoLayout.visibility = View.GONE
        }
    }

    /** 隐藏顶部信息提示布局 */
    fun hideTopInfo() {
        binding.topInfoLayout.visibility = View.GONE
    }

    /** 根据播放状态决定是否保持屏幕常亮 */
    private fun updateKeepScreenOnFlag() {
        if (mediaController?.isPlaying == true) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    /** 应用缩放比例到内容容器（用于双指缩放） */
    private fun applyVideoScale(videoScale: Float) {
        exoContentFrameLayout.scaleX = videoScale
        exoContentFrameLayout.scaleY = videoScale
        exoContentFrameLayout.requestLayout()
    }

    /** 重置内容容器宽高和缩放比例为全屏默认状态 */
    private fun resetExoContentFrameWidthAndHeight() {
        exoContentFrameLayout.layoutParams.width = LayoutParams.MATCH_PARENT
        exoContentFrameLayout.layoutParams.height = LayoutParams.MATCH_PARENT
        exoContentFrameLayout.scaleX = 1.0f
        exoContentFrameLayout.scaleY = 1.0f
        exoContentFrameLayout.requestLayout()
    }

    /**
     * 应用预设的视频缩放模式：
     * - BEST_FIT：保持比例，完整显示画面
     * - STRETCH：拉伸填满
     * - CROP：裁剪填满
     * - HUNDRED_PERCENT：按原始像素显示
     */
    private fun applyVideoZoom(videoZoom: VideoZoom) {
        resetExoContentFrameWidthAndHeight()
        when (videoZoom) {
            VideoZoom.BEST_FIT -> {
                binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                videoZoomButton.setImageDrawable(this, coreUiR.drawable.ic_fit_screen)
            }

            VideoZoom.STRETCH -> {
                binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                videoZoomButton.setImageDrawable(this, coreUiR.drawable.ic_aspect_ratio)
            }

            VideoZoom.CROP -> {
                binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                videoZoomButton.setImageDrawable(this, coreUiR.drawable.ic_crop_landscape)
            }

            VideoZoom.HUNDRED_PERCENT -> {
                mediaController?.videoSize?.let {
                    exoContentFrameLayout.layoutParams.width = it.width
                    exoContentFrameLayout.layoutParams.height = it.height
                    exoContentFrameLayout.requestLayout()
                }
                binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                videoZoomButton.setImageDrawable(this, coreUiR.drawable.ic_width_wide)
            }
        }
    }

    /**
     * 更改并保存当前视频缩放模式：
     * - 应用缩放模式到 PlayerView
     * - 将用户选择写入偏好
     * - 显示当前模式名称的提示信息
     */
    private fun changeAndSaveVideoZoom(videoZoom: VideoZoom) {
        applyVideoZoom(videoZoom)
        viewModel.setVideoZoom(videoZoom)

        mediaController?.currentMediaItem?.mediaId?.let {
            viewModel.updateMediumZoom(uri = it, zoom = 1f)
        }

        lifecycleScope.launch {
            binding.infoLayout.visibility = View.VISIBLE
            binding.infoText.text = getString(videoZoom.nameRes())
            delay(HIDE_DELAY_MILLIS)
            binding.infoLayout.visibility = View.GONE
        }
    }

    /**
     * 结束 Activity 并通知 PlayerService 停止当前播放会话
     * 用于播放结束或用户主动退出时的统一清理逻辑
     */
    private fun finishAndStopPlayerSession() {
        finish()
        mediaController?.stopPlayerSession()
    }

    companion object {
        const val HIDE_DELAY_MILLIS = 1000L
        const val PIP_INTENT_ACTION = "pip_action"
        const val PIP_INTENT_ACTION_CODE = "pip_action_code"
        const val PIP_ACTION_PLAY = 1
        const val PIP_ACTION_PAUSE = 2
        const val PIP_ACTION_NEXT = 3
        const val PIP_ACTION_PREVIOUS = 4
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun createPipAction(
    context: Context,
    title: String,
    @DrawableRes icon: Int,
    actionCode: Int,
): RemoteAction {
    return RemoteAction(
        Icon.createWithResource(context, icon),
        title,
        title,
        PendingIntent.getBroadcast(
            context,
            actionCode,
            Intent(PlayerActivity.PIP_INTENT_ACTION).apply {
                putExtra(PlayerActivity.PIP_INTENT_ACTION_CODE, actionCode)
                setPackage(context.packageName)
            },
            PendingIntent.FLAG_IMMUTABLE,
        ),
    )
}
