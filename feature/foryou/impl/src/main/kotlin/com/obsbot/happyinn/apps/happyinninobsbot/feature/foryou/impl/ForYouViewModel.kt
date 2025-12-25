package com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.data.util.SyncManager
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsEvent
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsEvent.Param
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsHelper
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserVideosResourceRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.VideosResourceQuery
import com.obsbot.happyinn.apps.happyinninobsbot.core.domain.GetFollowableTopicsUseCase
import com.obsbot.happyinn.apps.happyinninobsbot.core.notifications.DEEP_LINK_VIDEOS_RESOURCE_ID_KEY
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.VideosFeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.firstOrNull

/**
 * ForYouViewModel - 为"为你推荐"页面提供状态管理和业务逻辑处理的ViewModel
 *
 * 负责管理视频推荐流、用户兴趣主题选择、首次使用引导、深度链接处理等功能的状态和操作
 * 采用MVVM架构模式，通过Flow和StateFlow提供响应式数据流
 */
@HiltViewModel
class ForYouViewModel @Inject constructor(
    // 用于存储和获取与当前界面相关的保存状态，如深度链接参数
    private val savedStateHandle: SavedStateHandle,
    // 同步管理器，用于检查应用数据同步状态
    syncManager: SyncManager,
    // 分析助手，用于记录用户行为事件
    private val analyticsHelper: AnalyticsHelper,
    // 用户数据仓库，用于读写用户偏好设置和状态
    private val userDataRepository: UserDataRepository,
    // 用户视频资源仓库，用于获取和管理视频推荐数据
    userVideosResourceRepository: UserVideosResourceRepository,
    // 获取可关注主题的用例，用于展示兴趣选择界面
    getFollowableTopics: GetFollowableTopicsUseCase,
) : ViewModel() {

    /**
     * 是否应该显示首次使用引导流程
     * 当用户数据中shouldHideOnboarding为false时显示引导
     */
    private val shouldShowOnboarding: Flow<Boolean> =
        userDataRepository.userData.map { !it.shouldHideOnboarding }

    /**
     * 从深度链接打开的视频资源
     * 当用户通过深度链接打开特定视频时，此StateFlow包含该视频的详细信息
     */
    val deepLinkedVideosResource = savedStateHandle.getStateFlow<String?>(
        key = DEEP_LINK_VIDEOS_RESOURCE_ID_KEY,
        null,
    )
        .flatMapLatest { videosResourceId ->
            if (videosResourceId == null) {
                flowOf(emptyList())
            } else {
                // 根据ID查询特定视频资源
                userVideosResourceRepository.observeAll(
                    VideosResourceQuery(
                        filterVideosIds = setOf(videosResourceId),
                    ),
                )
            }
        }
        .map { it.firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    /**
     * 应用数据同步状态
     * 用于显示加载指示器或同步提示
     */
    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /**
     * 视频推荐流状态
     * 根据用户关注的主题提供个性化视频推荐列表
     */
    val feedState: StateFlow<VideosFeedUiState> =
        userVideosResourceRepository.observeAllForFollowedTopics()
            .map(VideosFeedUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = VideosFeedUiState.Loading,
            )

    /**
     * 首次使用引导界面状态
     * 组合shouldShowOnboarding和可关注主题列表，决定引导界面的显示状态
     */
    val onboardingUiState: StateFlow<OnboardingUiState> =
        combine(
            shouldShowOnboarding,
            getFollowableTopics(),
        ) { shouldShowOnboarding, topics ->
            if (shouldShowOnboarding) {
                OnboardingUiState.Shown(topics = topics)
            } else {
                OnboardingUiState.NotShown
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = OnboardingUiState.Loading,
            )

    /**
     * 更新用户对主题的关注状态
     *
     * @param topicId 主题ID
     * @param isChecked 是否关注该主题
     */
    fun updateTopicSelection(topicId: String, isChecked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setTopicIdFollowed(topicId, isChecked)
        }
    }

    /**
     * 更新视频资源的收藏状态
     *
     * @param videosResourceId 视频资源ID
     * @param isChecked 是否收藏该视频
     */
    fun updateVideosResourceSaved(videosResourceId: String, isChecked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setVideosResourceBookmarked(videosResourceId, isChecked)
        }
    }

    /**
     * 更新视频资源的已观看状态
     *
     * @param videosResourceId 视频资源ID
     * @param viewed 是否已观看该视频
     */
    fun setVideosResourceViewed(videosResourceId: String, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setVideosResourceViewed(videosResourceId, viewed)
        }
    }

    /**
     * 处理深度链接打开视频的逻辑
     *
     * @param videosResourceId 通过深度链接打开的视频资源ID
     */
    fun onDeepLinkOpened(videosResourceId: String) {
        // 如果打开的视频正是当前深度链接指向的视频，则清除深度链接状态
        if (videosResourceId == deepLinkedVideosResource.value?.id) {
            savedStateHandle[DEEP_LINK_VIDEOS_RESOURCE_ID_KEY] = null
        }
        // 记录深度链接打开事件
        analyticsHelper.logVideosDeepLinkOpen(videosResourceId = videosResourceId)
        // 将打开的视频标记为已观看
        viewModelScope.launch {
            userDataRepository.setVideosResourceViewed(
                videosResourceId = videosResourceId,
                viewed = true,
            )
        }
    }

    /**
     * 关闭首次使用引导
     * 将用户偏好设置中的shouldHideOnboarding设为true
     */
    fun dismissOnboarding() {
        viewModelScope.launch {
            userDataRepository.setShouldHideOnboarding(true)
        }
    }
}

/**
 * AnalyticsHelper的扩展函数，用于记录视频深度链接打开事件
 *
 * @param videosResourceId 通过深度链接打开的视频资源ID
 */
private fun AnalyticsHelper.logVideosDeepLinkOpen(videosResourceId: String) =
    logEvent(
        AnalyticsEvent(
            type = "videos_deep_link_opened",
            extras = listOf(
                Param(
                    key = DEEP_LINK_VIDEOS_RESOURCE_ID_KEY,
                    value = videosResourceId,
                ),
            ),
        ),
    )