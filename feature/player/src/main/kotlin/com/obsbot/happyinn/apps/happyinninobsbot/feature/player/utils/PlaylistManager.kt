package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils

import android.net.Uri

/**
 * 播放列表管理器
 * - 维护当前播放队列和当前播放项
 * - 提供上一首/下一首/当前索引等便捷操作
 * - 仅管理队列数据，不直接触发播放器
 */
class PlaylistManager {

    private val queue = mutableListOf<Uri>()
    private var currentItem: Uri? = null

    fun clear() = queue.clear()

    fun hasNext(): Boolean {
        return currentIndex() + 1 < size()
    }

    fun hasPrev(): Boolean {
        return currentIndex() > 0
    }

    fun getNext(): Uri? = queue.getOrNull(currentIndex() + 1)

    fun getPrev(): Uri? = queue.getOrNull(currentIndex() - 1)

    fun size() = queue.size

    fun currentIndex(): Int = queue.indexOfFirst { it == currentItem }.takeIf { it >= 0 } ?: 0

    fun isNotEmpty() = queue.isNotEmpty()

    fun isEmpty() = queue.isEmpty()

    fun getCurrent(): Uri? = currentItem

    fun setPlaylist(items: List<Uri>) {
        if (items == queue) return
        queue.clear()
        queue.addAll(items)
    }

    fun updateCurrent(uri: Uri) {
        currentItem = uri
    }

    fun clearQueue() {
        queue.clear()
        currentItem = null
    }

    override fun toString(): String = buildString {
        append("########## playlist ##########\n")
        queue.forEach { append(it.toString() + "\n") }
        append("##############################")
    }
}
