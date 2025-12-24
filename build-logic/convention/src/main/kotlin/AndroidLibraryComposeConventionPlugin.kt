
// 导入Android库扩展配置相关的DSL接口
import com.android.build.api.dsl.LibraryExtension
// 导入自定义的Compose配置函数
import com.obsbot.happyinn.apps.happyinninobsbot.configureAndroidCompose

// 导入Gradle插件基础接口
import org.gradle.api.Plugin
// 导入Gradle项目对象
import org.gradle.api.Project
// 导入Gradle Kotlin DSL的apply扩展函数，用于应用插件
import org.gradle.kotlin.dsl.apply
// 导入Gradle Kotlin DSL的getByType扩展函数，用于获取特定类型的扩展对象
import org.gradle.kotlin.dsl.getByType

/**
 * Android库Compose约定插件
 *
 * 该插件用于为Android库项目配置Jetpack Compose支持，
 * 自动应用必要的插件并配置Compose相关设置
 */
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    /**
     * 应用插件的核心方法
     *
     * @param target 需要应用此插件的Gradle项目对象
     */
    override fun apply(target: Project) {
        // 使用with语法简化对target项目的操作
        with(target) {
            // 应用Android Library插件，为项目提供Android库构建能力
            apply(plugin = "com.android.library")
            // 应用Kotlin Compose插件，为项目提供Compose编译器支持
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            // 获取项目的LibraryExtension扩展对象，用于配置Android库相关属性
            val extension = extensions.getByType<LibraryExtension>()
            // 调用自定义函数配置Android Compose相关设置
            configureAndroidCompose(extension)
        }
    }
}
