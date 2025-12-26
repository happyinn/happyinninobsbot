package com.obsbot.happyinn.apps.happyinninobsbot.core.network.retrofit
import androidx.tracing.trace
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.BuildConfig
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.HioNetworkDataSource
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkChangeList
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkNewsResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkTopic
import dagger.Lazy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.java

/**
 * Hio网络API的Retrofit接口声明
 */
private interface RetrofitHioNetworkApi {

    /**
     * 获取主题列表
     *
     * @param ids 可选的主题ID列表，用于过滤结果
     * @return 包含主题列表的网络响应
     */
    @GET(value = "topics")
    suspend fun getTopics(
        @Query("id") ids: List<String>?,
    ): NetworkResponse<List<NetworkTopic>>

    /**
     * 获取新闻资源列表
     *
     * @param ids 可选的新闻资源ID列表，用于过滤结果
     * @return 包含新闻资源列表的网络响应
     */
    @GET(value = "newsresources")
    suspend fun getNewsResources(
        @Query("id") ids: List<String>?,
    ): NetworkResponse<List<NetworkNewsResource>>

    /**
     * 获取主题变更列表
     *
     * @param after 可选的时间戳，用于获取在此之后的变更
     * @return 主题变更列表
     */
    @GET(value = "changelists/topics")
    suspend fun getTopicChangeList(
        @Query("after") after: Int?,
    ): List<NetworkChangeList>

    /**
     * 获取新闻资源变更列表
     *
     * @param after 可选的时间戳，用于获取在此之后的变更
     * @return 新闻资源变更列表
     */
    @GET(value = "changelists/newsresources")
    suspend fun getNewsResourcesChangeList(
        @Query("after") after: Int?,
    ): List<NetworkChangeList>
}

// Hio基础URL常量，从构建配置中获取
private const val Hio_BASE_URL = BuildConfig.BACKEND_URL

/**
 * [Hio_BASE_URL]提供的数据包装器
 */
@Serializable
private data class NetworkResponse<T>(
    val data: T,
)

/**
 * 基于[Retrofit]的[HioNetworkDataSource]实现
 */
@Singleton
internal class RetrofitHioNetwork @Inject constructor(
    // 网络JSON序列化器
    networkJson: Json,
    // OkHttp调用工厂的延迟加载实例
    okhttpCallFactory: Lazy<Call.Factory>,
) : HioNetworkDataSource {

    // 网络API实例，使用trace进行性能追踪
    private val networkApi = trace("RetrofitHioNetwork") {
        Retrofit.Builder()
            .baseUrl(Hio_BASE_URL)
            // 我们在这里使用callFactory lambda配合dagger.Lazy<Call.Factory>
            // 来防止在主线程初始化OkHttp
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitHioNetworkApi::class.java)
    }

    /**
     * 获取主题列表
     *
     * @param ids 可选的主题ID列表，用于过滤结果
     * @return 主题列表
     */
    override suspend fun getTopics(ids: List<String>?): List<NetworkTopic> =
        networkApi.getTopics(ids = ids).data

    /**
     * 获取新闻资源列表
     *
     * @param ids 可选的新闻资源ID列表，用于过滤结果
     * @return 新闻资源列表
     */
    override suspend fun getNewsResources(ids: List<String>?): List<NetworkNewsResource> =
        networkApi.getNewsResources(ids = ids).data

    /**
     * 获取主题变更列表
     *
     * @param after 可选的时间戳，用于获取在此之后的变更
     * @return 主题变更列表
     */
    override suspend fun getTopicChangeList(after: Int?): List<NetworkChangeList> =
        networkApi.getTopicChangeList(after = after)

    /**
     * 获取新闻资源变更列表
     *
     * @param after 可选的时间戳，用于获取在此之后的变更
     * @return 新闻资源变更列表
     */
    override suspend fun getNewsResourceChangeList(after: Int?): List<NetworkChangeList> =
        networkApi.getNewsResourcesChangeList(after = after)
}
