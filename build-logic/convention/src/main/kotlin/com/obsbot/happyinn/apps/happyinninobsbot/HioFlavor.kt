package com.obsbot.happyinn.apps.happyinninobsbot

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

/**
 * 风味维度枚举类
 * 定义应用的不同维度分类
 */
@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType  // 内容类型维度
}

/**
 * 应用风味枚举类
 * 定义应用的不同产品风味配置
 *
 * The content for the app can either come from local static data which is useful for demo
 * purposes, or from a production backend server which supplies up-to-date, real content.
 * These two product flavors reflect this behaviour.
 *
 * 应用的内容可以来自本地静态数据（适用于演示目的），
 * 或者来自生产后端服务器（提供最新、真实的内容）。
 * 这两种产品风味反映了这种行为。
 */
@Suppress("EnumEntryName")
enum class HioFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    // 演示风味：使用本地静态数据，应用ID后缀为".demo"
    demo(FlavorDimension.contentType, applicationIdSuffix = ".demo"),
    // 生产风味：使用真实后端数据，无应用ID后缀
    prod(FlavorDimension.contentType),
}

/**
 * 配置应用的产品风味
 *
 * @param commonExtension 通用扩展配置
 * @param flavorConfigurationBlock 风味配置块，允许自定义每个风味的具体配置
 */
fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: HioFlavor) -> Unit = {},
) {
    commonExtension.apply {
        // 遍历所有风味维度并添加到配置中
        FlavorDimension.values().forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        // 配置产品风味
        productFlavors {
            // 遍历所有Nia风味配置
            HioFlavor.values().forEach { niaFlavor ->
                register(niaFlavor.name) {
                    // 设置风味所属的维度
                    dimension = niaFlavor.dimension.name
                    // 执行风味配置块
                    flavorConfigurationBlock(this, niaFlavor)
                    // 如果是应用扩展且是应用产品风味
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        // 如果风味有应用ID后缀，则设置
                        if (niaFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = niaFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}

