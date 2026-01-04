/**
 * 媒体布局模式相关的扩展函数
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaLayoutMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R

/**
 * 获取媒体布局模式的显示名称
 * @return 媒体布局模式的本地化字符串
 */
@Composable
fun MediaLayoutMode.name(): String {
    return when (this) {
        MediaLayoutMode.LIST -> stringResource(id = R.string.list) // 列表布局
        MediaLayoutMode.GRID -> stringResource(id = R.string.grid) // 网格布局
    }
}
