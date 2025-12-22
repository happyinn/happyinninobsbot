package com.obsbot.happyinn.apps.happyinninobsbot.core.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.tracing.trace
import com.obsbot.happyinn.apps.happyinninobsbot.network.HioDispatchers.IO
import com.google.samples.apps.nowinandroid.core.network.di.ApplicationScope
import com.obsbot.happyinn.apps.happyinninobsbot.network.Dispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinTimeZone
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用于报告设备当前时区设置的工具。
 * 它总是至少发出一次默认设置，然后在每次时区更改时发出。
 */
interface TimeZoneMonitor {
    // 当前时区的Flow
    val currentTimeZone: Flow<TimeZone>
}

@Singleton
internal class TimeZoneBroadcastMonitor @Inject constructor(
    // 应用上下文
    @ApplicationContext private val context: Context,
    // 应用作用域
    @ApplicationScope appScope: CoroutineScope,
    // IO调度器
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : TimeZoneMonitor {

    // 当前时间的SharedFlow
    override val currentTimeZone: SharedFlow<TimeZone> =
        callbackFlow {
            // 首先发送默认时区
            trySend(TimeZone.currentSystemDefault())

            // 注册用于时区变化的BroadcastReceiver
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    // 如果不是时区变化动作，则返回
                    if (intent.action != Intent.ACTION_TIMEZONE_CHANGED) return

                    // 根据Android版本获取意图中的时区信息
                    val zoneIdFromIntent = if (VERSION.SDK_INT < VERSION_CODES.R) {
                        null
                    } else {
                        // 从Android R开始，我们可以从意图中获取新的时区
                        intent.getStringExtra(Intent.EXTRA_TIMEZONE)?.let { timeZoneId ->
                            // 需要将java.util.Timezone转换为java.time.ZoneId
                            val zoneId = ZoneId.of(timeZoneId, ZoneId.SHORT_IDS)
                            // 转换为kotlinx.datetime.TimeZone
                            zoneId.toKotlinTimeZone()
                        }
                    }

                    // 如果意图中没有时区信息，则回退到系统默认时区
                    trySend(zoneIdFromIntent ?: TimeZone.currentSystemDefault())
                }
            }

            // 注册时区变化广播接收器
            trace("TimeZoneBroadcastReceiver.register") {
                context.registerReceiver(receiver, IntentFilter(Intent.ACTION_TIMEZONE_CHANGED))
            }

            // 再次发送，因为注册BroadcastReceiver可能需要几毫秒时间
            // 这样可以减少TZ变化未被捕获的可能性
            trySend(TimeZone.currentSystemDefault())

            // 关闭时注销广播接收器
            awaitClose {
                context.unregisterReceiver(receiver)
            }
        }
            // 使用distinctUntilChanged防止相同类型的多次发射，因为我们多次使用trySend
            .distinctUntilChanged()
            .conflate()
            .flowOn(ioDispatcher)
            // 共享回调以防止注册多个BroadcastReceiver
            .shareIn(appScope, SharingStarted.WhileSubscribed(5_000), 1)
}
