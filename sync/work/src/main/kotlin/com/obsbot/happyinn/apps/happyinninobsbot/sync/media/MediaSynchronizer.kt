package com.obsbot.happyinn.apps.happyinninobsbot.sync.media

/**
 * 媒体同步器接口
 * 定义了媒体文件同步的核心方法
 */
interface MediaSynchronizer {
    /**
     * 刷新媒体库
     * @param path 可选路径，指定要刷新的特定目录
     * @return 是否成功刷新
     */
    suspend fun refresh(path: String? = null): Boolean
    
    /**
     * 启动媒体同步
     */
    fun startSync()
    
    /**
     * 停止媒体同步
     */
    fun stopSync()
}
