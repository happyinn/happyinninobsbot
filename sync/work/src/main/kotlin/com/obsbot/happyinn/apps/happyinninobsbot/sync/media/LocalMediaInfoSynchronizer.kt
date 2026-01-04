package com.obsbot.happyinn.apps.happyinninobsbot.sync.media

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.samples.apps.nowinandroid.core.network.di.ApplicationScope
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.media.MediumDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.AudioStreamInfoEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.SubtitleStreamInfoEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.VideoStreamInfoEntity
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.thumbnailCacheDir
import com.obsbot.happyinn.apps.happyinninobsbot.network.Dispatcher
import com.obsbot.happyinn.apps.happyinninobsbot.network.HioDispatchers
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.github.anilbeesetti.nextlib.mediainfo.AudioStream
import io.github.anilbeesetti.nextlib.mediainfo.MediaInfoBuilder
import io.github.anilbeesetti.nextlib.mediainfo.SubtitleStream
import io.github.anilbeesetti.nextlib.mediainfo.VideoStream

/**
 * 本地媒体信息同步器实现
 * 负责同步媒体文件的详细信息，包括视频流、音频流、字幕流和缩略图
 */
class LocalMediaInfoSynchronizer @Inject constructor(
    private val mediumDao: MediumDao,                // 媒体数据访问对象
    @ApplicationScope private val applicationScope: CoroutineScope,  // 应用级协程作用域
    @ApplicationContext private val context: Context, // 应用上下文
    @Dispatcher(HioDispatchers.Default) private val dispatcher: CoroutineDispatcher,  // 默认调度器
) : MediaInfoSynchronizer {

    private val media = MutableSharedFlow<Uri>()  // 媒体URI共享流

    /**
     * 添加媒体信息到同步队列
     * @param uri 媒体文件的URI
     */
    override suspend fun addMedia(uri: Uri) = media.emit(uri)

    /**
     * 执行媒体信息同步
     * 监听媒体URI流，提取媒体信息并更新数据库
     */
    private suspend fun sync(): Unit = withContext(dispatcher) {
        media.collect { mediumUri ->
            // 获取媒体实体
            val medium = mediumDao.getWithInfo(mediumUri.toString()) ?: return@collect
            // 如果缩略图已存在，跳过同步
            if (medium.mediumEntity.thumbnailPath?.let { File(it) }?.exists() == true) {
                return@collect
            }

            // 构建媒体信息
            val mediaInfo = runCatching {
                MediaInfoBuilder().from(context = context, uri = mediumUri).build() ?: throw NullPointerException()
            }.onFailure { e ->
                e.printStackTrace()
                Log.d(TAG, "sync: MediaInfoBuilder exception", e)
            }.getOrNull() ?: return@collect

            // 获取缩略图
            val thumbnail = runCatching { mediaInfo.getFrame() }.getOrNull()
            // 释放媒体信息资源
            mediaInfo.release()

            // 转换视频流信息
            val videoStreamInfo = mediaInfo.videoStream?.toVideoStreamInfoEntity(medium.mediumEntity.uriString)
            // 转换音频流信息列表
            val audioStreamsInfo = mediaInfo.audioStreams.map {
                it.toAudioStreamInfoEntity(medium.mediumEntity.uriString)
            }
            // 转换字幕流信息列表
            val subtitleStreamsInfo = mediaInfo.subtitleStreams.map {
                it.toSubtitleStreamInfoEntity(medium.mediumEntity.uriString)
            }
            // 保存缩略图
            val thumbnailPath = thumbnail?.saveTo(
                storageDir = context.thumbnailCacheDir,
                quality = 40,
                fileName = medium.mediumEntity.mediaStoreId.toString(),
            )

            // 更新媒体信息
            mediumDao.upsert(
                medium.mediumEntity.copy(
                    format = mediaInfo.format,
                    thumbnailPath = thumbnailPath,
                ),
            )
            // 更新视频流信息
            videoStreamInfo?.let { mediumDao.upsertVideoStreamInfo(it) }
            // 更新音频流信息列表
            audioStreamsInfo.onEach { mediumDao.upsertAudioStreamInfo(it) }
            // 更新字幕流信息列表
            subtitleStreamsInfo.onEach { mediumDao.upsertSubtitleStreamInfo(it) }
        }
    }

    /**
     * 初始化块
     * 启动媒体信息同步协程
     */
    init {
        applicationScope.launch { sync() }
    }

    companion object {
        private const val TAG = "MediaInfoSynchronizer"  // 日志标签
    }
}

/**
 * 将VideoStream转换为VideoStreamInfoEntity
 * @param mediumUri 媒体URI
 * @return 视频流信息实体
 */
private fun VideoStream.toVideoStreamInfoEntity(mediumUri: String) = VideoStreamInfoEntity(
    index = index,
    title = title,
    codecName = codecName,
    language = language,
    disposition = disposition,
    bitRate = bitRate,
    frameRate = frameRate,
    frameWidth = frameWidth,
    frameHeight = frameHeight,
    mediumUri = mediumUri,
)

/**
 * 将AudioStream转换为AudioStreamInfoEntity
 * @param mediumUri 媒体URI
 * @return 音频流信息实体
 */
private fun AudioStream.toAudioStreamInfoEntity(mediumUri: String) = AudioStreamInfoEntity(
    index = index,
    title = title,
    codecName = codecName,
    language = language,
    disposition = disposition,
    bitRate = bitRate,
    sampleFormat = sampleFormat,
    sampleRate = sampleRate,
    channels = channels,
    channelLayout = channelLayout,
    mediumUri = mediumUri,
)

/**
 * 将SubtitleStream转换为SubtitleStreamInfoEntity
 * @param mediumUri 媒体URI
 * @return 字幕流信息实体
 */
private fun SubtitleStream.toSubtitleStreamInfoEntity(mediumUri: String) = SubtitleStreamInfoEntity(
    index = index,
    title = title,
    codecName = codecName,
    language = language,
    disposition = disposition,
    mediumUri = mediumUri,
)

/**
 * 保存Bitmap到文件
 * @param storageDir 存储目录
 * @param quality 压缩质量，0-100
 * @param fileName 文件名
 * @return 保存后的文件路径，失败返回null
 */
suspend fun Bitmap.saveTo(
    storageDir: File,
    quality: Int = 100,
    fileName: String,
): String? = withContext(Dispatchers.IO) {
    val thumbFile = File(storageDir, fileName)
    try {
        FileOutputStream(thumbFile).use { fos ->
            compress(Bitmap.CompressFormat.JPEG, quality, fos)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext if (thumbFile.exists()) thumbFile.path else null
}
