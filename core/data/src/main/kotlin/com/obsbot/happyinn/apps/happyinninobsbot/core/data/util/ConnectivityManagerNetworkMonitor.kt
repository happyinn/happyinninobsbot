/*
 * 版权所有 2022 The Android Open Source Project
 *
 * 根据 Apache 许可证 2.0 版（"许可证"）授权；
 * 除非符合许可证要求，否则您不得使用此文件。
 * 您可以在以下位置获得许可证副本：
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，根据许可证分发的软件
 * 是基于"按原样"的基础上分发的，不附带任何明示或暗示的担保条件。
 * 请参阅许可证了解特定语言 governing permissions 和 limitations。
 */

package com.obsbot.happyinn.apps.happyinninobsbot.core.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest.Builder
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.content.getSystemService
import androidx.tracing.trace
import com.obsbot.happyinn.apps.happyinninobsbot.network.HioDispatchers.IO
import com.obsbot.happyinn.apps.happyinninobsbot.network.Dispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.collections.isNotEmpty
import kotlin.collections.minusAssign
import kotlin.collections.plusAssign
import kotlin.let

/**
 * 基于 ConnectivityManager 的网络监控器实现
 * 监控设备的网络连接状态变化
 *
 * @param context 应用上下文
 * @param ioDispatcher IO调度器，用于在后台线程执行网络监控任务
 */
internal class ConnectivityManagerNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : NetworkMonitor {
    /**
     * 网络在线状态的 Flow 流
     * 当网络连接状态发生变化时会发出更新
     */
    override val isOnline: Flow<Boolean> = callbackFlow {
        trace("NetworkMonitor.callbackFlow") {
            // 获取 ConnectivityManager 系统服务
            val connectivityManager = context.getSystemService<ConnectivityManager>()
            if (connectivityManager == null) {
                // 如果无法获取服务，发送离线状态并关闭通道
                channel.trySend(false)
                channel.close()
                return@callbackFlow
            }

            /**
             * 网络回调对象
             * 回调方法会在任何匹配 NetworkRequest 的网络变化时被调用，不仅仅是活跃网络
             * 因此我们可以简单地跟踪此类 Network 的存在（或不存在）
             */
            val callback = object : NetworkCallback() {

                // 跟踪当前可用的网络集合
                private val networks = mutableSetOf<Network>()

                /**
                 * 当网络变为可用时调用
                 *
                 * @param network 可用的网络
                 */
                override fun onAvailable(network: Network) {
                    networks += network
                    channel.trySend(true)
                }

                /**
                 * 当网络丢失时调用
                 *
                 * @param network 丢失的网络
                 */
                override fun onLost(network: Network) {
                    networks -= network
                    channel.trySend(networks.isNotEmpty())
                }
            }

            trace("NetworkMonitor.registerNetworkCallback") {
                // 构建网络请求，要求具有 Internet 能力的网络
                val request = Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
                // 注册网络回调
                connectivityManager.registerNetworkCallback(request, callback)
            }

            /**
             * 发送最新的连接状态到基础通道
             */
            channel.trySend(connectivityManager.isCurrentlyConnected())

            // 等待关闭，取消注册网络回调
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }
    }
        .flowOn(ioDispatcher)  // 在 IO 调度器上执行
        .conflate()            // 合并发射，避免背压

    /**
     * 检查当前是否连接到网络
     *
     * @return Boolean 如果当前连接到网络返回 true，否则返回 false
     */
    @Suppress("DEPRECATION")
    private fun ConnectivityManager.isCurrentlyConnected() = when {
        VERSION.SDK_INT >= VERSION_CODES.M ->
            activeNetwork
                ?.let(::getNetworkCapabilities)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        else -> activeNetworkInfo?.isConnected
    } ?: false
}
