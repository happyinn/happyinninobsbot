/**
 * 排序相关的扩展函数
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Sort
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R

/**
 * 获取排序顺序的显示名称
 * @param sortBy 排序类型，如按标题、路径、长度等
 * @return 排序顺序的本地化字符串
 */
@Composable
fun Sort.Order.name(sortBy: Sort.By): String {
    // 根据排序类型和排序顺序获取对应的字符串资源ID
    val stringRes = when (sortBy) {
        // 按标题或路径排序时的顺序名称
        Sort.By.TITLE,
        Sort.By.PATH,
        -> when (this) {
            Sort.Order.ASCENDING -> R.string.a_z // 升序：A-Z
            Sort.Order.DESCENDING -> R.string.z_a // 降序：Z-A
        }
        // 按长度排序时的顺序名称
        Sort.By.LENGTH -> when (this) {
            Sort.Order.ASCENDING -> R.string.shortest // 升序：最短
            Sort.Order.DESCENDING -> R.string.longest // 降序：最长
        }
        // 按大小排序时的顺序名称
        Sort.By.SIZE -> when (this) {
            Sort.Order.ASCENDING -> R.string.smallest // 升序：最小
            Sort.Order.DESCENDING -> R.string.largest // 降序：最大
        }

        // 按日期排序时的顺序名称
        Sort.By.DATE -> when (this) {
            Sort.Order.ASCENDING -> R.string.oldest // 升序：最旧
            Sort.Order.DESCENDING -> R.string.newest // 降序：最新
        }
    }

    // 返回本地化的字符串
    return stringResource(stringRes)
}
