
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.obsbot.happyinn.apps.happyinninobsbot.configureFlavors
import com.obsbot.happyinn.apps.happyinninobsbot.configureGradleManagedDevices
import com.obsbot.happyinn.apps.happyinninobsbot.configureKotlinAndroid
import com.obsbot.happyinn.apps.happyinninobsbot.configurePrintApksTask
import com.obsbot.happyinn.apps.happyinninobsbot.disableUnnecessaryAndroidTests
import com.obsbot.happyinn.apps.happyinninobsbot.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Android Library模块的约定插件
 * 用于为所有Android库模块提供统一的配置
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    /**
     * 应用插件时执行的配置逻辑
     * @param target 当前应用此插件的Project对象
     */
    override fun apply(target: Project) {
        with(target) {
            // 应用基础的Android库插件
            apply(plugin = "com.android.library")
            // 应用Kotlin Android插件以支持Kotlin
            apply(plugin = "org.jetbrains.kotlin.android")
            // 应用自定义的Lint规则插件
            apply(plugin = "happyinninobsbot.android.lint")

            // 配置LibraryExtension扩展
            extensions.configure<LibraryExtension> {
                // 配置Kotlin Android相关设置
                configureKotlinAndroid(this)
                // 设置测试的目标SDK版本为36
                testOptions.targetSdk = 36
                // 设置Lint检查的目标SDK版本为36
                lint.targetSdk = 36
                // 设置默认配置的目标SDK版本为36
                defaultConfig.targetSdk = 36
                // 设置测试 instrumentation runner
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                // 禁用测试中的动画以提高测试稳定性
                testOptions.animationsDisabled = true
                // 配置产品风味(variants)
                configureFlavors(this)
                // 配置Gradle管理的设备用于测试
                configureGradleManagedDevices(this)
                // The resource prefix is derived from the module name,
                // so resources inside ":core:module1" must be prefixed with "core_module1_"
                // 根据模块路径生成资源前缀，确保资源命名唯一性
                // 例如":core:module1"模块中的资源必须以"core_module1_"开头
                resourcePrefix =
                    path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_")
                        .lowercase() + "_"
            }

            // 配置LibraryAndroidComponentsExtension扩展
            extensions.configure<LibraryAndroidComponentsExtension> {
                // 配置打印APK信息的任务
                configurePrintApksTask(this)
                // 禁用不必要的Android测试
                disableUnnecessaryAndroidTests(target)
            }

            // 配置依赖项
            dependencies {
//                "implementation"(libs.findLibrary("androidx.core.ktx").get())
                // 添加Android测试实现依赖
                "androidTestImplementation"(libs.findLibrary("kotlin.test").get())
                // 添加单元测试实现依赖
                "testImplementation"(libs.findLibrary("kotlin.test").get())

                // 添加核心追踪库的实现依赖
                "implementation"(libs.findLibrary("androidx.tracing.ktx").get())
            }
        }
    }
}
