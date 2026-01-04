/**
 * 媒体选择器ViewModel，负责处理媒体数据的获取、删除、重命名等逻辑
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.domain.GetSortedMediaUseCase
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Folder
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaConfig
import com.obsbot.happyinn.apps.happyinninobsbot.core.service.media.MediaInfoSynchronizer
import com.obsbot.happyinn.apps.happyinninobsbot.core.service.media.MediaService
import com.obsbot.happyinn.apps.happyinninobsbot.sync.media.MediaSynchronizer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 媒体选择器ViewModel，负责处理媒体数据的获取、删除、重命名等逻辑
 * @param getSortedMediaUseCase 获取排序后媒体的用例
 * @param mediaService 媒体服务，用于处理媒体文件操作
 * @param userDataRepository 偏好设置仓库，用于获取和更新应用偏好
 * @param mediaInfoSynchronizer 媒体信息同步器，用于同步媒体信息
 * @param mediaSynchronizer 媒体同步器，用于同步媒体文件
 */
@HiltViewModel
class VideosViewModel @Inject constructor(
    getSortedMediaUseCase: GetSortedMediaUseCase,
    private val mediaService: MediaService,
    private val userDataRepository: UserDataRepository,
    private val mediaInfoSynchronizer: MediaInfoSynchronizer,
    private val mediaSynchronizer: MediaSynchronizer,
) : ViewModel() {

    /**
     * 内部UI状态，使用可变状态流
     */
    private val uiStateInternal = MutableStateFlow(MediaPickerUiState())

    /**
     * 对外暴露的UI状态，使用不可变状态流
     */
    val uiState = uiStateInternal.asStateFlow()

    /**
     * 媒体状态，从用例获取排序后的媒体数据
     * 初始状态为加载中，当有订阅者且活跃时开始数据流，订阅者消失5秒后停止
     */
    val mediaState = getSortedMediaUseCase.invoke()
        .map { MediaState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MediaState.Loading,
        )

    /**
     * 应用偏好设置，从偏好设置仓库获取
     * 初始状态为默认应用偏好，当有订阅者且活跃时开始数据流，订阅者消失5秒后停止
     */
    val preferences : StateFlow<UserData> = userDataRepository.userData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserData.DEFAULT,
        )

    /**
     * 更新媒体设置偏好设置
     * @param mediaConfig 新的应用偏好设置
     */
    //更新配置
    fun updateMediaConfig(mediaConfig: MediaConfig) {
        viewModelScope.launch {
            userDataRepository.updateMediaConfig(mediaConfig)
        }
    }

    fun updatePreferences(userData: UserData) {
        val mediaConfig = MediaConfig(
            sortBy = userData.sortBy,
            sortOrder = userData.sortOrder,
            showFloatingPlayButton = userData.showFloatingPlayButton,
            excludeFolders = userData.excludeFolders,
            mediaViewMode = userData.mediaViewMode,
            mediaLayoutMode = userData.mediaLayoutMode,
            showDurationField = userData.showDurationField,
            showExtensionField = userData.showExtensionField,
            showPathField = userData.showPathField,
            showResolutionField = userData.showResolutionField,
            showSizeField = userData.showSizeField,
            showThumbnailField = userData.showThumbnailField,
            showPlayedProgress = userData.showPlayedProgress,
        )
        updateMediaConfig(mediaConfig)
    }

    /**
     * 删除视频文件
     * @param uris 要删除的视频URI字符串列表
     */
    fun deleteVideos(uris: List<String>) {
        viewModelScope.launch {
            mediaService.deleteMedia(uris.map { Uri.parse(it) })
        }
    }

    /**
     * 删除文件夹及其包含的所有媒体文件
     * @param folders 要删除的文件夹列表
     */
    fun deleteFolders(folders: List<Folder>) {
        viewModelScope.launch {
            val uris = folders.flatMap { folder ->
                folder.allMediaList.mapNotNull {
                    Uri.parse(it.uriString)
                }
            }
            mediaService.deleteMedia(uris)
        }
    }

    /**
     * 将媒体添加到媒体信息同步器
     * @param uri 要添加的媒体URI
     */
    fun addToMediaInfoSynchronizer(uri: Uri) {
        viewModelScope.launch {
            mediaInfoSynchronizer.addMedia(uri)
        }
    }

    /**
     * 重命名视频文件
     * @param uri 要重命名的视频URI
     * @param to 新的文件名
     */
    fun renameVideo(uri: Uri, to: String) {
        viewModelScope.launch {
            mediaService.renameMedia(uri, to)
        }
    }

    /**
     * 处理刷新点击事件
     * 更新UI状态为刷新中，调用媒体同步器刷新，然后更新UI状态为刷新完成
     */
    fun onRefreshClicked() {
        viewModelScope.launch {
            uiStateInternal.update { it.copy(refreshing = true) }
            mediaSynchronizer.refresh()
            uiStateInternal.update { it.copy(refreshing = false) }
        }
    }
}

/**
 * 媒体选择器UI状态数据类
 * @param refreshing 是否正在刷新
 */
data class MediaPickerUiState(
    val refreshing: Boolean = false,
)
