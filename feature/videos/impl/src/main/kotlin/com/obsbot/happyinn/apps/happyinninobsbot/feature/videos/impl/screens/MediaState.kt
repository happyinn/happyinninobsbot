/**
 * 媒体状态密封接口，用于表示媒体加载的不同状态
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.screens
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Folder

/**
 * 媒体状态密封接口，定义了媒体加载的不同状态
 */
sealed interface MediaState {
    /**
     * 媒体正在加载中的状态
     */
    data object Loading : MediaState
    
    /**
     * 媒体加载成功的状态
     * @param data 加载成功的媒体文件夹数据，可能为null
     */
    data class Success(val data: Folder?) : MediaState
}
