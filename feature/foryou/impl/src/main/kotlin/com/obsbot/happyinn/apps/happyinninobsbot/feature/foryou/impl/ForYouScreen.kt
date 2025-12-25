/*
 * ForYouScreen.kt
 *
 * 这是应用的"为你推荐"(For You)屏幕实现文件，负责展示个性化的视频内容流和用户兴趣选择界面。
 * 主要功能包括：
 * 1. 显示用户感兴趣的视频内容网格布局
 * 2. 提供首次使用时的兴趣选择引导流程(Onboarding)
 * 3. 处理通知权限请求
 * 4. 处理深度链接跳转
 * 5. 显示加载状态和滚动条
 *
 * 该屏幕使用Compose框架构建，采用MVVM架构模式，与ForYouViewModel配合使用。
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.impl

import android.Manifest
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus.Denied
import com.google.accompanist.permissions.rememberPermissionState
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioIconToggleButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioOverlayLoadingWheel
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.scrollbar.DecorativeScrollbar
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.scrollbar.DraggableScrollbar
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.scrollbar.DynamicAsyncImage
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.scrollbar.scrollbarState
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.icon.HioIcons
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioTheme
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserVideosResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.DevicePreviews
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.TrackScreenViewEvent
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.UserVideosResourcePreviewParameterProvider
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.VideosFeedUiState
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.launchCustomChromeTab
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.videosFeed
import kotlin.collections.distinctBy
import kotlin.collections.flatMap
import com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.api.R



/**
 * ForYouScreen函数
 *
 * 这是"为你推荐"屏幕的主入口点，负责从ForYouViewModel获取状态并渲染UI。
 * 该函数使用ViewModel来管理数据流和业务逻辑，并通过内部ForYouScreen函数渲染实际UI。
 *
 * @param onTopicClick 当用户点击主题时的回调函数，接收主题ID作为参数
 * @param modifier 修饰符，用于自定义组件样式
 * @param viewModel ForYouViewModel实例，默认通过hiltViewModel()获取
 */
@Composable
fun ForYouScreen(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForYouViewModel = hiltViewModel(),
) {
    // 从ViewModel收集状态
    val onboardingUiState by viewModel.onboardingUiState.collectAsStateWithLifecycle()
    val feedState by viewModel.feedState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val deepLinkedUserVideosResource by viewModel.deepLinkedVideosResource.collectAsStateWithLifecycle()

    // 调用内部ForYouScreen函数，传入状态和回调
    ForYouScreen(
        isSyncing = isSyncing,
        onboardingUiState = onboardingUiState,
        feedState = feedState,
        deepLinkedUserVideosResource = deepLinkedUserVideosResource,
        onTopicCheckedChanged = viewModel::updateTopicSelection,
        onDeepLinkOpened = viewModel::onDeepLinkOpened,
        onTopicClick = onTopicClick,
        saveFollowedTopics = viewModel::dismissOnboarding,
        onVideosResourcesCheckedChanged = viewModel::updateVideosResourceSaved,
        onVideosResourceViewed = { viewModel.setVideosResourceViewed(it, true) },
        modifier = modifier,
    )
}

/**
 * 内部ForYouScreen函数
 *
 * 这是实际渲染UI的核心函数，接收所有必要的状态和回调函数作为参数。
 * 负责构建整个"为你推荐"屏幕的UI布局，包括内容网格、加载指示器和滚动条。
 *
 * @param isSyncing 表示是否正在同步数据的布尔值
 * @param onboardingUiState 引导流程的状态
 * @param feedState 视频内容流的状态
 * @param deepLinkedUserVideosResource 通过深度链接打开的视频资源
 * @param onTopicCheckedChanged 主题选择状态变更回调
 * @param onTopicClick 主题点击回调
 * @param onDeepLinkOpened 深度链接打开回调
 * @param saveFollowedTopics 保存用户关注主题的回调
 * @param onVideosResourcesCheckedChanged 视频资源收藏状态变更回调
 * @param onVideosResourceViewed 视频资源被查看的回调
 * @param modifier 修饰符
 */
@Composable
internal fun ForYouScreen(
    isSyncing: Boolean,
    onboardingUiState: OnboardingUiState,
    feedState: VideosFeedUiState,
    deepLinkedUserVideosResource: UserVideosResource?,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
    onTopicClick: (String) -> Unit,
    onDeepLinkOpened: (String) -> Unit,
    saveFollowedTopics: () -> Unit,
    onVideosResourcesCheckedChanged: (String, Boolean) -> Unit,
    onVideosResourceViewed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 判断各种加载状态
    val isOnboardingLoading = onboardingUiState is OnboardingUiState.Loading
    val isFeedLoading = feedState is VideosFeedUiState.Loading

    // 当UI完全准备好时报告渲染完成，用于性能监控
    ReportDrawnWhen { !isSyncing && !isOnboardingLoading && !isFeedLoading }

    // 计算可用的项目总数，用于滚动条显示
    val itemsAvailable = feedItemsSize(feedState, onboardingUiState)

    // 创建并配置交错网格状态和滚动条状态
    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(
        itemsAvailable = itemsAvailable,
    )
    //    TrackScrollJank(scrollableState = state, stateName = "forYou:feed")

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        // 垂直交错网格布局，用于显示内容
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(300.dp), // 自适应列宽，最小300dp
            contentPadding = PaddingValues(16.dp), // 内容内边距
            horizontalArrangement = Arrangement.spacedBy(16.dp), // 水平间距
            verticalItemSpacing = 24.dp, // 垂直间距
            modifier = Modifier
                .testTag("forYou:feed"), // 测试标签
            state = state,
        ) {
            // 添加引导流程部分
            onboarding(
                onboardingUiState = onboardingUiState,
                onTopicCheckedChanged = onTopicCheckedChanged,
                saveFollowedTopics = saveFollowedTopics,
                // 自定义布局修饰符，移除父容器的内容内边距限制，实现边缘到边缘滚动
                interestsItemModifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        constraints.copy(
                            maxWidth = constraints.maxWidth + 32.dp.roundToPx(),
                        ),
                    )
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                },
            )

            // 添加视频内容流部分
            videosFeed(
                feedState = feedState,
                onVideosResourcesCheckedChanged = onVideosResourcesCheckedChanged,
                onVideosResourceViewed = onVideosResourceViewed,
                onTopicClick = onTopicClick,
            )

            // 添加底部间距，确保内容不被系统UI遮挡
            item(span = StaggeredGridItemSpan.FullLine, contentType = "bottomSpacing") {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    // 为"离线"提示栏留出空间
                    // TODO: 检查Scaffold在HioApp中是否正确处理了这一点
                    // if (isOffline) Spacer(modifier = Modifier.height(48.dp))
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                }
            }
        }
        // 加载指示器，根据加载状态显示/隐藏
        AnimatedVisibility(
            visible = isSyncing || isFeedLoading || isOnboardingLoading,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
            ) + fadeOut(),
        ) {
            val loadingContentDescription = stringResource(id = R.string.feature_foryou_api_loading)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                HioOverlayLoadingWheel(
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentDesc = loadingContentDescription,
                )
            }
        }
        // 垂直拖动滚动条
        state.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }
    // 跟踪屏幕浏览事件
    TrackScreenViewEvent(screenName = "ForYou")
    // 处理通知权限请求
    NotificationPermissionEffect()
    // 处理深度链接
    DeepLinkEffect(
        deepLinkedUserVideosResource,
        onDeepLinkOpened,
    )
}

/**
 * 为LazyStaggeredGridScope扩展的onboarding函数
 *
 * 定义"为你推荐"屏幕的引导流程部分。根据onboardingUiState的状态，可能会渲染引导内容或不渲染任何内容。
 * 当用户首次启动应用时，显示兴趣选择界面，让用户选择感兴趣的主题。
 *
 * @param onboardingUiState 引导流程的状态，决定是否显示引导界面
 * @param onTopicCheckedChanged 当主题选择状态变更时的回调函数
 * @param saveFollowedTopics 保存用户选择的主题并关闭引导流程的回调函数
 * @param interestsItemModifier 自定义修饰符，用于调整兴趣选择区域的布局
 */
private fun LazyStaggeredGridScope.onboarding(
    onboardingUiState: OnboardingUiState,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
    saveFollowedTopics: () -> Unit,
    interestsItemModifier: Modifier = Modifier,
) {
    when (onboardingUiState) {
        // 加载中、加载失败或不显示引导流程时，不渲染任何内容
        OnboardingUiState.Loading,
        OnboardingUiState.LoadFailed,
        OnboardingUiState.NotShown,
            -> Unit

        // 显示引导流程时，渲染引导界面
        is OnboardingUiState.Shown -> {
            item(span = StaggeredGridItemSpan.FullLine, contentType = "onboarding") {
                Column(modifier = interestsItemModifier) {
                    // 引导标题
                    Text(
                        text = stringResource(R.string.feature_foryou_api_onboarding_guidance_title),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    // 引导副标题
                    Text(
                        text = stringResource(R.string.feature_foryou_api_onboarding_guidance_subtitle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 24.dp, end = 24.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    // 主题选择区域
                    TopicSelection(
                        onboardingUiState,
                        onTopicCheckedChanged,
                        Modifier.padding(bottom = 8.dp),
                    )
                    // 完成按钮
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        HioButton(
                            onClick = saveFollowedTopics,
                            enabled = onboardingUiState.isDismissable, // 当用户至少选择一个主题时启用
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .widthIn(364.dp)
                                .fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(R.string.feature_foryou_api_done),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * TopicSelection函数
 *
 * 渲染主题选择界面，允许用户从多个主题中选择感兴趣的内容。
 * 使用水平网格布局显示主题按钮，支持水平滚动和自定义高度以适应不同字体大小。
 *
 * @param onboardingUiState 包含可用主题列表的引导状态
 * @param onTopicCheckedChanged 当主题选择状态变更时的回调函数
 * @param modifier 修饰符，用于自定义组件样式
 */
@Composable
private fun TopicSelection(
    onboardingUiState: OnboardingUiState.Shown,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyGridState = rememberLazyGridState()
    val topicSelectionTestTag = "forYou:topicSelection"

//    TrackScrollJank(scrollableState = lazyGridState, stateName = topicSelectionTestTag)

    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        // 水平网格布局，用于显示主题按钮
        LazyHorizontalGrid(
            state = lazyGridState,
            rows = GridCells.Fixed(3), // 固定3行
            horizontalArrangement = Arrangement.spacedBy(12.dp), // 水平间距12dp
            verticalArrangement = Arrangement.spacedBy(12.dp), // 垂直间距12dp
            contentPadding = PaddingValues(24.dp), // 内容内边距24dp
            modifier = Modifier
                // 根据字体大小动态调整最大高度
                // 确保在不同字体缩放设置下都能正确显示内容
                .heightIn(max = max(240.dp, with(LocalDensity.current) { 240.sp.toDp() }))
                .fillMaxWidth()
                .testTag(topicSelectionTestTag),
        ) {
            // 渲染主题按钮列表
            items(
                items = onboardingUiState.topics,
                key = { it.topic.id }, // 使用主题ID作为唯一标识
            ) {
                SingleTopicButton(
                    name = it.topic.name,
                    topicId = it.topic.id,
                    imageUrl = it.topic.imageUrl,
                    isSelected = it.isFollowed,
                    onClick = onTopicCheckedChanged,
                )
            }
        }
        // 水平装饰性滚动条
        lazyGridState.DecorativeScrollbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .align(Alignment.BottomStart),
            state = lazyGridState.scrollbarState(itemsAvailable = onboardingUiState.topics.size),
            orientation = Orientation.Horizontal,
        )
    }
}

/**
 * SingleTopicButton函数
 *
 * 单个主题选择按钮组件，显示主题名称、图标和选择状态。
 * 点击按钮可以切换主题的选择状态。
 *
 * @param name 主题名称
 * @param topicId 主题ID
 * @param imageUrl 主题图标URL
 * @param isSelected 当前主题是否被选择
 * @param onClick 点击按钮时的回调函数
 */
@Composable
private fun SingleTopicButton(
    name: String,
    topicId: String,
    imageUrl: String,
    isSelected: Boolean,
    onClick: (String, Boolean) -> Unit,
) {
    // 使用Surface组件实现可点击的卡片效果
    Surface(
        modifier = Modifier
            .width(312.dp)
            .heightIn(min = 56.dp), // 最小高度56dp
        shape = RoundedCornerShape(corner = CornerSize(8.dp)), // 圆角8dp
        color = MaterialTheme.colorScheme.surface,
        selected = isSelected, // 根据是否选中应用不同样式
        onClick = {
            onClick(topicId, !isSelected) // 切换选择状态
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 8.dp),
        ) {
            // 主题图标
            TopicIcon(
                imageUrl = imageUrl,
            )
            // 主题名称
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
            )
            // 选择状态切换按钮
            HioIconToggleButton(
                checked = isSelected,
                onCheckedChange = { checked -> onClick(topicId, checked) },
                icon = { // 未选中状态图标
                    Icon(
                        imageVector = HioIcons.Add,
                        contentDescription = name,
                    )
                },
                checkedIcon = { // 选中状态图标
                    Icon(
                        imageVector = HioIcons.Check,
                        contentDescription = name,
                    )
                },
            )
        }
    }
}

/**
 * TopicIcon函数
 *
 * 用于显示主题图标的组件，使用DynamicAsyncImage异步加载网络图片。
 * 当图片加载中或加载失败时，会显示一个占位符图标。
 *
 * @param imageUrl 主题图标图片的URL地址
 * @param modifier 修饰符，用于自定义组件样式
 */
@Composable
fun TopicIcon(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    DynamicAsyncImage(
        placeholder = painterResource(R.drawable.feature_foryou_api_ic_icon_placeholder),
        imageUrl = imageUrl,
        // decorative - 纯装饰性图片，不需要内容描述以提高可访问性
        contentDescription = null,
        modifier = modifier
            .padding(10.dp)
            .size(32.dp),
    )
}

/**
 * NotificationPermissionEffect函数
 *
 * 处理通知权限请求的副作用函数。
 * 在Android Tiramisu及更高版本上自动请求POST_NOTIFICATIONS权限。
 * 权限请求只在应用处于非预览模式且未显示过权限提示时触发。
 */
@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun NotificationPermissionEffect() {
    // 权限请求只能在Activity Context中进行，预览模式下跳过
    if (LocalInspectionMode.current) return
    // 只在Android Tiramisu(33)及更高版本上请求通知权限
    if (VERSION.SDK_INT < VERSION_CODES.TIRAMISU) return

    // 初始化通知权限状态
    val notificationsPermissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS,
    )

    // 当权限状态变化时触发副作用
    LaunchedEffect(notificationsPermissionState) {
        val status = notificationsPermissionState.status
        // 只有在权限被拒绝且系统未提示用户为何需要此权限时才请求
        if (status is Denied && !status.shouldShowRationale) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}

/**
 * DeepLinkEffect函数
 *
 * 处理深度链接的副作用函数。
 * 当接收到通过深度链接打开的视频资源时，会自动打开Chrome自定义标签页访问视频内容。
 *
 * @param userVideosResource 通过深度链接接收到的用户视频资源，如果为null则不执行任何操作
 * @param onDeepLinkOpened 当深度链接被打开时的回调函数，用于标记该资源已被查看
 */
@Composable
private fun DeepLinkEffect(
    userVideosResource: UserVideosResource?,
    onDeepLinkOpened: (String) -> Unit,
) {
    // 获取当前上下文和主题背景色
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    // 当userVideosResource变化时触发副作用
    LaunchedEffect(userVideosResource) {
        // 如果没有视频资源，则不执行任何操作
        if (userVideosResource == null) return@LaunchedEffect

        // 标记视频资源为已查看（如果尚未标记）
        if (!userVideosResource.hasBeenViewed) onDeepLinkOpened(userVideosResource.id)

        // 使用Chrome自定义标签页打开视频URL
        launchCustomChromeTab(
            context = context,
            uri = Uri.parse(userVideosResource.url),
            toolbarColor = backgroundColor,
        )
    }
}

/**
 * feedItemsSize函数
 *
 * 计算交错网格布局中的项目总数，用于滚动条状态的正确显示。
 * 根据当前的视频内容状态和引导流程状态，动态计算可见的项目总数。
 *
 * @param feedState 视频内容流的状态，决定了内容项的数量
 * @param onboardingUiState 引导流程的状态，决定了是否包含引导项
 * @return 计算得到的项目总数
 */
private fun feedItemsSize(
    feedState: VideosFeedUiState,
    onboardingUiState: OnboardingUiState,
): Int {
    // 计算内容流中的项目数量：加载中状态返回0，成功状态返回feed.size
    val feedSize = when (feedState) {
        VideosFeedUiState.Loading -> 0
        is VideosFeedUiState.Success -> feedState.feed.size
    }
    // 计算引导流程中的项目数量：只有当显示引导流程时返回1，其他状态返回0
    val onboardingSize = when (onboardingUiState) {
        OnboardingUiState.Loading,
        OnboardingUiState.LoadFailed,
        OnboardingUiState.NotShown,
            -> 0

        is OnboardingUiState.Shown -> 1
    }
    // 返回总项目数 = 内容流项目数 + 引导流程项目数
    return feedSize + onboardingSize
}

@DevicePreviews
@Composable
fun ForYouScreenPopulatedFeed(
    @PreviewParameter(UserVideosResourcePreviewParameterProvider::class)
    userVideosResources: List<UserVideosResource>,
) {
    HioTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.NotShown,
            feedState = VideosFeedUiState.Success(
                feed = userVideosResources,
            ),
            deepLinkedUserVideosResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onVideosResourcesCheckedChanged = { _, _ -> },
            onVideosResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenOfflinePopulatedFeed(
    @PreviewParameter(UserVideosResourcePreviewParameterProvider::class)
    userVideosResources: List<UserVideosResource>,
) {
    HioTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.NotShown,
            feedState = VideosFeedUiState.Success(
                feed = userVideosResources,
            ),
            deepLinkedUserVideosResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onVideosResourcesCheckedChanged = { _, _ -> },
            onVideosResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenTopicSelection(
    @PreviewParameter(UserVideosResourcePreviewParameterProvider::class)
    userVideosResources: List<UserVideosResource>,
) {
    HioTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.Shown(
                topics = userVideosResources.flatMap { videos -> videos.followableTopics }
                    .distinctBy { it.topic.id },
            ),
            feedState = VideosFeedUiState.Success(
                feed = userVideosResources,
            ),
            deepLinkedUserVideosResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onVideosResourcesCheckedChanged = { _, _ -> },
            onVideosResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenLoading() {
    HioTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.Loading,
            feedState = VideosFeedUiState.Loading,
            deepLinkedUserVideosResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onVideosResourcesCheckedChanged = { _, _ -> },
            onVideosResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenPopulatedAndLoading(
    @PreviewParameter(UserVideosResourcePreviewParameterProvider::class)
    userVideosResources: List<UserVideosResource>,
) {
    HioTheme {
        ForYouScreen(
            isSyncing = true,
            onboardingUiState = OnboardingUiState.Loading,
            feedState = VideosFeedUiState.Success(
                feed = userVideosResources,
            ),
            deepLinkedUserVideosResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onVideosResourcesCheckedChanged = { _, _ -> },
            onVideosResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}




