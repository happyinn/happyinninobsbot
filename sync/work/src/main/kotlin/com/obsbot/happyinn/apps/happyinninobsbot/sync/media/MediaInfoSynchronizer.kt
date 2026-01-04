package com.obsbot.happyinn.apps.happyinninobsbot.sync.media

import android.net.Uri

/**
 * 媒体信息同步器接口
 * 用于同步媒体文件的元信息
 */
interface MediaInfoSynchronizer {

    /**
     * 添加媒体信息到同步队列
     * @param uri 媒体文件的URI
     */
    suspend fun addMedia(uri: Uri)
}
