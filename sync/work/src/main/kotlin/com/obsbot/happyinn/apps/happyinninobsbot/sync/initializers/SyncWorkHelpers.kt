package com.obsbot.happyinn.apps.happyinninobsbot.sync.initializers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import com.obsbot.happyinn.apps.happyinninobsbo.sync.R

//TODO 全部待细看
/**
 * 同步主题常量，用于标识同步相关的通知或事件
 */
const val SYNC_TOPIC = "sync"
private const val SYNC_NOTIFICATION_ID = 0
private const val SYNC_NOTIFICATION_CHANNEL_ID = "SyncNotificationChannel"

// 所有同步工作都需要网络连接
val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

/**
 * 为较低API级别提供同步前台信息服务
 * 当同步工作器作为前台服务运行时使用
 * @receiver Context 上下文
 * @return ForegroundInfo 前台服务信息
 */
fun Context.syncForegroundInfo() = ForegroundInfo(
    SYNC_NOTIFICATION_ID,
    syncWorkNotification(),
)

/**
 * 为较低API级别提供同步工作通知
 * 当同步工作器作为前台服务运行时显示的通知
 * @receiver Context 上下文
 * @return Notification 通知对象
 */
private fun Context.syncWorkNotification(): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            SYNC_NOTIFICATION_CHANNEL_ID,
            getString(R.string.sync_work_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = getString(R.string.sync_work_notification_channel_description)
        }
        // 向系统注册通知渠道
        val notificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        notificationManager?.createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(
        this,
        SYNC_NOTIFICATION_CHANNEL_ID,
    )
        .setSmallIcon(
            com.obsbot.happyinn.apps.happyinninobsbot.core.notifications.R.drawable.core_notifications_ic_nia_notification,
        )
        .setContentTitle(getString(R.string.sync_work_notification_title))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}
