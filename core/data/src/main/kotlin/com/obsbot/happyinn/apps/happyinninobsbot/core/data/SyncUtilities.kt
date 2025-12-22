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
 */
//TODO 待研究
interface Synchronizer {
    /**
     * 获取变更列表版本信息
     */
    suspend fun getChangeListVersions(): ChangeListVersions

    /**
     * 更新变更列表版本信息
     */
    suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions)

    /**
     * 语法糖，用于调用 [Syncable.syncWith] 并省略同步器参数
     */
    suspend fun Syncable.sync() = this@sync.syncWith(this@Synchronizer)

}

/**
 * 可同步接口标记，表示与远程数据源同步的类。
 * 同步操作不能并发执行，[Synchronizer] 负责确保这一点。
 */
interface Syncable {
    /**
     * 将本地数据库与网络同步。
     * 返回同步是否成功。
     */
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

/**
 * 尝试执行 [block]，如果成功则返回成功的 [Result]，否则返回 [Result.Failure]
 * 注意不要破坏结构化并发
 */
private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.i(
        "suspendRunCatching",
        "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result",
        exception,
    )
    Result.failure(exception)
}

/**
 * 用于将仓库与网络同步的工具函数。
 * [versionReader] 读取需要同步的模型的当前版本
 * [changeListFetcher] 获取模型的变更列表
 * [versionUpdater] 在同步成功后更新 [ChangeListVersions]
 * [modelDeleter] 通过消费已删除模型的ID来删除模型
 * [modelUpdater] 通过消费已更改模型的ID来更新模型
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
    if (changeList.isEmpty()) return@suspendRunCatching true

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
}.isSuccess
