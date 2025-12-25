package com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.impl

import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.FollowableTopic
import kotlin.collections.any

/**
 * 首次使用引导界面的状态密封接口
 *
 * 定义了"为你推荐"页面中首次使用引导流程的各种可能状态
 * 使用密封接口（sealed interface）确保所有可能的状态都被明确枚举
 */
sealed interface OnboardingUiState {
    /**
     * 加载中的引导状态
     *
     * 当应用正在加载引导数据（如可关注的主题列表）时使用此状态
     */
    data object Loading : OnboardingUiState

    /**
     * 加载失败的引导状态
     *
     * 当应用无法加载引导数据时使用此状态，通常需要显示错误提示或重试选项
     */
    data object LoadFailed : OnboardingUiState

    /**
     * 不需要显示引导的状态
     *
     * 当用户已经完成过引导流程或已设置为不再显示引导时使用此状态
     */
    data object NotShown : OnboardingUiState

    /**
     * 需要显示引导的状态，包含可选择的主题列表
     *
     * @property topics 可关注的主题列表，用户可以从中选择感兴趣的主题
     */
    data class Shown(
        val topics: List<FollowableTopic>,
    ) : OnboardingUiState {
        /**
         * 判断引导界面是否可以被关闭
         *
         * 只有当用户至少关注了一个主题时，引导界面才允许被关闭
         */
        val isDismissable: Boolean get() = topics.any { it.isFollowed }
    }
}