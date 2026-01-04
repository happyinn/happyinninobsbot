/**
 * 媒体视图模式相关的扩展函数
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaViewMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R

/**
 * 获取媒体视图模式的显示名称
 * @return 媒体视图模式的本地化字符串
 */
@Composable
fun MediaViewMode.name(): String {
    return when (this) {
        MediaViewMode.VIDEOS -> stringResource(id = R.string.videos) // 视频模式
        MediaViewMode.FOLDERS -> stringResource(id = R.string.folders) // 文件夹模式
        MediaViewMode.FOLDER_TREE -> stringResource(id = R.string.tree) // 文件夹树模式
    }
}
