package com.obsbot.happyinn.apps.happyinninobsbot.core.network.demo


import JvmUnitTestDemoAssetManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.HioNetworkDataSource
import com.obsbot.happyinn.apps.happyinninobsbot.network.Dispatcher
import com.obsbot.happyinn.apps.happyinninobsbot.network.HioDispatchers.IO

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.BufferedReader
import javax.inject.Inject

/**
 * [NiaNetworkDataSource] 实现，提供静态新闻资源以辅助开发
 */
class DemoNiaNetworkDataSource @Inject constructor(
    // IO调度器，用于执行IO密集型任务
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    // 网络JSON序列化器
    private val networkJson: Json,
    // 资源管理器，默认使用JvmUnitTestDemoAssetManager
    private val assets: DemoAssetManager = JvmUnitTestDemoAssetManager,
) : HioNetworkDataSource {

    /**
     * 获取主题列表
     *
     * @param ids 可选的主题ID列表，用于过滤结果
     * @return 网络主题列表
     */
/*    override suspend fun getTopics(ids: List<String>?): List<NetworkTopic> =
        getDataFromJsonFile(TOPICS_ASSET)*/

    /**
     * 获取新闻资源列表
     *
     * @param ids 可选的新闻资源ID列表，用于过滤结果
     * @return 网络新闻资源列表
     */
 /*   override suspend fun getNewsResources(ids: List<String>?): List<NetworkNewsResource> =
        getDataFromJsonFile(NEWS_ASSET)*/

    /**
     * 获取主题变更列表
     *
     * @param after 可选的时间戳，用于获取在此之后的变更
     * @return 网络变更列表
     */
/*    override suspend fun getTopicChangeList(after: Int?): List<NetworkChangeList> =
        getTopics().mapToChangeList(NetworkTopic::id)*/

    /**
     * 获取新闻资源变更列表
     *
     * @param after 可选的时间戳，用于获取在此之后的变更
     * @return 网络变更列表
     */
/*    override suspend fun getNewsResourceChangeList(after: Int?): List<NetworkChangeList> =
        getNewsResources().mapToChangeList(NetworkNewsResource::id)*/

    /**
     * 从指定的JSON文件中获取数据
     *
     * @param fileName JSON文件名
     * @return 解析后的数据列表
     */
    @OptIn(ExperimentalSerializationApi::class)
    private suspend inline fun <reified T> getDataFromJsonFile(fileName: String): List<T> =
        withContext(ioDispatcher) {
            assets.open(fileName).use { inputStream ->
                if (SDK_INT <= M) {
                    /**
                     * 在API 23 (M)及以下版本，我们必须使用一种解决方法来避免在反序列化过程中抛出异常。
                     * 详见: https://github.com/Kotlin/kotlinx.serialization/issues/2457#issuecomment-1786923342
                     */
                    inputStream.bufferedReader().use(BufferedReader::readText)
                        .let(networkJson::decodeFromString)
                } else {
                    networkJson.decodeFromStream(inputStream)
                }
            }
        }

    // 伴生对象，定义常量
    companion object {
        // 新闻资源JSON文件名
        private const val NEWS_ASSET = "news.json"
        // 主题JSON文件名
        private const val TOPICS_ASSET = "topics.json"
    }
}

/**
 * 将[T]类型的列表转换为变更列表，其中[idGetter]定义[NetworkChangeList.id]
 *
 * @param idGetter 用于获取项目ID的函数
 * @return 网络变更列表
 */
/*private fun <T> List<T>.mapToChangeList(
    idGetter: (T) -> String,
) = mapIndexed { index, item ->
    NetworkChangeList(
        id = idGetter(item),
        changeListVersion = index,
        isDelete = false,
    )
}*/
