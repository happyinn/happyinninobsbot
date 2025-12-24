package com.obsbot.happyinn.apps.happyinninobsbot.core.analytics

/**
 * 分析事件数据类
 *
 * 用于表示一个分析事件，包含事件类型和附加参数
 *
 * @param type 事件类型。尽可能使用标准事件类型 [Types] 中定义的值，
 *             如果没有合适的标准事件类型，可以定义自定义事件，但需要在后端分析系统中进行配置
 *             （例如，创建 Firebase Analytics 自定义事件）
 * @param extras 为事件提供额外上下文的参数列表。详情请参见 [Param]
 */
data class AnalyticsEvent(
    val type: String,
    val extras: List<Param> = emptyList(),
) {
    /**
     * 标准分析事件类型
     *
     * 定义了应用中常用的分析事件类型常量
     */
    class Types {
        companion object {
            /**
             * 页面浏览事件
             *
             * 用于记录用户查看特定页面的行为
             * 额外参数：SCREEN_NAME - 页面名称
             */
            const val SCREEN_VIEW = "screen_view" // (extras: SCREEN_NAME)
        }
    }

    /**
     * 分析事件参数数据类
     *
     * 用于为分析事件提供键值对形式的额外上下文信息
     *
     * @param key 参数键名。尽可能使用标准参数键 [ParamKeys] 中定义的值，
     *            如果没有合适的标准参数键，可以定义自己的参数键，
     *            但需要在后端分析系统中进行配置（例如，创建 Firebase Analytics 自定义参数）
     * @param value 参数值
     */
    data class Param(val key: String, val value: String)

    /**
     * 标准参数键名
     *
     * 定义了应用中常用的分析事件参数键名常量
     */
    class ParamKeys {
        companion object {
            /**
             * 页面名称参数键
             *
             * 用于在页面浏览事件中标识具体的页面名称
             */
            const val SCREEN_NAME = "screen_name"
        }
    }
}
