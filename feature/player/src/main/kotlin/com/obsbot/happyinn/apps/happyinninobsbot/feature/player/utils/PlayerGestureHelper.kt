package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.obsbot.happyinn.apps.happyinninobsbot.Utils
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.DoubleTapGesture
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.PlayerPreferences
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.dpToPx
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R as coreUiR
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.PlayerActivity
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.PlayerViewModel
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.R
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.seekBack
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.seekForward
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.shouldFastSeek
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.togglePlayPause
import kotlin.math.abs
import kotlin.math.roundToInt

@UnstableApi
@SuppressLint("ClickableViewAccessibility")
/**
 * 播放器手势帮助类，处理视频播放过程中的各种手势操作
 * - 单击显示/隐藏控制器
 * - 双击快进/后退或播放/暂停
 * - 长按快速播放
 * - 滑动调节音量和亮度
 * - 双指缩放视频
 * - 左右滑动快进/后退
 */
class PlayerGestureHelper(
    private val viewModel: PlayerViewModel,          // 播放器ViewModel，用于访问播放状态和偏好设置
    private val activity: PlayerActivity,            // 播放器Activity，用于访问UI组件和方法
    private val volumeManager: VolumeManager,        // 音量管理器，用于调节设备音量
    private val brightnessManager: BrightnessManager, // 亮度管理器，用于调节屏幕亮度
    private val onScaleChanged: (Float) -> Unit,     // 缩放变化回调，通知外部缩放比例变化
) {
    // 播放器偏好设置，从ViewModel中获取
    private val prefs: PlayerPreferences
        get() = viewModel.playerPrefs.value

    // 播放器视图，从Activity绑定中获取
    private val playerView: PlayerView
        get() = activity.binding.playerView

    // 判断是否应该快速快进/后退
    private val shouldFastSeek: Boolean
        get() = playerView.player?.duration?.let { prefs.shouldFastSeek(it) } == true

    // 视频内容帧布局，用于处理视频缩放
    private var exoContentFrameLayout: AspectRatioFrameLayout = playerView.findViewById(R.id.exo_content_frame)

    // 当前手势操作类型
    private var currentGestureAction: GestureAction? = null
    // 快进/后退开始时间
    private var seekStart = 0L
    // 当前播放位置
    private var position = 0L
    // 快进/后退变化量
    private var seekChange = 0L
    // 触摸指针数量
    private var pointerCount = 1
    // 快进/后退开始时是否正在播放
    private var isPlayingOnSeekStart: Boolean = false
    // 当前播放速度，用于长按快速播放后恢复
    private var currentPlaybackSpeed: Float? = null

    // 点击手势检测器，处理单击、长按和双击事件
    private val tapGestureDetector = GestureDetector(
        playerView.context,
        object : GestureDetector.SimpleOnGestureListener() {
            // 单击确认事件，用于显示/隐藏控制器
            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                with(playerView) {
                    if (!isControllerFullyVisible) showController() else hideController()
                }
                return true
            }

            // 长按事件，用于快速播放
            override fun onLongPress(e: MotionEvent) {
                if (!prefs.useLongPressControls) return
                if (playerView.player?.isPlaying == false) return
                if (activity.isControlsLocked) return

                if (currentGestureAction == null) {
                    currentGestureAction = GestureAction.FAST_PLAYBACK
                    currentPlaybackSpeed = playerView.player?.playbackParameters?.speed
                }
                if (currentGestureAction != GestureAction.FAST_PLAYBACK) return
                if (pointerCount >= 3) return

                playerView.hideController()
                activity.showTopInfo(activity.getString(coreUiR.string.fast_playback_speed, prefs.longPressControlsSpeed))
                playerView.player?.setPlaybackSpeed(prefs.longPressControlsSpeed)
            }

            // 双击事件，根据偏好设置执行快进/后退或播放/暂停
            override fun onDoubleTap(event: MotionEvent): Boolean {
                if (activity.isControlsLocked) return false

                playerView.player?.run {
                    when (prefs.doubleTapGesture) {
                        // 双击左右区域分别快进/后退
                        DoubleTapGesture.FAST_FORWARD_AND_REWIND -> {
                            val viewCenterX = playerView.measuredWidth / 2

                            if (event.x.toInt() < viewCenterX) {
                                val newPosition = currentPosition - prefs.seekIncrement.toMillis
                                seekBack(newPosition.coerceAtLeast(0), shouldFastSeek)
                            } else {
                                val newPosition = currentPosition + prefs.seekIncrement.toMillis
                                seekForward(newPosition.coerceAtMost(duration), shouldFastSeek)
                            }
                        }

                        // 双击左侧后退，右侧前进，中间播放/暂停
                        DoubleTapGesture.BOTH -> {
                            val eventPositionX = event.x / playerView.measuredWidth

                            if (eventPositionX < 0.35) {
                                val newPosition = currentPosition - prefs.seekIncrement.toMillis
                                seekBack(newPosition.coerceAtLeast(0), shouldFastSeek)
                            } else if (eventPositionX > 0.65) {
                                val newPosition = currentPosition + prefs.seekIncrement.toMillis
                                seekForward(newPosition.coerceAtMost(duration), shouldFastSeek)
                            } else {
                                playerView.togglePlayPause()
                            }
                        }

                        // 双击播放/暂停
                        DoubleTapGesture.PLAY_PAUSE -> playerView.togglePlayPause()

                        // 禁用双击手势
                        DoubleTapGesture.NONE -> return false
                    }
                } ?: return false
                return true
            }
        },
    )

    // 快进/后退手势检测器，处理左右滑动快进/后退
    private val seekGestureDetector = GestureDetector(
        playerView.context,
        object : GestureDetector.SimpleOnGestureListener() {
            // 滑动事件，处理左右滑动快进/后退
            override fun onScroll(
                firstEvent: MotionEvent?,      // 初始触摸事件
                currentEvent: MotionEvent,     // 当前触摸事件
                distanceX: Float,              // X轴滑动距离
                distanceY: Float,              // Y轴滑动距离
            ): Boolean {
                if (firstEvent == null) return false
                if (inExclusionArea(firstEvent)) return false // 排除区域内不处理
                if (!prefs.useSeekControls) return false      // 检查是否启用了快进/后退控制
                if (activity.isControlsLocked) return false   // 控制锁定时不处理
                if (!activity.isMediaItemReady) return false  // 媒体项未准备好时不处理
                if (abs(distanceX / distanceY) < 2) return false // 确保是水平滑动

                if (currentGestureAction == null) {
                    // 初始化快进/后退参数
                    seekChange = 0L
                    seekStart = playerView.player?.currentPosition ?: 0L
                    playerView.controllerAutoShow = playerView.isControllerFullyVisible
                    // 保存初始播放状态
                    if (playerView.player?.isPlaying == true) {
                        playerView.player?.pause()
                        isPlayingOnSeekStart = true
                    }
                    currentGestureAction = GestureAction.SEEK
                }
                if (currentGestureAction != GestureAction.SEEK) return false

                // 计算快进/后退步长
                val distanceDiff = abs(Utils.pxToDp(distanceX) / 4).coerceIn(0.5f, 10f)
                val change = (distanceDiff * SEEK_STEP_MS).toLong()

                playerView.player?.run {
                    if (distanceX < 0L) {
                        // 向右滑动，快进
                        seekChange = (seekChange + change)
                            .takeIf { it + seekStart < duration } ?: (duration - seekStart)
                        position = (seekStart + seekChange).coerceAtMost(duration)
                        seekForward(positionMs = position, shouldFastSeek = shouldFastSeek)
                    } else {
                        // 向左滑动，后退
                        seekChange = (seekChange - change)
                            .takeIf { it + seekStart > 0 } ?: (0 - seekStart)
                        position = seekStart + seekChange
                        seekBack(positionMs = position, shouldFastSeek = shouldFastSeek)
                    }
                    // 显示快进/后退信息
                    activity.showPlayerInfo(
                        info = Utils.formatDurationMillis(this.currentPosition),
                        subInfo = "[${Utils.formatDurationMillisSign(seekChange)}]",
                    )
                    return true
                }
                return false
            }
        },
    )

    // 音量和亮度手势检测器，处理上下滑动调节音量和亮度
    private val volumeAndBrightnessGestureDetector = GestureDetector(
        playerView.context,
        object : GestureDetector.SimpleOnGestureListener() {
            // 滑动事件，处理上下滑动调节音量和亮度
            override fun onScroll(
                firstEvent: MotionEvent?,      // 初始触摸事件
                currentEvent: MotionEvent,     // 当前触摸事件
                distanceX: Float,              // X轴滑动距离
                distanceY: Float,              // Y轴滑动距离
            ): Boolean {
                if (firstEvent == null) return false
                if (inExclusionArea(firstEvent)) return false // 排除区域内不处理
                if (!prefs.useSwipeControls) return false     // 检查是否启用了滑动控制
                if (activity.isControlsLocked) return false   // 控制锁定时不处理
                if (abs(distanceY / distanceX) < 2) return false // 确保是垂直滑动

                if (currentGestureAction == null) {
                    currentGestureAction = GestureAction.SWIPE
                }
                if (currentGestureAction != GestureAction.SWIPE) return false

                val viewCenterX = playerView.measuredWidth / 2
                // 计算完整滑动范围
                val distanceFull = playerView.measuredHeight * FULL_SWIPE_RANGE_SCREEN_RATIO
                // 计算滑动比例变化
                val ratioChange = distanceY / distanceFull

                if (firstEvent.x.toInt() > viewCenterX) {
                    // 右侧滑动调节音量
                    val change = ratioChange * volumeManager.maxStreamVolume
                    volumeManager.setVolume(volumeManager.currentVolume + change, prefs.showSystemVolumePanel)
                    activity.showVolumeGestureLayout()
                } else {
                    // 左侧滑动调节亮度
                    val change = ratioChange * brightnessManager.maxBrightness
                    brightnessManager.setBrightness(brightnessManager.currentBrightness + change)
                    activity.showBrightnessGestureLayout()
                }
                return true
            }
        },
    )

    // 缩放手势检测器，处理双指缩放视频
    private val zoomGestureDetector = ScaleGestureDetector(
        playerView.context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            // 缩放范围限制：0.25倍到4.0倍
            private val SCALE_RANGE = 0.25f..4.0f

            // 缩放事件，处理视频缩放
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (!prefs.useZoomControls) return false // 检查是否启用了缩放控制
                if (activity.isControlsLocked) return false // 控制锁定时不处理

                if (currentGestureAction == null) {
                    currentGestureAction = GestureAction.ZOOM
                }
                if (currentGestureAction != GestureAction.ZOOM) return false

                playerView.player?.videoSize?.let { videoSize ->
                    if (videoSize.width <= 0) return false
                    // 计算新的缩放因子
                    val scaleFactor = (exoContentFrameLayout.scaleX * detector.scaleFactor)
                    // 计算更新后的视频缩放比例
                    val updatedVideoScale = (exoContentFrameLayout.width * scaleFactor) / videoSize.width.toFloat()
                    // 检查是否在缩放范围内
                    if (updatedVideoScale in SCALE_RANGE) {
                        // 应用缩放
                        exoContentFrameLayout.scaleX = scaleFactor
                        exoContentFrameLayout.scaleY = scaleFactor
                        // 通知外部缩放比例变化
                        onScaleChanged(scaleFactor)
                    }
                    // 计算当前视频缩放比例
                    val currentVideoScale = (exoContentFrameLayout.width * exoContentFrameLayout.scaleX) / videoSize.width.toFloat()
                    if (currentVideoScale.isNaN() || currentVideoScale.isInfinite()) return false
                    // 显示缩放百分比
                    activity.showPlayerInfo("${(currentVideoScale * 100).roundToInt()}%")
                }
                return true
            }
        },
    )

    /**
     * 释放手势资源，恢复播放状态
     * - 隐藏所有手势指示器
     * - 恢复播放速度
     * - 恢复控制器自动显示
     * - 恢复播放状态
     * - 重置手势操作类型
     */
    private fun releaseGestures() {
        // 隐藏音量指示器
        activity.hideVolumeGestureLayout()
        // 隐藏亮度指示器
        activity.hideBrightnessGestureLayout()
        // 隐藏信息布局
        activity.hidePlayerInfo(0L)
        // 隐藏快速播放顶部信息布局
        activity.hideTopInfo()

        // 恢复原始播放速度
        currentPlaybackSpeed?.let {
            playerView.player?.setPlaybackSpeed(it)
            currentPlaybackSpeed = null
        }

        // 恢复控制器自动显示
        playerView.controllerAutoShow = true
        // 如果快进/后退开始时正在播放，则恢复播放
        if (isPlayingOnSeekStart) playerView.player?.play()
        isPlayingOnSeekStart = false
        // 重置当前手势操作
        currentGestureAction = null
    }

    /**
     * 检查初始触摸事件是否在手势排除区域内
     * @param firstEvent 初始触摸事件
     * @return 是否在排除区域内
     */
    private fun inExclusionArea(firstEvent: MotionEvent): Boolean {
        val gestureExclusionBorder = playerView.context.dpToPx(GESTURE_EXCLUSION_AREA)

        // 检查触摸点是否在屏幕边缘的排除区域内
        return firstEvent.y < gestureExclusionBorder || firstEvent.y > playerView.height - gestureExclusionBorder ||
            firstEvent.x < gestureExclusionBorder || firstEvent.x > playerView.width - gestureExclusionBorder
    }

    // 初始化触摸监听器，处理各种手势
    init {
        playerView.setOnTouchListener { _, motionEvent ->
            pointerCount = motionEvent.pointerCount
            // 根据触摸指针数量分发不同的手势检测器
            when (motionEvent.pointerCount) {
                1 -> {
                    // 单指操作：点击、音量/亮度调节、快进/后退
                    tapGestureDetector.onTouchEvent(motionEvent)
                    volumeAndBrightnessGestureDetector.onTouchEvent(motionEvent)
                    seekGestureDetector.onTouchEvent(motionEvent)
                }

                2 -> {
                    // 双指操作：缩放
                    zoomGestureDetector.onTouchEvent(motionEvent)
                }
            }

            // 手势结束或多指针操作时释放手势资源
            if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.pointerCount >= 3) {
                releaseGestures()
            }
            true
        }
    }

    // 伴生对象，定义常量
    companion object {
        const val FULL_SWIPE_RANGE_SCREEN_RATIO = 0.66f // 完整滑动范围占屏幕高度的比例
        const val GESTURE_EXCLUSION_AREA = 20f          // 手势排除区域大小（dp）
        const val SEEK_STEP_MS = 1000L                  // 快进/后退步长（毫秒）
    }
}

/**
 * 将秒转换为毫秒的扩展属性
 */
inline val Int.toMillis get() = this * 1000

/**
 * 手势操作类型枚举
 */
enum class GestureAction {
    SWIPE,         // 滑动（调节音量和亮度）
    SEEK,          // 快进/后退
    ZOOM,          // 缩放
    FAST_PLAYBACK, // 快速播放
}
