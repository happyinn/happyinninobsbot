package com.obsbot.happyinn.apps.happyinninobsbot.core.ui

import android.content.ClipData
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioIconToggleButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioTopicTag
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.icon.HioIcons
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioTheme
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.FollowableTopic
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserNewsResource
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.text.isNotBlank
import kotlin.text.isNullOrEmpty
import kotlin.text.uppercase
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.R.drawable

/**
 * 新闻资源卡片组件文件
 * 
 * 该文件定义了新闻资源卡片相关的Composable组件，主要用于在应用中展示新闻资源信息，
 * 包括展开式卡片、标题、头部图片、元数据、简短描述和主题标签等元素�?
 * 这些组件主要用于"为你推荐"�?已保�?等页面中�?
 */

/**
 * 展开式新闻资源卡片组�?
 * 
 * 显示一个包含完整新闻资源信息的卡片，包括标题、头部图片、书签按钮、元数据�?
 * 简短描述和主题标签。支持点击操作、书签切换和拖拽分享功能�?
 * 
 * @param userNewsResource 包含新闻资源数据的用户新闻资源对�?
 * @param isBookmarked 是否已添加书�?
 * @param hasBeenViewed 是否已查�?
 * @param onToggleBookmark 切换书签状态的回调函数
 * @param onClick 点击卡片的回调函�?
 * @param onTopicClick 点击主题标签的回调函�?
 * @param modifier 修饰�?
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsResourceCardExpanded(
    userNewsResource: UserNewsResource,
    isBookmarked: Boolean,
    hasBeenViewed: Boolean,
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 定义可访问性标签和分享内容
    val clickActionLabel = stringResource(R.string.core_ui_card_tap_action)
    val sharingLabel = stringResource(R.string.core_ui_feed_sharing)
    val sharingContent = stringResource(
        R.string.core_ui_feed_sharing_data,
        userNewsResource.title,
        userNewsResource.url,
    )

    // 设置拖拽标志，根据Android版本适配
    val dragAndDropFlags = if (VERSION.SDK_INT >= VERSION_CODES.N) {
        View.DRAG_FLAG_GLOBAL
    } else {
        0
    }

    // 卡片组件
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        // 使用自定义标签为无障碍服务提供按钮动作的描述
        modifier = modifier
            .semantics {
                onClick(label = clickActionLabel, action = null)
            }
            .testTag("NewsResourceCard:${userNewsResource.id}"),
    ) {
        //整个是一层纵向布局的容器
        Column {
            //顶部图片
            // 如果新闻资源有头部图片URL，则显示头部图片
            if (!userNewsResource.headerImageUrl.isNullOrEmpty()) {
                Row {
                    NewsResourceHeaderImage(userNewsResource.headerImageUrl)
                }
            }
            // 下部内容
            Box(
                modifier = Modifier.padding(16.dp),
            ) {
                //又是一个纵向布局
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    //显示一行标题和收藏按钮
                    Row {
                        // 显示新闻标题，并支持拖拽分享功能
                        NewsResourceTitle(
                            userNewsResource.title,
                            modifier = Modifier
                                .fillMaxWidth((.8f))
                                .dragAndDropSource { _ ->
                                    DragAndDropTransferData(
                                        ClipData.newPlainText(
                                            sharingLabel,
                                            sharingContent,
                                        ),
                                        flags = dragAndDropFlags,
                                    )
                                },
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        // 显示书签按钮
                        BookmarkButton(isBookmarked, onToggleBookmark)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 如果新闻未被查看，显示未读指示器
                        if (!hasBeenViewed) {
                            NotificationDot(
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(8.dp),
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                        }
                        // 显示新闻发布日期和类�?
                        NewsResourceMetaData(userNewsResource.publishDate, userNewsResource.type)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    // 显示新闻简短描�?
                    NewsResourceShortDescription(userNewsResource.content)
                    Spacer(modifier = Modifier.height(12.dp))
                    // 显示新闻主题标签列表
                    NewsResourceTopics(
                        topics = userNewsResource.followableTopics,
                        onTopicClick = onTopicClick,
                    )
                }
            }
        }
    }
}

/**
 * 新闻资源头部图片组件
 * 
 * 显示新闻资源的头部图片，支持加载状态、错误状态和占位图显示�?
 * 使用Coil库异步加载图片，并提供加载指示器�?
 * 
 * @param headerImageUrl 头部图片的URL，如果为null则显示占位图
 */
@Composable
fun NewsResourceHeaderImage(
    headerImageUrl: String?,
) {
    // 加载状态和错误状�?
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    
    // 使用Coil库的AsyncImagePainter加载图片
    val imageLoader = rememberAsyncImagePainter(
        model = headerImageUrl,
        onState = { state ->
            isLoading = state is AsyncImagePainter.State.Loading
            isError = state is AsyncImagePainter.State.Error
        },
    )
    
    // 检查是否在预览模式
    val isLocalInspection = LocalInspectionMode.current
    
    // 图片容器
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center,
    ) {
        // 加载中显示进度指示器
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(80.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

        // 显示图片或占位图
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop,
            painter = if (isError.not() && !isLocalInspection) {
                imageLoader
            } else {
                painterResource(drawable.core_designsystem_ic_placeholder_default)
            },
            // 装饰性图片，无需内容描述
            contentDescription = null,
        )
    }
}

/**
 * 新闻资源标题组件
 * 
 * 显示新闻资源的标题文本，使用适当的样式格式化�?
 * 
 * @param newsResourceTitle 新闻资源的标题文�?
 * @param modifier 修饰�?
 */
@Composable
fun NewsResourceTitle(
    newsResourceTitle: String,
    modifier: Modifier = Modifier,
) {
    Text(newsResourceTitle, style = MaterialTheme.typography.headlineSmall, modifier = modifier)
}

/**
 * 书签按钮组件
 * 
 * 显示一个可切换书签状态的图标按钮，支持选中和未选中两种状态�?
 * 
 * @param isBookmarked 是否已添加书�?
 * @param onClick 点击按钮的回调函�?
 * @param modifier 修饰�?
 */
@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HioIconToggleButton(
        checked = isBookmarked,
        onCheckedChange = { onClick() },
        modifier = modifier,
        icon = {
            Icon(
                imageVector = HioIcons.BookmarkBorder,
                contentDescription = stringResource(R.string.core_ui_bookmark),
            )
        },
        checkedIcon = {
            Icon(
                imageVector = HioIcons.Bookmark,
                contentDescription = stringResource(R.string.core_ui_unbookmark),
            )
        },
    )
}

/**
 * 通知点组�?
 * 
 * 显示一个小的圆形点，用于表示未读状态�?
 * 
 * @param color 通知点的颜色
 * @param modifier 修饰�?
 */
@Composable
fun NotificationDot(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(R.string.core_ui_unread_resource_dot_content_description)
    Canvas(
        modifier = modifier
            .semantics { contentDescription = description },
        onDraw = {
            drawCircle(
                color,
                radius = size.minDimension / 2,
            )
        },
    )
}

/**
 * 日期格式化工具函�?
 * 
 * 将Instant类型的日期格式化为本地日期字符串，使用中等长度的本地日期格式�?
 * 
 * @param publishDate 要格式化的发布日�?
 * @return 格式化后的日期字符串
 */
@Composable
fun dateFormatted(publishDate: Instant): String = DateTimeFormatter
    .ofLocalizedDate(FormatStyle.MEDIUM)
    .withLocale(Locale.getDefault())
    .withZone(LocalTimeZone.current.toJavaZoneId())
    .format(publishDate.toJavaInstant())

/**
 * 新闻资源元数据组�?
 * 
 * 显示新闻资源的发布日期和类型信息�?
 * 
 * @param publishDate 发布日期
 * @param resourceType 资源类型
 */
@Composable
fun NewsResourceMetaData(
    publishDate: Instant,
    resourceType: String,
) {
    val formattedDate = dateFormatted(publishDate)
    Text(
        if (resourceType.isNotBlank()) {
            stringResource(R.string.core_ui_card_meta_data_text, formattedDate, resourceType)
        } else {
            formattedDate
        },
        style = MaterialTheme.typography.labelSmall,
    )
}

/**
 * 新闻资源简短描述组�?
 * 
 * 显示新闻资源的简短描述文本�?
 * 
 * @param newsResourceShortDescription 新闻资源的简短描�?
 */
@Composable
fun NewsResourceShortDescription(
    newsResourceShortDescription: String,
) {
    Text(newsResourceShortDescription, style = MaterialTheme.typography.bodyLarge)
}

/**
 * 新闻资源主题标签组件
 * 
 * 显示新闻资源的主题标签列表，支持水平滚动和点击操作�?
 * 
 * @param topics 可关注的主题列表
 * @param onTopicClick 点击主题标签的回调函�?
 * @param modifier 修饰�?
 */
@Composable
fun NewsResourceTopics(
    topics: List<FollowableTopic>,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        // 水平滚动以适应可能较长的标签列�?
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // 遍历主题列表，为每个主题创建一个标�?
        for (followableTopic in topics) {
            HioTopicTag(
                followed = followableTopic.isFollowed,
                onClick = { onTopicClick(followableTopic.topic.id) },
                text = {
                    // 为可访问性服务提供标签内容描�?
                    val contentDescription = if (followableTopic.isFollowed) {
                        stringResource(
                            R.string.core_ui_topic_chip_content_description_when_followed,
                            followableTopic.topic.name,
                        )
                    } else {
                        stringResource(
                            R.string.core_ui_topic_chip_content_description_when_not_followed,
                            followableTopic.topic.name,
                        )
                    }
                    Text(
                        text = followableTopic.topic.name.uppercase(Locale.getDefault()),
                        modifier = Modifier
                            .semantics {
                                this.contentDescription = contentDescription
                            }
                            .testTag("topicTag:${followableTopic.topic.id}"),
                    )
                },
            )
        }
    }
}

/**
 * 书签按钮预览
 * 
 * 用于在Android Studio预览中显示未选中状态的书签按钮�?
 */
@Preview("Bookmark Button")
@Composable
private fun BookmarkButtonPreview() {
    HioTheme {
        Surface {
            BookmarkButton(isBookmarked = false, onClick = { })
        }
    }
}

/**
 * 已选中书签按钮预览
 * 
 * 用于在Android Studio预览中显示已选中状态的书签按钮�?
 */
@Preview("Bookmark Button Bookmarked")
@Composable
private fun BookmarkButtonBookmarkedPreview() {
    HioTheme {
        Surface {
            BookmarkButton(isBookmarked = true, onClick = { })
        }
    }
}

/**
 * 展开式新闻资源卡片预�?
 * 
 * 用于在Android Studio预览中显示完整的展开式新闻资源卡片�?
 */
@Preview("NewsResourceCardExpanded")
@Composable
private fun ExpandedNewsResourcePreview(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    CompositionLocalProvider(
        LocalInspectionMode provides true,
    ) {
        HioTheme {
            Surface {
                NewsResourceCardExpanded(
                    userNewsResource = userNewsResources[0],
                    isBookmarked = true,
                    hasBeenViewed = false,
                    onToggleBookmark = {},
                    onClick = {},
                    onTopicClick = {},
                )
            }
        }
    }
}
