/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
//TODO 待细看
@HiltViewModel
class ForYouViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    syncManager: SyncManager,
    private val analyticsHelper: AnalyticsHelper,
    private val userDataRepository: UserDataRepository,
    userVideosResourceRepository: UserVideosResourceRepository,
    getFollowableTopics: GetFollowableTopicsUseCase,
) : ViewModel() {

    private val shouldShowOnboarding: Flow<Boolean> =
        userDataRepository.userData.map { !it.shouldHideOnboarding }

    val deepLinkedVideosResource = savedStateHandle.getStateFlow<String?>(
        key = DEEP_LINK_VIDEOS_RESOURCE_ID_KEY,
        null,
    )
        .flatMapLatest { videosResourceId ->
            if (videosResourceId == null) {
                flowOf(emptyList())
            } else {
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

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val feedState: StateFlow<VideosFeedUiState> =
        userVideosResourceRepository.observeAllForFollowedTopics()
            .map(VideosFeedUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = VideosFeedUiState.Loading,
            )

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

    fun updateTopicSelection(topicId: String, isChecked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setTopicIdFollowed(topicId, isChecked)
        }
    }

    fun updateVideosResourceSaved(videosResourceId: String, isChecked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setVideosResourceBookmarked(videosResourceId, isChecked)
        }
    }

    fun setVideosResourceViewed(videosResourceId: String, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setVideosResourceViewed(videosResourceId, viewed)
        }
    }

    fun onDeepLinkOpened(videosResourceId: String) {
        if (videosResourceId == deepLinkedVideosResource.value?.id) {
            savedStateHandle[DEEP_LINK_VIDEOS_RESOURCE_ID_KEY] = null
        }
        analyticsHelper.logVideosDeepLinkOpen(videosResourceId = videosResourceId)
        viewModelScope.launch {
            userDataRepository.setVideosResourceViewed(
                videosResourceId = videosResourceId,
                viewed = true,
            )
        }
    }

    fun dismissOnboarding() {
        viewModelScope.launch {
            userDataRepository.setShouldHideOnboarding(true)
        }
    }
}

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
