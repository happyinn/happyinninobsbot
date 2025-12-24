package com.obsbot.happyinn.apps.happyinninobsbot.core.ui

import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.LocalAnalyticsHelper
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioTheme
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserVideosResource

//TODO 待细看
/**
 * An extension on [LazyListScope] defining a feed with videos resources.
 * Depending on the [feedState], this might emit no items.
 */
fun LazyStaggeredGridScope.videosFeed(
    feedState: VideosFeedUiState,
    onVideosResourcesCheckedChanged: (String, Boolean) -> Unit,
    onVideosResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    onExpandedCardClick: () -> Unit = {},
) {
    when (feedState) {
        VideosFeedUiState.Loading -> Unit
        is VideosFeedUiState.Success -> {
            items(
                items = feedState.feed,
                key = { it.id },
                contentType = { "videosFeedItem" },
            ) { userVideosResource ->
                val context = LocalContext.current
                val analyticsHelper = LocalAnalyticsHelper.current
                val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

                VideosResourceCardExpanded(
                    userVideosResource = userVideosResource,
                    isBookmarked = userVideosResource.isSaved,
                    onClick = {
                        onExpandedCardClick()
                        analyticsHelper.logVideosResourceOpened(
                            videosResourceId = userVideosResource.id,
                        )
                        launchCustomChromeTab(context, Uri.parse(userVideosResource.url), backgroundColor)

                        onVideosResourceViewed(userVideosResource.id)
                    },
                    hasBeenViewed = userVideosResource.hasBeenViewed,
                    onToggleBookmark = {
                        onVideosResourcesCheckedChanged(
                            userVideosResource.id,
                            !userVideosResource.isSaved,
                        )
                    },
                    onTopicClick = onTopicClick,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItem(),
                )
            }
        }
    }
}

fun launchCustomChromeTab(context: Context, uri: Uri, @ColorInt toolbarColor: Int) {
    val customTabBarColor = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor).build()
    val customTabsIntent = CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(customTabBarColor)
        .build()

    customTabsIntent.launchUrl(context, uri)
}

/**
 * A sealed hierarchy describing the state of the feed of videos resources.
 */
sealed interface VideosFeedUiState {
    /**
     * The feed is still loading.
     */
    data object Loading : VideosFeedUiState

    /**
     * The feed is loaded with the given list of videos resources.
     */
    data class Success(
        /**
         * The list of videos resources contained in this feed.
         */
        val feed: List<UserVideosResource>,
    ) : VideosFeedUiState
}

@Preview
@Composable
private fun VideosFeedLoadingPreview() {
    HioTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            videosFeed(
                feedState = VideosFeedUiState.Loading,
                onVideosResourcesCheckedChanged = { _, _ -> },
                onVideosResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}

@Preview
@Preview(device = Devices.TABLET)
@Composable
private fun VideosFeedContentPreview(
    @PreviewParameter(UserVideosResourcePreviewParameterProvider::class)
    userVideosResources: List<UserVideosResource>,
) {
    HioTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            videosFeed(
                feedState = VideosFeedUiState.Success(userVideosResources),
                onVideosResourcesCheckedChanged = { _, _ -> },
                onVideosResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}
