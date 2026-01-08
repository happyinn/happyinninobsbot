package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.convertToUTF8
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.getFilenameFromUri
import java.nio.charset.Charset

/**
 * Uri 扩展函数，提供与媒体播放相关的 Uri 处理功能
 * - 字幕 MIME 类型判断
 * - Content URI 判断
 * - Uri 转字幕配置
 * - 兼容不同 Android 版本的 Parcelable Uri 数组获取
 */

/**
 * 根据 Uri 路径获取字幕文件的 MIME 类型
 * @return 字幕文件的 MIME 类型
 */
fun Uri.getSubtitleMime(): String {
    return when {
        // SSA/ASS 字幕格式
        path?.endsWith(".ssa") == true || path?.endsWith(".ass") == true -> {
            MimeTypes.TEXT_SSA
        }

        // WebVTT 字幕格式
        path?.endsWith(".vtt") == true -> {
            MimeTypes.TEXT_VTT
        }

        // TTML/DFXP 字幕格式
        path?.endsWith(".ttml") == true || path?.endsWith(".xml") == true || path?.endsWith(".dfxp") == true -> {
            MimeTypes.APPLICATION_TTML
        }

        // 默认使用 SRT 字幕格式
        else -> {
            MimeTypes.APPLICATION_SUBRIP
        }
    }
}

/**
 * 判断 Uri 是否使用 Content 协议
 * @return 是否为 Content URI
 */
val Uri.isSchemaContent: Boolean
    get() = ContentResolver.SCHEME_CONTENT.equals(scheme, ignoreCase = true)

/**
 * 将 Uri 转换为 Media3 字幕配置
 * @param uri 字幕文件 Uri
 * @param subtitleEncoding 字幕编码格式
 * @param isSelected 是否默认选中该字幕
 * @return 配置好的字幕配置对象
 */
suspend fun Context.uriToSubtitleConfiguration(
    uri: Uri,
    subtitleEncoding: String = "",
    isSelected: Boolean = false,
): MediaItem.SubtitleConfiguration {
    // 确定字幕字符集
    val charset = if (subtitleEncoding.isNotEmpty() && Charset.isSupported(subtitleEncoding)) {
        Charset.forName(subtitleEncoding)
    } else {
        null
    }
    // 获取字幕文件名作为标签
    val label = getFilenameFromUri(uri)
    // 获取字幕 MIME 类型
    val mimeType = uri.getSubtitleMime()
    // 转换为 UTF-8 编码的 Uri
    val utf8ConvertedUri = convertToUTF8(uri = uri, charset = charset)
    // 构建并返回字幕配置
    return MediaItem.SubtitleConfiguration.Builder(utf8ConvertedUri).apply {
        setId(uri.toString())
        setMimeType(mimeType)
        setLabel(label)
        // 如果默认选中，设置选中标志
        if (isSelected) setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
    }.build()
}

/**
 * 兼容不同 Android 版本的 Parcelable Uri 数组获取
 * @param key Bundle 中的键
 * @return Uri 数组，可能为 null
 */
@Suppress("DEPRECATION")
fun Bundle.getParcelableUriArray(key: String): Array<out Parcelable>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13+ 使用类型安全的 getParcelableArray 方法
        getParcelableArray(key, Uri::class.java)
    } else {
        // 旧版本使用非类型安全的 getParcelableArray 方法
        getParcelableArray(key)
    }
}
