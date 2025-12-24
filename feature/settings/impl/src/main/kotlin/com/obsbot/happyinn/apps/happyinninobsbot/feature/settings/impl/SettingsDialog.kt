@file:Suppress("ktlint:standard:max-line-length")
package com.obsbot.happyinn.apps.happyinninobsbot.feature.settings.impl

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioTextButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioTheme
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.supportsDynamicTheming
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.DarkThemeConfig
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.DarkThemeConfig.LIGHT
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.DarkThemeConfig.FOLLOW_SYSTEM
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.DarkThemeConfig.DARK

import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.ThemeBrand
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.ThemeBrand.DEFAULT
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.ThemeBrand.ANDROID
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.TrackScreenViewEvent
import kotlin.jvm.java
import com.obsbot.happyinn.apps.happyinninobsbot.feature.settings.impl.R.string
import com.obsbot.happyinn.apps.happyinninobsbot.feature.settings.impl.SettingsUiState.Loading


/**
 * 设置对话框入口组件
 *
 * 这是一个组合函数，用于创建设置对话框的入口点，它会连接到 [SettingsViewModel] 并收集状态
 *
 * @param onDismiss 关闭对话框的回调函数
 * @param viewModel 设置界面的 ViewModel，默认通过 Hilt 注入
 */
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    // 收集设置界面的状态，使用collectAsStateWithLifecycle确保在生命周期内正确收集和取消收集
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()
    
    // 调用实际的设置对话框实现组件，将收集的状态传递给它
    SettingsDialog(
        onDismiss = onDismiss,
        settingsUiState = settingsUiState,
        onChangeThemeBrand = viewModel::updateThemeBrand,
        onChangeDynamicColorPreference = viewModel::updateDynamicColorPreference,
        onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
    )
}

/**
 * 设置对话框主组件
 * 实现设置对话框的主要逻辑和UI，包括加载状态处理、设置面板显示和链接面板
 *
 * @param settingsUiState 设置界面的 UI 状态，包含加载或成功状态
 * @param supportDynamicColor 设备是否支持动态颜色，默认通过 [supportsDynamicTheming] 函数获取
 * @param onDismiss 关闭对话框的回调函数
 * @param onChangeThemeBrand 更改主题品牌的回调函数
 * @param onChangeDynamicColorPreference 更改动态颜色偏好的回调函数
 * @param onChangeDarkThemeConfig 更改深色主题配置的回调函数
 */
@Composable
fun SettingsDialog(
    settingsUiState: SettingsUiState,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onDismiss: () -> Unit,
    onChangeThemeBrand: (themeBrand: ThemeBrand) -> Unit,
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
    // 获取本地配置，用于设置对话框的最大宽度
    val configuration = LocalConfiguration.current

    /**
     * usePlatformDefaultWidth = false 是一个临时修复方案，允许在重新组合期间重新计算高度。
     * 然而，这会导致对话框在紧凑模式下占据全宽。因此下面配置了最大宽度。
     * 当 https://issuetracker.google.com/issues/221643630 有修复时应该移除此设置。
     */
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(string.feature_settings_impl_title),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            // 添加水平分隔线，分隔标题和内容
            HorizontalDivider()
            
            // 使用垂直滚动视图确保内容超出时可滚动
            Column(Modifier.verticalScroll(rememberScrollState())) {
                // 根据状态显示不同内容
                when (settingsUiState) {
                    SettingsUiState.Loading -> {
                        // 加载状态：显示加载文本
                        Text(
                            text = stringResource(string.feature_settings_impl_loading),
                            modifier = Modifier.padding(vertical = 16.dp),
                        )
                    }

                    is SettingsUiState.Success -> {
                        // 成功状态：显示设置面板
                        SettingsPanel(
                            settings = settingsUiState.settings,
                            supportDynamicColor = supportDynamicColor,
                            onChangeThemeBrand = onChangeThemeBrand,
                            onChangeDynamicColorPreference = onChangeDynamicColorPreference,
                            onChangeDarkThemeConfig = onChangeDarkThemeConfig,
                        )
                    }
                }
                
                // 添加底部分隔线，分隔设置和链接面板
                HorizontalDivider(Modifier.padding(top = 8.dp))
                
                // 显示链接面板
                LinksPanel()
            }
            
            // 跟踪屏幕视图事件，用于分析
            TrackScreenViewEvent(screenName = "Settings")
        },
        confirmButton = {
            // 关闭按钮
            HioTextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(horizontal = 8.dp),
            ) {
                Text(
                    text = stringResource(string.feature_settings_impl_dismiss_dialog_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}

// [ColumnScope] 用于使用 [ColumnScope.AnimatedVisibility] 扩展重载可组合函数。
/**
 * 设置面板组件
 *
 * 包含主题选择、动态颜色设置和深色模式配置的面板
 *
 * @param settings 用户可编辑的设置项，包含品牌、深色主题配置和动态颜色偏好
 * @param supportDynamicColor 设备是否支持动态颜色
 * @param onChangeThemeBrand 更改主题品牌的回调函数
 * @param onChangeDynamicColorPreference 更改动态颜色偏好的回调函数
 * @param onChangeDarkThemeConfig 更改深色主题配置的回调函数
 */
@Composable
private fun ColumnScope.SettingsPanel(
    settings: UserEditableSettings,
    supportDynamicColor: Boolean,
    onChangeThemeBrand: (themeBrand: ThemeBrand) -> Unit,
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
    // 主题设置部分
    SettingsDialogSectionTitle(text = stringResource(string.feature_settings_impl_theme))
    Column(Modifier.selectableGroup()) {
        SettingsDialogThemeChooserRow(
            text = stringResource(string.feature_settings_impl_brand_default),
            selected = settings.brand == ThemeBrand.DEFAULT,
            onClick = { onChangeThemeBrand(DEFAULT) },
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(string.feature_settings_impl_brand_android),
            selected = settings.brand == ANDROID,
            onClick = { onChangeThemeBrand(ANDROID) },
        )
    }

    // 动态颜色设置部分（仅当使用默认主题且设备支持时显示）
    AnimatedVisibility(visible = settings.brand == DEFAULT && supportDynamicColor) {
        Column {
            SettingsDialogSectionTitle(text = stringResource(string.feature_settings_impl_dynamic_color_preference))
            Column(Modifier.selectableGroup()) {
                SettingsDialogThemeChooserRow(
                    text = stringResource(string.feature_settings_impl_dynamic_color_yes),
                    selected = settings.useDynamicColor,
                    onClick = { onChangeDynamicColorPreference(true) },
                )
                SettingsDialogThemeChooserRow(
                    text = stringResource(string.feature_settings_impl_dynamic_color_no),
                    selected = !settings.useDynamicColor,
                    onClick = { onChangeDynamicColorPreference(false) },
                )
            }
        }
    }

    // 深色模式设置部分
    SettingsDialogSectionTitle(text = stringResource(string.feature_settings_impl_dark_mode_preference))
    Column(Modifier.selectableGroup()) {
        SettingsDialogThemeChooserRow(
            text = stringResource(string.feature_settings_impl_dark_mode_config_system_default),
            selected = settings.darkThemeConfig == FOLLOW_SYSTEM,
            onClick = { onChangeDarkThemeConfig(FOLLOW_SYSTEM) },
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(string.feature_settings_impl_dark_mode_config_light),
            selected = settings.darkThemeConfig == DarkThemeConfig.LIGHT,
            onClick = { onChangeDarkThemeConfig(LIGHT) },
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(string.feature_settings_impl_dark_mode_config_dark),
            selected = settings.darkThemeConfig == DARK,
            onClick = { onChangeDarkThemeConfig(DARK) },
        )
    }
}

/**
 * 设置对话框章节标题组件
 *
 * 用于显示设置对话框中各部分的标题，如"主题"、"深色模式偏好"等
 *
 * @param text 标题文本内容
 */
@Composable
private fun SettingsDialogSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

/**
 * 设置对话框主题选择行组件
 *
 * 实现一个可选择的行，包含单选按钮和文本，用于主题选择
 *
 * @param text 选项显示的文本内容
 * @param selected 该选项是否被选中
 * @param onClick 点击该行时的回调函数
 */
@Composable
fun SettingsDialogThemeChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null, // 点击由父级Row的selectable处理
        )
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

/**
 * 链接面板组件，包含隐私政策、许可证等链接
 *
 * 显示设置对话框底部的链接，如隐私政策、许可证、品牌指南和反馈链接
 */
@OptIn(ExperimentalLayoutApi::class) // 使用实验性的FlowRow API
@Composable
private fun LinksPanel() {
    // 使用FlowRow自动换行排列按钮
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        // 获取Uri处理器用于打开链接
        val uriHandler = LocalUriHandler.current
        
        // 隐私政策按钮
        HioTextButton(
            onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) },
        ) {
            Text(text = stringResource(string.feature_settings_impl_privacy_policy))
        }
        
        // 获取上下文用于启动活动
        val context = LocalContext.current
        
        // 开源许可证按钮
        HioTextButton(
            onClick = {
                // 启动开源许可证菜单活动
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            },
        ) {
            Text(text = stringResource(string.feature_settings_impl_licenses))
        }
        
        // 品牌指南按钮
        HioTextButton(
            onClick = { uriHandler.openUri(BRAND_GUIDELINES_URL) },
        ) {
            Text(text = stringResource(string.feature_settings_impl_brand_guidelines))
        }
        
        // 反馈按钮
        HioTextButton(
            onClick = { uriHandler.openUri(FEEDBACK_URL) },
        ) {
            Text(text = stringResource(string.feature_settings_impl_feedback))
        }
    }
}

/**
 * 设置对话框预览 - 成功状态
 *
 * 用于在开发阶段预览设置对话框的UI（成功加载状态）
 */
@Preview
@Composable
private fun PreviewSettingsDialog() {
    HioTheme {
        SettingsDialog(
            onDismiss = {},
            settingsUiState = SettingsUiState.Success(
                UserEditableSettings(
                    brand = DEFAULT,
                    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                    useDynamicColor = false,
                ),
            ),
            onChangeThemeBrand = {},
            onChangeDynamicColorPreference = {},
            onChangeDarkThemeConfig = {},
        )
    }
}

/**
 * 设置对话框预览 - 加载状态
 *
 * 用于在开发阶段预览设置对话框加载状态的UI
 */
@Preview
@Composable
private fun PreviewSettingsDialogLoading() {
    HioTheme {
        SettingsDialog(
            onDismiss = {},
            settingsUiState = Loading,
            onChangeThemeBrand = {},
            onChangeDynamicColorPreference = {},
            onChangeDarkThemeConfig = {},
        )
    }
}

// 隐私政策链接地址
private const val PRIVACY_POLICY_URL = "https://policies.google.com/privacy"

// 品牌指南链接地址
private const val BRAND_GUIDELINES_URL = "https://developer.android.com/distribute/marketing-tools/brand-guidelines"

// 反馈链接地址
private const val FEEDBACK_URL = "https://goo.gle/Hio-app-feedback"

