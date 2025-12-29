package com.obsbot.happyinn.apps.happyinninobsbot.core.notifications

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.InboxStyle
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.NewsResource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
//TODO 待细�?
// 通知相关常量定义
// 最大通知数量限制
private const val MAX_NUM_NOTIFICATIONS = 5
// 目标Activity名称（点击通知打开的Activity�?
private const val TARGET_ACTIVITY_NAME = "com.obsbot.happyinn.apps.happyinninobsbot.MainActivity"
// 新闻通知请求�?
private const val News_NOTIFICATION_REQUEST_CODE = 0
// 新闻通知摘要ID
private const val News_NOTIFICATION_SUMMARY_ID = 1
// 新闻通知渠道ID
private const val News_NOTIFICATION_CHANNEL_ID = ""
// 新闻通知组ID
private const val News_NOTIFICATION_GROUP = "News_NOTIFICATIONS"
// 深度链接的协议和主机�?
private const val DEEP_LINK_SCHEME_AND_HOST = "https://www.nowinandroid.apps.samples.google.com"
// 深度链接的路径部�?
private const val DEEP_LINK_FOR_YOU_PATH = "foryou"
// 深度链接的基础路径
private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$DEEP_LINK_FOR_YOU_PATH"
// 深度链接中新闻资源ID的键�?
const val DEEP_LINK_News_RESOURCE_ID_KEY = "linkedNewsResourceId"
// 深度链接URI模式
const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_BASE_PATH/{$DEEP_LINK_News_RESOURCE_ID_KEY}"

/**
 * [Notifier]接口的系统托盘通知实现�?
 * 
 * 负责在Android系统托盘中显示通知，特别是新闻资源相关的通知�?
 * 支持批量通知管理、通知分组以及点击通知后的深度链接导航�?
 */
@Singleton
internal class SystemTrayNotifier @Inject constructor(
    // 注入ApplicationContext，用于访问系统服务和资源
    @ApplicationContext private val context: Context,
) : Notifier {

    /**
     * 发布新闻资源通知
     * 
     * @param newsResources 新闻资源列表，将为每个资源创建一个通知
     */
    override fun postNewsNotifications(
        newsResources: List<NewsResource>,
    ) = with(context) {
        // 检查是否有发布通知的权限
        if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            return
        }

        // 限制通知数量，只取前MAX_NUM_NOTIFICATIONS个新闻资源
        val truncatedNewsResources = newsResources.take(MAX_NUM_NOTIFICATIONS)

        // 为每个新闻资源创建对应的通知
        val newsNotifications = truncatedNewsResources.map { newsResource ->
            createNewsNotification {
                // 设置通知图标
                setSmallIcon(R.drawable.core_notifications_ic_nia_notification)
                // 设置通知标题为新闻标�?
                .setContentTitle(newsResource.title)
                // 设置通知内容为新闻内容摘要
                .setContentText(newsResource.content)
                // 设置点击通知后的跳转意图
                .setContentIntent(newsPendingIntent(newsResource))
                // 设置通知组，用于将相关通知分组显示
                .setGroup(News_NOTIFICATION_GROUP)
                // 设置点击后自动取消通知
                .setAutoCancel(true)
            }
        }
        
        // 创建摘要通知，用于在通知组折叠时显示
        val summaryNotification = createNewsNotification {
            val title = getString(
                R.string.core_notifications_News_notification_group_summary,
                truncatedNewsResources.size,
            )
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(R.drawable.core_notifications_ic_nia_notification)
                // 使用InboxStyle构建摘要信息
                .setStyle(newsNotificationStyle(truncatedNewsResources, title))
                .setGroup(News_NOTIFICATION_GROUP)
                // 标记为组摘要通知
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // 获取通知管理器并发送通知
        val notificationManager = NotificationManagerCompat.from(this)
        // 发送每个新闻资源的单独通知
        newsNotifications.forEachIndexed { index, notification ->
            notificationManager.notify(
                truncatedNewsResources[index].id.hashCode(),
                notification,
            )
        }
        // 发送摘要通知
        notificationManager.notify(News_NOTIFICATION_SUMMARY_ID, summaryNotification)
    }

    /**
     * 创建收件箱样式的新闻更新摘要通知
     * 
     * @param newsResources 新闻资源列表
     * @param title 摘要通知标题
     * @return 配置好的InboxStyle实例
     */
    private fun newsNotificationStyle(
        newsResources: List<NewsResource>,
        title: String,
    ): InboxStyle = newsResources
        // 将所有新闻标题添加到收件箱样式中
        .fold(InboxStyle()) { inboxStyle, newsResource -> inboxStyle.addLine(newsResource.title) }
        // 设置大内容标题
        .setBigContentTitle(title)
        // 设置摘要文本
        .setSummaryText(title)
}

/**
 * 为新闻更新创建配置好的通知
 * 
 * @param block 用于自定义通知构建器的lambda表达�?
 * @return 构建好的通知对象
 */
private fun Context.createNewsNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    // 确保通知渠道存在（Android 8.0+要求)
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        News_NOTIFICATION_CHANNEL_ID,
    )
        // 设置通知优先级为默认
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        // 应用自定义配�?
        .apply(block)
        // 构建通知
        .build()
}

/**
 * 确保通知渠道存在（适用于Android 8.0及以上版本）
 */
private fun Context.ensureNotificationChannelExists() {
    // 仅在Android 8.0 (Oreo)及以上版本需要创建通知渠道
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    // 创建通知渠道
    val channel = NotificationChannel(
        News_NOTIFICATION_CHANNEL_ID,
        getString(R.string.core_notifications_News_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.core_notifications_News_notification_channel_description)
    }
    // 向系统注册通知渠道
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

/**
 * 为新闻资源创建PendingIntent，用于点击通知时的跳转
 * 
 * @param newsResource 新闻资源对象
 * @return 配置好的PendingIntent
 */
private fun Context.newsPendingIntent(
    newsResource: NewsResource,
): PendingIntent? = PendingIntent.getActivity(
    this,
    News_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        // 设置操作为查�?
        action = Intent.ACTION_VIEW
        // 设置深度链接数据
        data = newsResource.newsDeepLinkUri()
        // 设置目标组件为指定的Activity
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    // 设置Intent标志：更新当前存在的Intent，并确保Intent是不可变的（Android 12+安全要求�?
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

/**
 * 为新闻资源生成深度链接URI
 * 
 * @return 包含新闻ID的深度链接URI
 */
private fun NewsResource.newsDeepLinkUri() = "$DEEP_LINK_BASE_PATH/$id".toUri()
