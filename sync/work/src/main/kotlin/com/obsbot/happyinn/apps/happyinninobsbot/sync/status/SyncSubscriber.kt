package com.obsbot.happyinn.apps.happyinninobsbot.sync.status

/**
 * 同步订阅器接口
 * 用于订阅后端请求的同步操作
 */
interface SyncSubscriber {
    /**
     * 订阅同步事件的挂起函数
     * 实现类需要提供具体的订阅逻辑
     */
    suspend fun subscribe()
}
