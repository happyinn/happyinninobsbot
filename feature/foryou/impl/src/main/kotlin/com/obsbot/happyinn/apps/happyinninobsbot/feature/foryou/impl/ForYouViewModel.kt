package com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.util.SyncManager
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsEvent
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsEvent.Param
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsHelper
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserNewsResourceRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.NewsResourceQuery
import com.obsbot.happyinn.apps.happyinninobsbot.core.domain.GetFollowableTopicsUseCase
import com.obsbot.happyinn.apps.happyinninobsbot.core.notifications.DEEP_LINK_News_RESOURCE_ID_KEY
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.news.NewsFeedUiState
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
 * ForYouViewModel - 态为你推荐"页面提供状态管理和业务逻辑处理的ViewModel
 *
 * 负责管理新闻推荐流、用户兴趣主题选择、首次使用引导、深度链接处理等功能的状态和操作
 * 采用MVVM架构模式，通过Flow和StateFlow提供响应式数据流
 */
@HiltViewModel
class ForYouViewModel @Inject constructor(
    // 用于存储和获取与当前界面相关的保存状态，如深度链接参态
    private val savedStateHandle: SavedStateHandle,
    // 同步管理器，用于检查应用数据同步状态
    syncManager: SyncManager,
    // 分析助手，用于记录用户行为事态
    private val analyticsHelper: AnalyticsHelper,
    // 用户数据仓库，用于读写用户偏好设置和状态
    private val userDataRepository: UserDataRepository,
    // 用户新闻资源仓库，用于获取和管理新闻推荐数据
    userNewsResourceRepository: UserNewsResourceRepository, //TODO 这里有歧义，没有通过domain的case层封装。
    // 获取可关注主题的用例，用于展示兴趣选择界面
    getFollowableTopics: GetFollowableTopicsUseCase,
) : ViewModel() {

    /**
     * 是否应该显示首次使用引导流程
     * 当用户数据中shouldHideOnboarding为false时显示引态
     */
    private val shouldShowOnboarding: Flow<Boolean> =
        userDataRepository.userData.map { !it.shouldHideOnboarding }

    /**
     * 从深度链接打开的新闻资态
     * 当用户通过深度链接打开特定新闻时，此StateFlow包含该新闻的详细信息
     */
    val deepLinkedNewsResource = savedStateHandle.getStateFlow<String?>(
        key = DEEP_LINK_News_RESOURCE_ID_KEY,
        null,
    )
        .flatMapLatest { newsResourceId ->
            if (newsResourceId == null) {
                flowOf(emptyList())
            } else {
                // 根据ID查询特定新闻资源
                userNewsResourceRepository.observeAll(
                    NewsResourceQuery(
                        filterNewsIds = setOf(newsResourceId),
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
     * 新闻推荐流状态
     * 根据用户关注的主题提供个性化新闻推荐列表
     */
    val feedState: StateFlow<NewsFeedUiState> =
        userNewsResourceRepository.observeAllForFollowedTopics()
            .map(NewsFeedUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NewsFeedUiState.Loading,
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
     * @param isChecked 是否关注该主态
     */
    fun updateTopicSelection(topicId: String, isChecked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setTopicIdFollowed(topicId, isChecked)
        }
    }

    /**
     * 更新新闻资源的收藏状态
     *
     * @param newsResourceId 新闻资源ID
     * @param isChecked 是否收藏该视态
     */
    fun updateNewsResourceSaved(newsResourceId: String, isChecked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceBookmarked(newsResourceId, isChecked)
        }
    }

    /**
     * 更新新闻资源的已观看状态
     *
     * @param newsResourceId 新闻资源ID
     * @param viewed 是否已观看该新闻
     */
    fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceViewed(newsResourceId, viewed)
        }
    }

    /**
     * 处理深度链接打开新闻的逻辑
     *
     * @param newsResourceId 通过深度链接打开的新闻资源ID
     */
    fun onDeepLinkOpened(newsResourceId: String) {
        // 如果打开的新闻正是当前深度链接指向的新闻，则清除深度链接状态
        if (newsResourceId == deepLinkedNewsResource.value?.id) {
            savedStateHandle[DEEP_LINK_News_RESOURCE_ID_KEY] = null
        }
        // 记录深度链接打开事件
        analyticsHelper.logNewsDeepLinkOpen(newsResourceId = newsResourceId)
        // 将打开的新闻标记为已观态
        viewModelScope.launch {
            userDataRepository.setNewsResourceViewed(
                newsResourceId = newsResourceId,
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
 * AnalyticsHelper的扩展函数，用于记录新闻深度链接打开事件
 *
 * @param newsResourceId 通过深度链接打开的新闻资源ID
 */
private fun AnalyticsHelper.logNewsDeepLinkOpen(newsResourceId: String) =
    logEvent(
        AnalyticsEvent(
            type = "News_deep_link_opened",
            extras = listOf(
                Param(
                    key = DEEP_LINK_News_RESOURCE_ID_KEY,
                    value = newsResourceId,
                ),
            ),
        ),
    )
