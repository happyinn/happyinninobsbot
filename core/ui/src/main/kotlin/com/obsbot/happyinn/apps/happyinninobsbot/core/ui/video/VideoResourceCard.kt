package com.obsbot.happyinn.apps.happyinninobsbot.core.ui.video

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R

/**
 * 展开式新闻资源卡片组�?
 *
 * 显示一个包含完整新闻资源信息的卡片，包括标题、头部图片、书签按钮、元数据�?
 * 简短描述和主题标签。支持点击操作、书签切换和拖拽分享功能�?
 *
 * @param onClick 点击卡片的回调函数
 * @param modifier 修饰器
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoResourceCardExpanded(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 定义可访问性标签和分享内容
    val clickActionLabel = stringResource(R.string.core_ui_card_tap_action)


    // 设置拖拽标志，根据Android版本适配
    val dragAndDropFlags = if (VERSION.SDK_INT >= VERSION_CODES.N) {
        View.DRAG_FLAG_GLOBAL
    } else {
        0
    }

    // 卡片组件
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        // 使用自定义标签为无障碍服务提供按钮动作的描述
        modifier = modifier
            .semantics {
                onClick(label = clickActionLabel, action = null)
            }
    ) {
        //整个是一层纵向布局的容器
        Column {
            //顶部图片
            // 如果新闻资源有头部图片URL，则显示头部图片
            // 下部内容
            Box(
                modifier = Modifier.padding(16.dp),
            ) {
                //又是一个纵向布局
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    //显示一行标题和收藏按钮
                    Row {

                    }

                }
            }
        }
    }
}