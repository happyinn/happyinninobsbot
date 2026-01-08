package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions

/**
 * 枚举工具扩展：
 * 返回当前枚举的下一个枚举值（循环取值）
 */

inline fun <reified T : Enum<T>> T.next(): T {
    val values = enumValues<T>()
    val nextOrdinal = (ordinal + 1) % values.size
    return values[nextOrdinal]
}

