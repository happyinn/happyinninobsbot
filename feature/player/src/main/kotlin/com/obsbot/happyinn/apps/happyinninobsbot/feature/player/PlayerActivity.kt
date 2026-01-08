package com.obsbot.happyinn.apps.happyinninobsbot.feature.player

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Rational
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import android.view.accessibility.CaptioningManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import androidx.media3.ui.SubtitleView
import androidx.media3.ui.TimeBar
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.util.concurrent.ListenableFuture
import com.obsbot.happyinn.apps.happyinninobsbot.Utils
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.DarkThemeConfig
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.ControlButtonsPosition
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.LoopMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.VideoZoom
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.getMediaContentUri
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.isDeviceTvBox
import dagger.hilt.android.AndroidEntryPoint

import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.databinding.ActivityPlayerBinding
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.dialogs.PlaybackSpeedControlsDialogFragment
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.dialogs.TrackSelectionDialogFragment
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.dialogs.VideoZoomOptionsDialogFragment
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.dialogs.nameRes
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.isPortrait
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.next
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.seekBack
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.seekForward
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.setImageDrawable
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.shouldFastSeek
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.toActivityOrientation
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.toTypeface
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.togglePlayPause
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.toggleSystemBars
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.uriToSubtitleConfiguration
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.PlayerService
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.addSubtitleTrack
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.getAudioSessionId
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.getSkipSilenceEnabled
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.stopPlayerSession
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.switchAudioTrack
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.switchSubtitleTrack
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils.BrightnessManager
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils.PlayerApi
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils.PlayerGestureHelper
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils.VolumeManager
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils.toMillis
import kotlin.apply
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R as coreUiR

@SuppressLint("UnsafeOptInUsageError")
@AndroidEntryPoint
/**
 * 播放器 Activity
 * 负责渲染播放界面、响应手势/按键、控制 MediaController 并处理画中画、亮度音量、字幕/音轨切换等交互。
 * 通过 Hilt 注入 ViewModel 和各类管理器，关联 PlayerService 完成播放。
 */
class PlayerActivity : AppCompatActivity() {

    /** ViewBinding：对应 `activity_player.xml`，用于访问布局中的所有视图 */
    lateinit var binding: ActivityPlayerBinding

    /** 播放器 ViewModel，负责读取和保存播放相关的偏好与状态 */
    private val viewModel: PlayerViewModel by viewModels()
    /** 全局应用偏好（主题、动态色、悬浮按钮等） */
    private val userData get() = viewModel.appPrefs.value
    /** 播放器偏好设置（快进方式、字幕样式、缩放模式等） */
    private val playerPreferences get() = viewModel.playerPrefs.value

    /** 当前此次播放会话是否因自然播放完成（STATE_ENDED）结束 */
    private var isPlaybackFinished = false

    /** 媒体项是否已准备完成（用于防抖一些手势逻辑） */
    var isMediaItemReady = false
    /** 控制是否锁定播放器 UI 控件（锁定时仅显示解锁区域） */
    var isControlsLocked = false
    /** 是否已经渲染出至少一帧视频画面（用于拖动进度条时的防抖） */
    private var isFrameRendered = false
    /** 拖动进度开始时，是否处于播放状态 */
    private var isPlayingOnScrubStart: Boolean = false
    /** 上一次拖动进度条时的位置，用于判断快进/后退方向 */
    private var previousScrubPosition = 0L
    /** 本次拖动开始时的播放位置，用于显示相对偏移 */
    private var scrubStartPosition: Long = -1L
    /** 记录当前屏幕方向，方便退出时恢复 */
    private var currentOrientation: Int? = null
    /** 延迟隐藏音量手势提示的协程任务 */
    private var hideVolumeIndicatorJob: Job? = null
    /** 延迟隐藏亮度手势提示的协程任务 */
    private var hideBrightnessIndicatorJob: Job? = null
    /** 延迟隐藏信息提示布局的协程任务 */
    private var hideInfoLayoutJob: Job? = null

    /** 是否允许后台播放（点击“后台播放”按钮或开启自动后台播放） */
    private var playInBackground: Boolean = false
    /** 标记当前 Intent 是否为新 Intent，用于决定是否重建播放列表 */
    private var isIntentNew: Boolean = true

    /** 当前是否处于画中画模式 */
    private var isPipActive: Boolean = false

    /**
     * 是否应该启用“快速快进/后退”
     * 由用户偏好 + 当前媒体时长共同决定
     */
    private val shouldFastSeek: Boolean
        get() = playerPreferences.shouldFastSeek(mediaController?.duration ?: C.TIME_UNSET)

    /**
     * Player
     */
    /** 异步创建 MediaController 的 Future，用于连接 PlayerService */
    private var controllerFuture: ListenableFuture<MediaController>? = null
    /** 实际用于控制播放的 MediaController（连接到 PlayerService） */
    private var mediaController: MediaController? = null
    /** 手势帮助类，负责处理单击/双击/长按/滑动等手势 */
    private lateinit var playerGestureHelper: PlayerGestureHelper
    /** 对外 API 封装，用于读取外部传入的标题、进度、字幕等参数 */
    private lateinit var playerApi: PlayerApi
    /** 音量管理器，处理系统音量和增益效果 */
    private lateinit var volumeManager: VolumeManager
    /** 亮度管理器，处理屏幕亮度调整 */
    private lateinit var brightnessManager: BrightnessManager
    /** 画中画广播接收器，用于响应 PIP 控制按钮 */
    private var pipBroadcastReceiver: BroadcastReceiver? = null

    /**
     * Listeners
     */
    /** 播放状态监听器，集中处理 MediaController 的各种回调 */
    private val playbackStateListener: Player.Listener = playbackStateListener()
    /** 记录当前为哪个 MediaItem 打开本地字幕选择器（避免切换媒体时错位） */
    private var subtitleFileLauncherLaunchedForMediaItem: MediaItem? = null

    private val subtitleFileLauncher = registerForActivityResult(OpenDocument()) { uri ->
        if (uri != null && subtitleFileLauncherLaunchedForMediaItem != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            lifecycleScope.launch {
                maybeInitControllerFuture()
                controllerFuture?.await()?.addSubtitleTrack(uri)
            }
        }
    }

    /**
     * Player controller views
     */
    private lateinit var audioTrackButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var exoContentFrameLayout: AspectRatioFrameLayout
    private lateinit var lockControlsButton: ImageButton
    private lateinit var playbackSpeedButton: ImageButton
    private lateinit var playerLockControls: FrameLayout
    private lateinit var playerUnlockControls: FrameLayout
    private lateinit var playerCenterControls: LinearLayout
    private lateinit var screenRotateButton: ImageButton
    private lateinit var pipButton: ImageButton
    private lateinit var seekBar: TimeBar
    private lateinit var subtitleTrackButton: ImageButton
    private lateinit var unlockControlsButton: ImageButton
    private lateinit var videoTitleTextView: TextView
    private lateinit var videoZoomButton: ImageButton
    private lateinit var playInBackgroundButton: ImageButton
    private lateinit var loopModeButton: ImageButton
    private lateinit var extraControls: LinearLayout

    /**
     * 设备是否支持画中画功能（API 26+ 且具备 PIP 特性）
     */
    private val isPipSupported: Boolean by lazy {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    }

    /**
     * 当前应用是否已在系统设置中开启画中画权限
     */
    private val isPipEnabled: Boolean
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    appOps?.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, Process.myUid(), packageName) == AppOpsManager.MODE_ALLOWED
                } else {
                    @Suppress("DEPRECATION")
                    appOps?.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, Process.myUid(), packageName) == AppOpsManager.MODE_ALLOWED
                }
            } else {
                false
            }
        }

    /**
     * Activity 创建入口：
     * - 根据用户偏好设置夜间模式和动态色
     * - 配置刘海区域显示策略和沉浸式系统栏
     * - 初始化 ViewBinding 和所有控制按钮
     * - 创建音量/亮度/手势管理器
     * - 自定义返回键逻辑（TV 设备上先隐藏控制条，再退出）
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(
            when (userData.darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                DarkThemeConfig.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                DarkThemeConfig.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            },
        )

        if (userData.useDynamicColor) {
            DynamicColors.applyToActivityIfAvailable(this)
        }

        // The window is always allowed to extend into the DisplayCutout areas on the short edges of the screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing views
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

        if (playerPreferences.controlButtonsPosition == ControlButtonsPosition.RIGHT) {
            extraControls.gravity = Gravity.END
        }

        if (!isPipSupported) {
            pipButton.visibility = View.GONE
        }

        // 注册进度条拖动监听器：按下时暂停播放，拖动时显示时间信息，松开后恢复播放
        seekBar.addListener(
            object : TimeBar.OnScrubListener {
                override fun onScrubStart(timeBar: TimeBar, position: Long) {
                    mediaController?.run {
                        if (isPlaying) {
                            isPlayingOnScrubStart = true
                            pause()
                        }
                        isFrameRendered = true
                        scrubStartPosition = currentPosition
                        previousScrubPosition = currentPosition
                        scrub(position)
                        showPlayerInfo(
                            info = Utils.formatDurationMillis(position),
                            subInfo = "[${Utils.formatDurationMillisSign(position - scrubStartPosition)}]",
                        )
                    }
                }

                override fun onScrubMove(timeBar: TimeBar, position: Long) {
                    scrub(position)
                    showPlayerInfo(
                        info = Utils.formatDurationMillis(position),
                        subInfo = "[${Utils.formatDurationMillisSign(position - scrubStartPosition)}]",
                    )
                }

                override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                    hidePlayerInfo(0L)
                    scrubStartPosition = -1L
                    if (isPlayingOnScrubStart) {
                        mediaController?.play()
                    }
                }
            },
        )

        // 初始化音量和亮度管理器
        volumeManager = VolumeManager(audioManager = getSystemService(AUDIO_SERVICE) as AudioManager)
        brightnessManager = BrightnessManager(activity = this)
        // 初始化手势帮助类：双击/长按/滑动等全部交给 PlayerGestureHelper 处理
        playerGestureHelper = PlayerGestureHelper(
            viewModel = viewModel,
            activity = this,
            volumeManager = volumeManager,
            brightnessManager = brightnessManager,
            onScaleChanged = { scale ->
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
            if (binding.playerView.isControllerFullyVisible && mediaController?.isPlaying == true && isDeviceTvBox()) {
                binding.playerView.hideController()
            } else {
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
        super.onStart()
        // 如果用户开启“记忆播放器亮度”，则恢复上一次的亮度值
        if (playerPreferences.rememberPlayerBrightness) {
            brightnessManager.setBrightness(playerPreferences.playerBrightness)
        }
        lifecycleScope.launch {
            // 延迟初始化 MediaController（首次进入或 Service 尚未连接）
            maybeInitControllerFuture()
            mediaController = controllerFuture?.await()

            setOrientation()
            applyVideoZoom(videoZoom = playerPreferences.playerVideoZoom)
            mediaController?.currentMediaItem?.mediaId?.let {
                applyVideoScale(videoScale = viewModel.getVideoState(it)?.videoScale ?: 1f)
            }

            // 配置 PlayerView、系统栏、循环模式等，并启动播放
            mediaController?.run {
                binding.playerView.player = this
                isMediaItemReady = currentMediaItem != null
                toggleSystemBars(showBars = binding.playerView.isControllerFullyVisible)
                videoTitleTextView.text = currentMediaItem?.mediaMetadata?.title
                applyLoopMode(playerPreferences.loopMode)
                if (playerPreferences.shouldUseVolumeBoost) {
                    try {
                        volumeManager.loudnessEnhancer = LoudnessEnhancer(getAudioSessionId())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                updateKeepScreenOnFlag()
                addListener(playbackStateListener)
                startPlayback()
            }
            subtitleFileLauncherLaunchedForMediaItem = null
        }
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
        binding.playerView.player = null
        binding.volumeGestureLayout.visibility = View.GONE
        binding.brightnessGestureLayout.visibility = View.GONE
        currentOrientation = requestedOrientation
        mediaController?.run {
            viewModel.playWhenReady = playWhenReady
            lifecycleScope.launch {
                viewModel.skipSilenceEnabled = getSkipSilenceEnabled()
            }
            removeListener(playbackStateListener)
        }
        val shouldPlayInBackground = playInBackground || playerPreferences.autoBackgroundPlay
        if (subtitleFileLauncherLaunchedForMediaItem != null || !shouldPlayInBackground) {
            mediaController?.pause()
        }

        if (isPipActive) {
            finish()
            if (!shouldPlayInBackground) {
                mediaController?.stopPlayerSession()
            }
        }

        controllerFuture?.run {
            MediaController.releaseFuture(this)
            controllerFuture = null
        }
        super.onStop()
    }

    /**
     * 懒加载 MediaController：
     * 仅在 controllerFuture 为空时创建，与 PlayerService 建立连接
     */
    private fun maybeInitControllerFuture() {
        if (controllerFuture == null) {
            val sessionToken = SessionToken(applicationContext, ComponentName(applicationContext, PlayerService::class.java))
            controllerFuture = MediaController.Builder(applicationContext, sessionToken).buildAsync()
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
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
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
                registerReceiver(pipBroadcastReceiver, IntentFilter(PIP_INTENT_ACTION), RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(pipBroadcastReceiver, IntentFilter(PIP_INTENT_ACTION))
            }
        } else {
            binding.playerView.subtitleView?.setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, playerPreferences.subtitleTextSize.toFloat())
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
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updatePictureInPictureParams(enableAutoEnter: Boolean = mediaController?.isPlaying == true): PictureInPictureParams {
        val playerViewWidth = binding.playerView.width
        val playerViewHeight = binding.playerView.height

        // Validate playerView dimensions
        if (playerViewWidth <= 0 || playerViewHeight <= 0) {
            Timber.w("Invalid playerView dimensions: $playerViewWidth x $playerViewHeight")
            return PictureInPictureParams.Builder().build()
        }

        val displayAspectRatio = Rational(playerViewWidth, playerViewHeight)

        return PictureInPictureParams.Builder().apply {
            val aspectRatio = calculateVideoAspectRatio()
            if (aspectRatio != null) {
                val sourceRectHint = calculateSourceRectHint(displayAspectRatio, aspectRatio)
                setAspectRatio(aspectRatio)
                setSourceRectHint(sourceRectHint)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setSeamlessResizeEnabled(playerPreferences.autoPip && enableAutoEnter)
                setAutoEnterEnabled(playerPreferences.autoPip && enableAutoEnter)
            }

            setActions(
                listOf(
                    createPipAction(
                        context = this@PlayerActivity,
                        title = "skip to previous",
                        icon = coreUiR.drawable.ic_skip_prev,
                        actionCode = PIP_ACTION_PREVIOUS,
                    ),
                    if (mediaController?.isPlaying == true) {
                        createPipAction(
                            context = this@PlayerActivity,
                            title = "pause",
                            icon = coreUiR.drawable.ic_pause,
                            actionCode = PIP_ACTION_PAUSE,
                        )
                    } else {
                        createPipAction(
                            context = this@PlayerActivity,
                            title = "play",
                            icon = coreUiR.drawable.ic_play,
                            actionCode = PIP_ACTION_PLAY,
                        )
                    },
                    createPipAction(
                        context = this@PlayerActivity,
                        title = "skip to next",
                        icon = coreUiR.drawable.ic_skip_next,
                        actionCode = PIP_ACTION_NEXT,
                    ),
                ),
            )
        }.build().also { params ->
            try {
                if (!isFinishing && !isDestroyed) {
                    setPictureInPictureParams(params)
                }
            } catch (e: IllegalStateException) {
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
            Rect(0, space, playerWidth.toInt(), (playerWidth / aspectRatio.toFloat()).toInt() + space)
        } else {
            val space = ((playerWidth - (playerHeight * aspectRatio.toFloat())) / 2).toInt()
            Rect(space, 0, (playerHeight * aspectRatio.toFloat()).toInt() + space, playerHeight.toInt())
        }
    }

    /**
     * 根据用户偏好和当前视频尺寸设置屏幕方向：
     * - 配合自定义的 ScreenOrientation 枚举
     * - 支持自动/横屏/竖屏/跟随视频方向等模式
     */
    private fun setOrientation() {
        requestedOrientation = currentOrientation ?: playerPreferences.playerScreenOrientation.toActivityOrientation(
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
                    val systemCaptionStyle = CaptionStyleCompat.createFromCaptionStyle(captioningManager.userStyle)
                    setStyle(systemCaptionStyle)
                } else {
                    val userStyle = CaptionStyleCompat(
                        Color.WHITE,
                        Color.BLACK.takeIf { playerPreferences.subtitleBackground } ?: Color.TRANSPARENT,
                        Color.TRANSPARENT,
                        CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
                        Color.BLACK,
                        Typeface.create(
                            playerPreferences.subtitleFont.toTypeface(),
                            Typeface.BOLD.takeIf { playerPreferences.subtitleTextBold } ?: Typeface.NORMAL,
                        ),
                    )
                    setStyle(userStyle)
                    setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, playerPreferences.subtitleTextSize.toFloat())
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
                Toast.makeText(this, coreUiR.string.enable_pip_from_settings, Toast.LENGTH_SHORT).show()
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
        val isNewUriTheCurrentMediaItem = mediaController?.currentMediaItem?.localConfiguration?.uri.toString() == uri.toString()

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
        val mediaContentUri = getMediaContentUri(uri)
        val playlist = mediaContentUri?.let { mediaUri ->
            viewModel.getPlaylistFromUri(mediaUri)
                .map { it.uriString }
                .toMutableList()
                .apply {
                    if (!contains(mediaUri.toString())) {
                        add(index = 0, element = mediaUri.toString())
                    }
                }
        } ?: listOf(uri.toString())

        val mediaItemIndexToPlay = playlist.indexOfFirst {
            it == (mediaContentUri ?: uri).toString()
        }.takeIf { it >= 0 } ?: 0

        val mediaItems = playlist.mapIndexed { index, uri ->
            MediaItem.Builder().apply {
                setUri(uri)
                setMediaId(uri)
                if (index == mediaItemIndexToPlay) {
                    setMediaMetadata(
                        MediaMetadata.Builder().apply {
                            setTitle(playerApi.title)
                        }.build(),
                    )
                    val apiSubs = playerApi.getSubs().map { subtitle ->
                        uriToSubtitleConfiguration(
                            uri = subtitle.uri,
                            subtitleEncoding = playerPreferences.subtitleTextEncoding,
                            isSelected = subtitle.isSelected,
                        )
                    }
                    setSubtitleConfigurations(apiSubs)
                }
            }.build()
        }

        withContext(Dispatchers.Main) {
            mediaController?.run {
                setMediaItems(mediaItems, mediaItemIndexToPlay, playerApi.position?.toLong() ?: C.TIME_UNSET)
                playWhenReady = viewModel.playWhenReady
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
                val videoScale = mediaController?.currentMediaItem?.mediaId?.let { viewModel.getVideoState(it)?.videoScale } ?: 1f
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
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_DPAD_UP,
            -> {
                if (!binding.playerView.isControllerFullyVisible || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    volumeManager.increaseVolume(playerPreferences.showSystemVolumePanel)
                    showVolumeGestureLayout()
                    return true
                }
            }

            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_DPAD_DOWN,
            -> {
                if (!binding.playerView.isControllerFullyVisible || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    volumeManager.decreaseVolume(playerPreferences.showSystemVolumePanel)
                    showVolumeGestureLayout()
                    return true
                }
            }

            KeyEvent.KEYCODE_MEDIA_PLAY,
            KeyEvent.KEYCODE_MEDIA_PAUSE,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_BUTTON_SELECT,
            -> {
                when {
                    keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE -> mediaController?.pause()
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY -> mediaController?.play()
                    mediaController?.isPlaying == true -> mediaController?.pause()
                    else -> mediaController?.play()
                }
                return true
            }

            KeyEvent.KEYCODE_BUTTON_START,
            KeyEvent.KEYCODE_BUTTON_A,
            KeyEvent.KEYCODE_SPACE,
            -> {
                if (!binding.playerView.isControllerFullyVisible) {
                    binding.playerView.togglePlayPause()
                    return true
                }
            }

            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_BUTTON_L2,
            KeyEvent.KEYCODE_MEDIA_REWIND,
            -> {
                if (!binding.playerView.isControllerFullyVisible || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                    mediaController?.run {
                        if (scrubStartPosition == -1L) {
                            scrubStartPosition = currentPosition
                        }
                        val position = (currentPosition - 10_000).coerceAtLeast(0L)
                        seekBack(position, shouldFastSeek)
                        showPlayerInfo(
                            info = Utils.formatDurationMillis(position),
                            subInfo = "[${Utils.formatDurationMillisSign(position - scrubStartPosition)}]",
                        )
                        return true
                    }
                }
            }

            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_BUTTON_R2,
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD,
            -> {
                if (!binding.playerView.isControllerFullyVisible || keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
                    mediaController?.run {
                        if (scrubStartPosition == -1L) {
                            scrubStartPosition = currentPosition
                        }

                        val position = (currentPosition + 10_000).coerceAtMost(duration)
                        seekForward(position, shouldFastSeek)
                        showPlayerInfo(
                            info = Utils.formatDurationMillis(position),
                            subInfo = "[${Utils.formatDurationMillisSign(position - scrubStartPosition)}]",
                        )
                        return true
                    }
                }
            }

            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            -> {
                if (!binding.playerView.isControllerFullyVisible) {
                    binding.playerView.showController()
                    return true
                }
            }

        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 处理遥控器/键盘抬起事件：
     * - 抬起音量/方向键时隐藏音量 UI
     * - 抬起左右键/快进快退键时隐藏时间信息
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,
            -> {
                hideVolumeGestureLayout()
                return true
            }

            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_BUTTON_L2,
            KeyEvent.KEYCODE_MEDIA_REWIND,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_BUTTON_R2,
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD,
            -> {
                hidePlayerInfo()
                return true
            }
        }
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
