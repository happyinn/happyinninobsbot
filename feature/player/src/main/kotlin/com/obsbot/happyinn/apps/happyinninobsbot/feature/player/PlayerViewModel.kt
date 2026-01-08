package com.obsbot.happyinn.apps.happyinninobsbot.feature.player

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.model.VideoState
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.media.MediaRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.media.PreferencesRepository

import com.obsbot.happyinn.apps.happyinninobsbot.core.domain.GetSortedPlaylistUseCase
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.LoopMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Video
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.VideoZoom

import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 播放器视图模型
 * 
 * 负责管理视频播放器的状态和数据，包括播放列表、播放设置和用户偏好
 * 使用 Hilt 进行依赖注入，提供给 com.obsbot.happyinn.apps.happyinninobsbot.feature.player.PlayerActivity 使用
 * 
 * @property mediaRepository 媒体仓库，用于获取和更新视频状态
 * @property preferencesRepository 偏好设置仓库，用于获取和更新播放器偏好
 * @property getSortedPlaylistUseCase 获取排序播放列表的用例
 * @property userDataRepository 用户数据仓库，用于获取应用偏好设置
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val preferencesRepository: PreferencesRepository,
    private val getSortedPlaylistUseCase: GetSortedPlaylistUseCase,
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    /** 是否准备好播放，默认为 true */
    var playWhenReady: Boolean = true
    
    /** 是否跳过静音片段，默认为 false */
    var skipSilenceEnabled: Boolean = false

    /**
     * 播放器偏好设置
     * 
     * 从偏好设置仓库获取，使用 ViewModel 作用域和立即启动策略
     */
    val playerPrefs = preferencesRepository.playerPreferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = runBlocking { preferencesRepository.playerPreferences.first() },
    )

    /**
     * 应用偏好设置
     * 
     * 从用户数据仓库获取，使用 ViewModel 作用域和立即启动策略
     */
    val appPrefs = userDataRepository.userData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UserData.DEFAULT
    )

    /**
     * 从 URI 获取播放列表
     * 
     * @param uri 视频 URI
     * @return 排序后的视频列表
     */
    suspend fun getPlaylistFromUri(uri: Uri): List<Video> {
        return getSortedPlaylistUseCase.invoke(uri)
    }

    /**
     * 获取视频状态
     * 
     * @param uri 视频 URI 字符串
     * @return 视频状态，如果不存在则返回 null
     */
    suspend fun getVideoState(uri: String): VideoState? {
        return mediaRepository.getVideoState(uri)
    }

    /**
     * 更新视频缩放比例
     * 
     * @param uri 视频 URI 字符串
     * @param zoom 缩放比例
     */
    fun updateMediumZoom(uri: String, zoom: Float) {
        viewModelScope.launch {
            mediaRepository.updateMediumZoom(uri, zoom)
        }
    }

    /**
     * 设置播放器亮度
     * 
     * @param value 亮度值（0.0-1.0）
     */
    fun setPlayerBrightness(value: Float) {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences { it.copy(playerBrightness = value) }
        }
    }

    /**
     * 设置视频缩放模式
     * 
     * @param videoZoom 视频缩放模式
     */
    fun setVideoZoom(videoZoom: VideoZoom) {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences { it.copy(playerVideoZoom = videoZoom) }
        }
    }

    /**
     * 设置循环播放模式
     * 
     * @param loopMode 循环播放模式
     */
    fun setLoopMode(loopMode: LoopMode) {
        viewModelScope.launch {
            preferencesRepository.updatePlayerPreferences { it.copy(loopMode = loopMode) }
        }
    }
}