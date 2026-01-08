/**
 * 媒体文件夹选择器ViewModel，负责处理特定文件夹中媒体数据的获取、删除、重命名等逻辑
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.screens.mediaFolder

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.media.PreferencesRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.domain.GetSortedMediaUseCase
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Folder
import com.obsbot.happyinn.apps.happyinninobsbot.core.service.media.MediaInfoSynchronizer
import com.obsbot.happyinn.apps.happyinninobsbot.core.service.media.MediaService
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.api.navigation.FolderNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.screens.MediaState
import com.obsbot.happyinn.apps.happyinninobsbot.sync.media.MediaSynchronizer
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 媒体文件夹选择器ViewModel，处理特定文件夹中媒体数据的获取、删除、重命名等逻辑
 * @param getSortedMediaUseCase 获取排序后媒体的用例
 * @param savedStateHandle 用于保存和恢复 UI 状态
 * @param mediaService 媒体服务，用于处理媒体文件操作
 * @param preferencesRepository 偏好设置仓库，用于获取和更新应用偏好
 * @param mediaInfoSynchronizer 媒体信息同步器，用于同步媒体信息
 * @param mediaSynchronizer 媒体同步器，用于同步媒体文件
 */
@HiltViewModel(assistedFactory = MediaPickerFolderViewModel.Factory::class)
class MediaPickerFolderViewModel @AssistedInject constructor(
    getSortedMediaUseCase: GetSortedMediaUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val mediaService: MediaService,
    private val preferencesRepository: PreferencesRepository,
    private val mediaInfoSynchronizer: MediaInfoSynchronizer,
    private val mediaSynchronizer: MediaSynchronizer,
    private val userDataRepository: UserDataRepository,
    // 导航传入的 FolderNavKey，包含当前文件夹路径
    @Assisted val key: FolderNavKey,
) : ViewModel() {

    /**
     * 当前文件夹路径
     */
    val folderPath: String = key.folderPath

    /**
     * 内部UI状态，使用可变状态流
     */
    private val uiStateInternal = MutableStateFlow(MediaPickerFolderUiState())
    
    /**
     * 对外暴露的UI状态，使用不可变状态流
     */
    val uiState = uiStateInternal.asStateFlow()

    /**
     * 媒体状态，从用例获取指定文件夹的媒体数据
     * 初始状态为加载中，当有订阅者且活跃时开始数据流，订阅者消失5秒后停止
     */
    val mediaState = getSortedMediaUseCase.invoke(folderPath)
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
    val preferences = userDataRepository.userData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserData.DEFAULT
        )

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
     * 删除文件夹及其包含的所有媒体文件
     * @param folders 要删除的文件夹列表
     */
    fun deleteFolders(folders: List<Folder>) {
        viewModelScope.launch {
            val uris = folders.flatMap { folder ->
                folder.allMediaList.mapNotNull { video ->
                    Uri.parse(video.uriString)
                }
            }
            mediaService.deleteMedia(uris)
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

    /**
     * 媒体文件夹选择器 ViewModel 的 Assisted Factory
     * 通过 NavKey 创建对应的 ViewModel 实例
     */
    @AssistedFactory
    interface Factory {
        fun create(key: FolderNavKey): MediaPickerFolderViewModel
    }
}



/**
 * 媒体文件夹选择器UI状态数据类
 * @param refreshing 是否正在刷新
 */
data class MediaPickerFolderUiState(
    val refreshing: Boolean = false,
)
