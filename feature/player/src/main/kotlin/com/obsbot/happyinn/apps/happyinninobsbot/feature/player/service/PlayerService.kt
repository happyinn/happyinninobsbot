package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.DISCONTINUITY_REASON_AUTO_TRANSITION
import androidx.media3.common.Player.DISCONTINUITY_REASON_REMOVE
import androidx.media3.common.Player.DISCONTINUITY_REASON_SEEK
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.CommandButton
import androidx.media3.session.CommandButton.ICON_UNDEFINED
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.ListenableFuture
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.model.VideoState
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.media.MediaRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.media.PreferencesRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.DecoderPriority
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.PlayerPreferences
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Resume
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.deleteFiles
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.getFilenameFromUri
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.getLocalSubtitles
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.getPath
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.subtitleCacheDir
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.PlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R as coreUiR
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.R
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.addAdditionalSubtitleConfiguration
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.switchTrack
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.uriToSubtitleConfiguration
import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory
import java.io.File
import javax.inject.Inject
import kotlin.time.measureTimedValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

@OptIn(UnstableApi::class)
@AndroidEntryPoint
/**
 * 媒体播放服务，负责管理媒体会话、播放状态和播放控制
 * - 处理媒体项的加载和播放
 * - 管理播放速度、音频/字幕轨道选择
 * - 保存和恢复播放位置
 * - 支持自定义命令扩展
 */
class PlayerService : MediaSessionService() {
    // 服务协程作用域，用于处理异步操作
    private val serviceScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    // 媒体会话，用于与控制器通信
    private var mediaSession: MediaSession? = null

    // 偏好设置仓库，用于获取和保存播放偏好
    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    // 媒体仓库，用于获取和更新媒体信息
    @Inject
    lateinit var mediaRepository: MediaRepository

    // 播放器偏好设置，从仓库中获取
    private val playerPreferences: PlayerPreferences
        get() = runBlocking { preferencesRepository.playerPreferences.first() }

    // 自定义命令列表
    private val customCommands = CustomCommands.asSessionCommands()

    // 媒体项是否准备就绪
    private var isMediaItemReady = false
    // 当前视频播放状态
    private var currentVideoState: VideoState? = null

    // 播放状态监听器，监听播放器的各种状态变化
    private val playbackStateListener = object : Player.Listener {
        /**
         * 媒体项切换时调用
         * @param mediaItem 新的媒体项
         * @param reason 切换原因
         */
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            // 重复播放时不处理
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT) return
            // 重置媒体项准备状态
            isMediaItemReady = false
            if (mediaItem != null) {
                serviceScope.launch {
                    // 获取当前视频状态
                    currentVideoState = mediaRepository.getVideoState(mediaItem.mediaId)
                    // 设置播放速度
                    mediaSession?.player?.setPlaybackSpeed(
                        currentVideoState?.playbackSpeed ?: playerPreferences.defaultPlaybackSpeed,
                    )
                    // 恢复播放位置（如果启用了恢复功能）
                    currentVideoState?.let { state ->
                        if (mediaSession?.player?.currentPosition != 0L) return@let
                        state.position?.takeIf { playerPreferences.resume == Resume.YES }?.let {
                            mediaSession?.player?.seekTo(it)
                        }
                    }
                }
            }
        }

        /**
         * 播放位置不连续变化时调用（如快进/后退）
         * @param oldPosition 旧位置信息
         * @param newPosition 新位置信息
         * @param reason 变化原因
         */
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int,
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            val oldMediaItem = oldPosition.mediaItem ?: return

            when (reason) {
                // 快进/后退或自动切换媒体项时
                DISCONTINUITY_REASON_SEEK,
                DISCONTINUITY_REASON_AUTO_TRANSITION,
                -> {
                    val newMediaItem = newPosition.mediaItem
                    // 切换到新媒体项时，保存旧媒体项的播放位置
                    if (newMediaItem != null && oldMediaItem != newMediaItem) {
                        serviceScope.launch {
                            mediaRepository.updateMediumPosition(
                                uri = oldMediaItem.mediaId,
                                // 只有快进/后退时才保存位置，自动切换时不保存
                                position = oldPosition.positionMs.takeIf { reason == DISCONTINUITY_REASON_SEEK } ?: C.TIME_UNSET,
                            )
                        }
                    }
                }

                // 移除媒体项时
                DISCONTINUITY_REASON_REMOVE -> {
                    // 保存被移除媒体项的播放位置
                    serviceScope.launch {
                        mediaRepository.updateMediumPosition(
                            uri = oldMediaItem.mediaId,
                            position = oldPosition.positionMs,
                        )
                    }
                }

                else -> return
            }
        }

        /**
         * 音视频轨道变化时调用
         * @param tracks 新的轨道信息
         */
        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            // 媒体项准备就绪时
            if (!isMediaItemReady && tracks.groups.isNotEmpty()) {
                isMediaItemReady = true

                // 恢复之前选择的音轨和字幕轨道
                currentVideoState?.let { state ->
                    if (!playerPreferences.rememberSelections) return@let
                    state.audioTrackIndex?.let {
                        mediaSession?.player?.switchTrack(C.TRACK_TYPE_AUDIO, it)
                    }
                    state.subtitleTrackIndex?.let {
                        mediaSession?.player?.switchTrack(C.TRACK_TYPE_TEXT, it)
                    }
                }
            }
        }

        /**
         * 播放状态变化时调用
         * @param playbackState 新的播放状态
         */
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)

            // 播放结束或空闲时，重置播放参数
            if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                mediaSession?.player?.trackSelectionParameters = TrackSelectionParameters.DEFAULT
                mediaSession?.player?.setPlaybackSpeed(playerPreferences.defaultPlaybackSpeed)
            }

            // 播放准备就绪时，更新最后播放时间
            if (playbackState == Player.STATE_READY) {
                mediaSession?.player?.let {
                    serviceScope.launch {
                        mediaRepository.updateMediumLastPlayedTime(
                            uri = it.currentMediaItem?.mediaId ?: return@launch,
                            lastPlayedTime = System.currentTimeMillis(),
                        )
                    }
                }
            }
        }

        /**
         * 播放/暂停状态变化时调用
         * @param playWhenReady 是否准备播放
         * @param reason 状态变化原因
         */
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)

            // 媒体项播放结束时
            if (reason == Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM) {
                // 如果设置了重复播放
                if (mediaSession?.player?.repeatMode != Player.REPEAT_MODE_OFF) {
                    // 从头开始播放
                    mediaSession?.player?.seekTo(0)
                    mediaSession?.player?.play()
                    return
                }
                // 否则停止播放并清理资源
                mediaSession?.run {
                    saveCurrentMediaPlaybackPosition(player)
                    player.clearMediaItems()
                    player.stop()
                }
                stopSelf()
            }
        }

        /**
         * 实际播放状态变化时调用
         * @param isPlaying 是否正在播放
         */
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            // 保存当前播放位置
            mediaSession?.run {
                saveCurrentMediaPlaybackPosition(player)
            }
        }
    }

    // 媒体会话回调，处理会话连接、媒体项设置和自定义命令
    private val mediaSessionCallback = object : MediaSession.Callback {
        /**
         * 媒体会话连接时调用
         * @param session 媒体会话
         * @param controller 控制器信息
         * @return 连接结果
         */
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)
            // 接受连接并添加自定义命令
            return MediaSession.ConnectionResult.accept(
                connectionResult.availableSessionCommands
                    .buildUpon()
                    .addSessionCommands(customCommands)
                    .build(),
                connectionResult.availablePlayerCommands,
            )
        }

        /**
         * 设置媒体项时调用
         * @param mediaSession 媒体会话
         * @param controller 控制器信息
         * @param mediaItems 媒体项列表
         * @param startIndex 起始索引
         * @param startPositionMs 起始位置（毫秒）
         * @return 包含更新后媒体项和起始位置的未来结果
         */
        override fun onSetMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
            startIndex: Int,
            startPositionMs: Long,
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> = serviceScope.future(Dispatchers.Default) {
            // 测量更新媒体项的时间
            val (updatedMediaItems, time) = measureTimedValue {
                updatedMediaItemsWithMetadata(mediaItems)
            }
            return@future MediaSession.MediaItemsWithStartPosition(updatedMediaItems, startIndex, startPositionMs)
        }

        /**
         * 添加媒体项时调用
         * @param mediaSession 媒体会话
         * @param controller 控制器信息
         * @param mediaItems 要添加的媒体项列表
         * @return 更新后的媒体项列表的未来结果
         */
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
        ): ListenableFuture<MutableList<MediaItem>> = serviceScope.future(Dispatchers.Default) {
            val updatedMediaItems = updatedMediaItemsWithMetadata(mediaItems)
            return@future updatedMediaItems.toMutableList()
        }

        /**
         * 处理自定义命令时调用
         * @param session 媒体会话
         * @param controller 控制器信息
         * @param customCommand 自定义命令
         * @param args 命令参数
         * @return 命令执行结果的未来结果
         */
        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle,
        ): ListenableFuture<SessionResult> = serviceScope.future {
            val command = CustomCommands.fromSessionCommand(customCommand)
                ?: return@future SessionResult(SessionError.ERROR_BAD_VALUE)

            when (command) {
                // 添加字幕轨道命令
                CustomCommands.ADD_SUBTITLE_TRACK -> {
                    val subtitleUri = args.getString(CustomCommands.SUBTITLE_TRACK_URI_KEY)?.toUri()
                        ?: return@future SessionResult(SessionError.ERROR_BAD_VALUE)

                    val newSubConfiguration = uriToSubtitleConfiguration(
                        uri = subtitleUri,
                        subtitleEncoding = playerPreferences.subtitleTextEncoding,
                    )
                    mediaSession?.player?.let { player ->
                        val currentMediaItem = player.currentMediaItem ?: return@let
                        val textTracks = player.currentTracks.groups.filter {
                            it.type == C.TRACK_TYPE_TEXT && it.isSupported
                        }

                        mediaRepository.updateMediumPosition(
                            uri = currentMediaItem.mediaId,
                            position = player.currentPosition,
                        )
                        mediaRepository.updateMediumSubtitleTrack(
                            uri = currentMediaItem.mediaId,
                            subtitleTrackIndex = textTracks.size,
                        )
                        mediaRepository.addExternalSubtitleToMedium(
                            uri = currentMediaItem.mediaId,
                            subtitleUri = subtitleUri,
                        )
                        player.addAdditionalSubtitleConfiguration(newSubConfiguration)
                    }
                    return@future SessionResult(SessionResult.RESULT_SUCCESS)
                }

                // 切换音轨命令
                CustomCommands.SWITCH_AUDIO_TRACK -> {
                    val trackIndex = args.getInt(CustomCommands.AUDIO_TRACK_INDEX_KEY, 0)
                    mediaSession?.player?.let { player ->
                        player.switchTrack(C.TRACK_TYPE_AUDIO, trackIndex)
                        mediaRepository.updateMediumAudioTrack(
                            uri = player.currentMediaItem?.mediaId ?: return@let,
                            audioTrackIndex = trackIndex,
                        )
                    }
                    return@future SessionResult(SessionResult.RESULT_SUCCESS)
                }

                // 切换字幕轨道命令
                CustomCommands.SWITCH_SUBTITLE_TRACK -> {
                    val trackIndex = args.getInt(CustomCommands.SUBTITLE_TRACK_INDEX_KEY, 0)
                    mediaSession?.player?.let { player ->
                        player.switchTrack(C.TRACK_TYPE_TEXT, trackIndex)
                        mediaRepository.updateMediumSubtitleTrack(
                            uri = player.currentMediaItem?.mediaId ?: return@let,
                            subtitleTrackIndex = trackIndex,
                        )
                    }
                    return@future SessionResult(SessionResult.RESULT_SUCCESS)
                }

                // 设置跳过静音功能命令
                CustomCommands.SET_SKIP_SILENCE_ENABLED -> {
                    val enabled = args.getBoolean(CustomCommands.SKIP_SILENCE_ENABLED_KEY)
                    mediaSession?.player?.let { player ->
                        player.skipSilenceEnabled = enabled
                    }
                    return@future SessionResult(SessionResult.RESULT_SUCCESS)
                }

                // 获取跳过静音功能状态命令
                CustomCommands.GET_SKIP_SILENCE_ENABLED -> {
                    val enabled = mediaSession?.player?.skipSilenceEnabled ?: false
                    return@future SessionResult(
                        SessionResult.RESULT_SUCCESS,
                        Bundle().apply {
                            putBoolean(CustomCommands.SKIP_SILENCE_ENABLED_KEY, enabled)
                        },
                    )
                }

                // 设置播放速度命令
                CustomCommands.SET_PLAYBACK_SPEED -> {
                    val playbackSpeed = args.getFloat(CustomCommands.PLAYBACK_SPEED_KEY, 1.0f)
                    mediaSession?.player?.let { player ->
                        player.setPlaybackSpeed(playbackSpeed)
                        mediaRepository.updateMediumPlaybackSpeed(
                            uri = player.currentMediaItem?.mediaId ?: return@let,
                            playbackSpeed = playbackSpeed,
                        )
                    }
                    return@future SessionResult(SessionResult.RESULT_SUCCESS)
                }

                // 获取音频会话ID命令
                CustomCommands.GET_AUDIO_SESSION_ID -> {
                    val audioSessionId = mediaSession?.player?.audioSessionId ?: C.AUDIO_SESSION_ID_UNSET
                    return@future SessionResult(
                        SessionResult.RESULT_SUCCESS,
                        Bundle().apply {
                            putInt(CustomCommands.AUDIO_SESSION_ID_KEY, audioSessionId)
                        },
                    )
                }

                // 停止播放器会话命令
                CustomCommands.STOP_PLAYER_SESSION -> {
                    mediaSession?.run {
                        saveCurrentMediaPlaybackPosition(player)
                        player.clearMediaItems()
                        player.stop()
                    }
                    stopSelf()
                    return@future SessionResult(SessionResult.RESULT_SUCCESS)
                }
            }
        }
    }

    /**
     * 获取媒体会话
     * @param controllerInfo 控制器信息
     * @return 媒体会话实例
     */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    /**
     * 服务创建时调用，初始化媒体会话和播放器
     */
    override fun onCreate() {
        super.onCreate()
        // 创建渲染器工厂，配置解码器优先级
        val renderersFactory = NextRenderersFactory(applicationContext)
            .setEnableDecoderFallback(true) // 启用解码器回退
            .setExtensionRendererMode(
                // 根据偏好设置选择扩展渲染器模式
                when (playerPreferences.decoderPriority) {
                    DecoderPriority.DEVICE_ONLY -> DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
                    DecoderPriority.PREFER_DEVICE -> DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
                    DecoderPriority.PREFER_APP -> DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                },
            )

        // 创建轨道选择器，设置偏好语言
        val trackSelector = DefaultTrackSelector(applicationContext).apply {
            setParameters(
                buildUponParameters()
                    .setPreferredAudioLanguage(playerPreferences.preferredAudioLanguage)
                    .setPreferredTextLanguage(playerPreferences.preferredSubtitleLanguage),
            )
        }

        // 创建ExoPlayer实例
        val player = ExoPlayer.Builder(applicationContext)
            .setRenderersFactory(renderersFactory)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(
                // 配置音频属性
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build(),
                playerPreferences.requireAudioFocus, // 是否需要音频焦点
            )
            .setHandleAudioBecomingNoisy(playerPreferences.pauseOnHeadsetDisconnect) // 耳机断开时是否暂停
            .build()
            .also {
                it.addListener(playbackStateListener) // 添加播放状态监听器
                it.pauseAtEndOfMediaItems = !playerPreferences.autoplay // 是否自动播放下一个
            }

        try {
            // 创建媒体会话
            mediaSession = MediaSession.Builder(this, player).apply {
                // 设置会话活动，点击通知时启动PlayerActivity
                setSessionActivity(
                    PendingIntent.getActivity(
                        this@PlayerService,
                        0,
                        Intent(this@PlayerService, PlayerActivity::class.java),
                        PendingIntent.FLAG_IMMUTABLE,
                    ),
                )
                setCallback(mediaSessionCallback) // 设置会话回调
                // 设置自定义布局，添加停止会话按钮
                setCustomLayout(
                    listOf(
                        CommandButton.Builder(ICON_UNDEFINED)
                            .setCustomIconResId(coreUiR.drawable.ic_close)
                            .setDisplayName(getString(coreUiR.string.stop_player_session))
                            .setSessionCommand(CustomCommands.STOP_PLAYER_SESSION.sessionCommand)
                            .setEnabled(true)
                            .build(),
                    ),
                )
            }.build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 任务被移除时调用（如用户从最近任务列表中移除应用）
     * @param rootIntent 根意图
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player!!
        // 如果播放器不在播放、没有媒体项或播放已结束，则停止服务
        if (!player.playWhenReady || player.mediaItemCount == 0 || player.playbackState == Player.STATE_ENDED) {
            stopSelf()
        }
    }

    /**
     * 服务销毁时调用，清理资源
     */
    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.run {
            saveCurrentMediaPlaybackPosition(player) // 保存当前播放位置
            player.clearMediaItems() // 清空媒体项
            player.stop() // 停止播放
            player.removeListener(playbackStateListener) // 移除播放状态监听器
            player.release() // 释放播放器资源
            release() // 释放媒体会话资源
            mediaSession = null // 清空媒体会话引用
        }
        subtitleCacheDir.deleteFiles() // 删除字幕缓存文件
        serviceScope.cancel() // 取消服务协程作用域
    }

    /**
     * 保存当前媒体播放位置
     * @param player 播放器实例
     */
    private fun saveCurrentMediaPlaybackPosition(player: Player) {
        val mediaUri = player.currentMediaItem?.mediaId ?: return
        val mediaPosition = player.currentPosition

        serviceScope.launch {
            mediaRepository.updateMediumPosition(
                uri = mediaUri,
                position = mediaPosition,
            )
        }
    }

    /**
     * 更新媒体项的元数据和字幕配置
     * @param mediaItems 要更新的媒体项列表
     * @return 更新后的媒体项列表
     */
    private suspend fun updatedMediaItemsWithMetadata(
        mediaItems: List<MediaItem>,
    ): List<MediaItem> = supervisorScope {
        mediaItems.map { mediaItem ->
            async {
                val uri = mediaItem.mediaId.toUri()
                // 获取视频信息和状态
                val video = mediaRepository.getVideoByUri(uri = mediaItem.mediaId)
                val videoState = mediaRepository.getVideoState(uri = mediaItem.mediaId)

                // 设置媒体标题
                val title = mediaItem.mediaMetadata.title ?: video?.nameWithExtension ?: getFilenameFromUri(uri)
                // 设置媒体封面
                val artwork = video?.thumbnailPath?.toUri() ?: Uri.Builder().apply {
                    val defaultArtwork = R.drawable.artwork_default
                    scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    authority(resources.getResourcePackageName(defaultArtwork))
                    appendPath(resources.getResourceTypeName(defaultArtwork))
                    appendPath(resources.getResourceEntryName(defaultArtwork))
                }.build()

                // 获取外部字幕和本地字幕
                val externalSubs = videoState?.externalSubs ?: emptyList()
                val localSubs = (videoState?.path ?: getPath(uri))?.let {
                    File(it).getLocalSubtitles(
                        context = this@PlayerService,
                        excludeSubsList = externalSubs,
                    )
                } ?: emptyList()

                // 合并现有字幕配置和新字幕配置
                val existingSubConfigurations = mediaItem.localConfiguration?.subtitleConfigurations ?: emptyList()
                val subConfigurations = (localSubs + externalSubs).map { subtitleUri ->
                    uriToSubtitleConfiguration(
                        uri = subtitleUri,
                        subtitleEncoding = playerPreferences.subtitleTextEncoding,
                    )
                }

                // 构建更新后的媒体项
                mediaItem.buildUpon().apply {
                    setSubtitleConfigurations(existingSubConfigurations + subConfigurations)
                    setMediaMetadata(
                        MediaMetadata.Builder().apply {
                            setTitle(title)
                            setArtworkUri(artwork)
                        }.build(),
                    )
                }.build()
            }
        }.awaitAll()
    }
}

/**
 * Player 扩展属性：获取音频会话ID
 */
@get:UnstableApi
private val Player.audioSessionId: Int
    get() = when (this) {
        is ExoPlayer -> this.audioSessionId
        else -> C.AUDIO_SESSION_ID_UNSET
    }

/**
 * Player 扩展属性：获取/设置跳过静音功能
 */
@get:UnstableApi
@set:UnstableApi
private var Player.skipSilenceEnabled: Boolean
    @OptIn(UnstableApi::class)
    get() = when (this) {
        is ExoPlayer -> this.skipSilenceEnabled
        else -> false
    }
    set(value) {
        when (this) {
            is ExoPlayer -> this.skipSilenceEnabled = value
        }
    }
