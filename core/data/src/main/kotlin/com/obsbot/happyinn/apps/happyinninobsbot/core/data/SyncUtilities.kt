package com.obsbot.happyinn.apps.happyinninobsbot.core.data
import android.util.Log
import com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.ChangeListVersions
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkChangeList
import kotlin.collections.last
import kotlin.collections.map
import kotlin.collections.partition
import kotlin.coroutines.cancellation.CancellationException

/**
 * 同步器接口标记，用于管理本地数据与远程数据源之间同步的类
 * 该接口负责处理同步过程中的版本控制、变更跟踪和同步操作协调
 */
//TODO 待研究
interface Synchronizer {
    /**
     * 获取变更列表版本信息
     * 用于查询上次同步的版本号，确定需要同步哪些数据
     */
    suspend fun getChangeListVersions(): ChangeListVersions

    /**
     * 更新变更列表版本信息
     * 同步完成后调用，用于记录最新的同步版本号
     * @param update 版本更新函数，接收当前版本并返回更新后的版本
     */
    suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions)

    /**
     * 语法糖，用于调用 [Syncable.syncWith] 并省略同步器参数
     * 提供更简洁的API调用方式
     */
    suspend fun Syncable.sync() = this@sync.syncWith(this@Synchronizer)

}

/**
 * 可同步接口标记，表示与远程数据源同步的类。
 * 任何需要与服务器数据同步的仓库或数据源都应实现此接口
 * 同步操作不能并发执行，[Synchronizer] 负责确保这一点。
 */
interface Syncable {
    /**
     * 将本地数据库与网络同步。
     * 实现此方法的类负责定义具体的同步逻辑
     * @param synchronizer 同步器实例，提供版本管理功能
     * @return 同步是否成功
     */
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

/**
 * 尝试执行 [block]，如果成功则返回成功的 [Result]，否则返回 [Result.Failure]
 * 注意不要破坏结构化并发
 * @param block 要执行的挂起函数
 * @return 包含执行结果的Result对象
 */
private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    // 执行传入的代码块并返回成功结果
    Result.success(block())
} catch (cancellationException: CancellationException) {
    // 不捕获协程取消异常，让它向上传播
    throw cancellationException
} catch (exception: Exception) {
    // 捕获其他异常，记录日志并返回失败结果
    Log.i(
        "suspendRunCatching",
        "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result",
        exception,
    )
    Result.failure(exception)
}

/**
 * 用于将仓库与网络同步的工具函数。
 * 实现了基于变更列表的增量同步机制，只同步发生变化的数据
 * 
 * @param versionReader 读取需要同步的模型的当前版本
 * @param changeListFetcher 获取模型的变更列表，基于当前版本号请求变更数据
 * @param versionUpdater 在同步成功后更新 [ChangeListVersions]
 * @param modelDeleter 通过消费已删除模型的ID来删除模型
 * @param modelUpdater 通过消费已更改模型的ID来更新模型
 * @return 同步是否成功
 * 
 * 注意上述定义的代码块永远不会并发运行，[Synchronizer] 实现必须保证这一点。
 */
suspend fun Synchronizer.changeListSync(
    versionReader: (ChangeListVersions) -> Int,
    changeListFetcher: suspend (Int) -> List<NetworkChangeList>,
    versionUpdater: ChangeListVersions.(Int) -> ChangeListVersions,
    modelDeleter: suspend (List<String>) -> Unit,
    modelUpdater: suspend (List<String>) -> Unit,
) = suspendRunCatching {
    // 获取自上次同步以来的变更列表（类似于git fetch）
    val currentVersion = versionReader(getChangeListVersions())
    val changeList = changeListFetcher(currentVersion)
    // 如果没有变更，直接返回成功
    if (changeList.isEmpty()) return@suspendRunCatching true

    // 将变更列表分为已删除和已更新两类
    val (deleted, updated) = changeList.partition(NetworkChangeList::isDelete)

    // 删除服务器端已被删除的模型
    modelDeleter(deleted.map(NetworkChangeList::id))

    // 使用变更列表，拉取并保存更改（类似于git pull）
    modelUpdater(updated.map(NetworkChangeList::id))

    // 更新最后同步的版本（类似于更新本地git HEAD）
    val latestVersion = changeList.last().changeListVersion
    updateChangeListVersions {
        versionUpdater(latestVersion)
    }
    
    // 同步成功返回true
    true
}.isSuccess // 检查执行结果是否成功