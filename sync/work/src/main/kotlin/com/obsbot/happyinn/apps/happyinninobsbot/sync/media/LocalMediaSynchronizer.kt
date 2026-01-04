package com.obsbot.happyinn.apps.happyinninobsbot.sync.media

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.provider.MediaStore
import com.google.samples.apps.nowinandroid.core.network.di.ApplicationScope
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.converter.UriListConverter
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.media.DirectoryDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.media.MediumDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.media.MediumStateDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.DirectoryEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.media.MediumEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaVideo
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.VIDEO_COLLECTION_URI
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.getStorageVolumes
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.prettyName
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.scanPaths
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.scanStorage
import com.obsbot.happyinn.apps.happyinninobsbot.network.Dispatcher
import com.obsbot.happyinn.apps.happyinninobsbot.network.HioDispatchers
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 本地媒体同步器实现
 * 负责同步本地媒体文件到应用数据库，并监听媒体库变化
 */
class LocalMediaSynchronizer @Inject constructor(
    private val mediumDao: MediumDao,                // 媒体数据访问对象
    private val mediumStateDao: MediumStateDao,      // 媒体状态数据访问对象
    private val directoryDao: DirectoryDao,          // 目录数据访问对象
    @ApplicationScope private val applicationScope: CoroutineScope,  // 应用级协程作用域
    @ApplicationContext private val context: Context, // 应用上下文
    @Dispatcher(HioDispatchers.IO) private val dispatcher: CoroutineDispatcher,  // IO调度器
) : MediaSynchronizer {

    private var mediaSyncingJob: Job? = null  // 媒体同步任务

    /**
     * 刷新媒体库
     * @param path 可选路径，指定要刷新的特定目录
     * @return 是否成功刷新
     */
    override suspend fun refresh(path: String?): Boolean {
        return path?.let { context.scanPaths(listOf(path)) }  // 刷新指定路径
            ?: context.getStorageVolumes().all { context.scanStorage(it.path) }  // 刷新所有存储卷
    }

    /**
     * 启动媒体同步
     * 监听媒体库变化并更新数据库
     */
    override fun startSync() {
        if (mediaSyncingJob != null) return  // 已有同步任务，直接返回
        mediaSyncingJob = getMediaVideosFlow().onEach { media ->
            applicationScope.launch { updateDirectories(media) }  // 更新目录
            applicationScope.launch { updateMedia(media) }  // 更新媒体
        }.launchIn(applicationScope)
    }

    /**
     * 停止媒体同步
     */
    override fun stopSync() {
        mediaSyncingJob?.cancel()  // 取消同步任务
    }

    /**
     * 更新目录信息
     * @param media 媒体文件列表
     */
    private suspend fun updateDirectories(media: List<MediaVideo>) =
        withContext(Dispatchers.Default) {
            // 获取所有存储卷的目录结构
            val directories = context.getStorageVolumes().flatMap {
                getDirectoryEntities(currentFolder = it, media = media)
            }
            // 插入或更新目录
            directoryDao.upsertAll(directories)

            val currentDirectoryPaths = directories.map { it.path }

            // 查找不再存在的目录
            val unwantedDirectories = directoryDao.getAll().first()
                .filterNot { it.path in currentDirectoryPaths }

            val unwantedDirectoriesPaths = unwantedDirectories.map { it.path }

            // 删除不再存在的目录
            directoryDao.delete(unwantedDirectoriesPaths)
        }

    /**
     * 递归获取目录实体
     * @param parentFolder 父目录
     * @param currentFolder 当前目录
     * @param media 媒体文件列表
     * @return 目录实体列表
     */
    private fun getDirectoryEntities(
        parentFolder: File? = null,
        currentFolder: File,
        media: List<MediaVideo>,
    ): List<DirectoryEntity> {
        // 检查当前目录是否包含媒体文件
        val hasMediaInCurrentFolder = media.any { it.data.startsWith(currentFolder.path) }

        if (!hasMediaInCurrentFolder) return emptyList()

        // 创建当前目录实体
        val currentDirectoryEntity = DirectoryEntity(
            path = currentFolder.path,
            name = currentFolder.prettyName,
            modified = currentFolder.lastModified(),
            parentPath = parentFolder?.path ?: "/",
        )

        // 递归获取子目录实体
        val subDirectories = currentFolder.listFiles { file ->
            file.isDirectory && media.any { it.data.startsWith(file.path) }
        }?.flatMap { file ->
            getDirectoryEntities(
                parentFolder = currentFolder,
                currentFolder = file,
                media = media,
            )
        } ?: emptyList()

        return listOf(currentDirectoryEntity) + subDirectories
    }

    /**
     * 更新媒体信息
     * @param media 媒体文件列表
     */
    private suspend fun updateMedia(media: List<MediaVideo>) = withContext(Dispatchers.Default) {
        // 转换媒体文件为数据库实体
        val mediumEntities = media.map {
            val file = File(it.data)
            val mediumEntity = mediumDao.get(it.uri.toString())
            mediumEntity?.copy(
                path = file.path,
                name = file.name,
                size = it.size,
                width = it.width,
                height = it.height,
                duration = it.duration,
                mediaStoreId = it.id,
                modified = it.dateModified,
                parentPath = file.parent!!,
            ) ?: MediumEntity(
                uriString = it.uri.toString(),
                path = it.data,
                name = file.name,
                parentPath = file.parent!!,
                modified = it.dateModified,
                size = it.size,
                width = it.width,
                height = it.height,
                duration = it.duration,
                mediaStoreId = it.id,
            )
        }

        // 插入或更新媒体
        mediumDao.upsertAll(mediumEntities)

        val currentMediaUris = mediumEntities.map { it.uriString }

        // 查找不再存在的媒体
        val unwantedMedia = mediumDao.getAllWithInfo().first()
            .filterNot { it.mediumEntity.uriString in currentMediaUris }

        val unwantedMediaUris = unwantedMedia.map { it.mediumEntity.uriString }

        // 删除不再存在的媒体
        mediumDao.delete(unwantedMediaUris)
        mediumStateDao.delete(unwantedMediaUris)

        // 删除不再需要的缩略图
        val unwantedThumbnailFiles = unwantedMedia.mapNotNull { medium -> medium.mediumEntity.thumbnailPath?.let { File(it) } }
        unwantedThumbnailFiles.forEach { file ->
            try {
                file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 释放不再使用的外部字幕URI权限
        launch {
            // 获取当前所有媒体使用的外部字幕
            val currentMediaExternalSubs = mediumEntities.flatMap {
                val mediaState = mediumStateDao.get(it.uriString) ?: return@flatMap emptyList<String>()
                UriListConverter.fromStringToList(mediaState.externalSubs)
            }.toSet()

            // 释放不再使用的外部字幕权限
            unwantedMedia.onEach { mediumWithInfo ->
                val mediumState = mediumWithInfo.mediumStateEntity ?: return@onEach
                for (sub in UriListConverter.fromStringToList(mediumState.externalSubs)) {
                    if (sub !in currentMediaExternalSubs) {
                        try {
                            context.contentResolver.releasePersistableUriPermission(sub, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取媒体视频流
     * @param selection 查询条件
     * @param selectionArgs 查询条件参数
     * @param sortOrder 排序方式
     * @return 媒体视频流
     */
    private fun getMediaVideosFlow(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = "${MediaStore.Video.Media.DISPLAY_NAME} ASC",
    ): Flow<List<MediaVideo>> = callbackFlow {
        // 监听媒体库变化
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                trySend(getMediaVideo(selection, selectionArgs, sortOrder))  // 发送媒体变化
            }
        }
        context.contentResolver.registerContentObserver(VIDEO_COLLECTION_URI, true, observer)
        // 发送初始值
        trySend(getMediaVideo(selection, selectionArgs, sortOrder))
        // 关闭时取消监听
        awaitClose { context.contentResolver.unregisterContentObserver(observer) }
    }.flowOn(dispatcher).distinctUntilChanged()

    /**
     * 从媒体库获取媒体视频
     * @param selection 查询条件
     * @param selectionArgs 查询条件参数
     * @param sortOrder 排序方式
     * @return 媒体视频列表
     */
    private fun getMediaVideo(
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
    ): List<MediaVideo> {
        val mediaVideos = mutableListOf<MediaVideo>()
        // 查询媒体库
        context.contentResolver.query(
            VIDEO_COLLECTION_URI,
            VIDEO_PROJECTION,
            selection,
            selectionArgs,
            sortOrder,
        )?.use { cursor ->

            // 获取列索引
            val idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID)
            val dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
            val durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
            val widthColumn = cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)
            val heightColumn = cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)
            val sizeColumn = cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
            val dateModifiedColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)

            // 遍历结果
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                mediaVideos.add(
                    MediaVideo(
                        id = id,
                        data = cursor.getString(dataColumn),
                        duration = cursor.getLong(durationColumn),
                        uri = ContentUris.withAppendedId(VIDEO_COLLECTION_URI, id),
                        width = cursor.getInt(widthColumn),
                        height = cursor.getInt(heightColumn),
                        size = cursor.getLong(sizeColumn),
                        dateModified = cursor.getLong(dateModifiedColumn),
                    ),
                )
            }
        }
        // 过滤掉不存在的文件
        return mediaVideos.filter { File(it.data).exists() }
    }

    companion object {
        /**
         * 媒体库查询投影
         * 包含媒体文件的关键信息
         */
        val VIDEO_PROJECTION = arrayOf(
            MediaStore.Video.Media._ID,              // 媒体ID
            MediaStore.Video.Media.DATA,             // 文件路径
            MediaStore.Video.Media.DURATION,         // 时长
            MediaStore.Video.Media.HEIGHT,           // 高度
            MediaStore.Video.Media.WIDTH,            // 宽度
            MediaStore.Video.Media.SIZE,             // 文件大小
            MediaStore.Video.Media.DATE_MODIFIED,    // 修改日期
        )
    }
}
