// 导入Android构建API中的相关扩展类
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
// 导入Gradle插件相关类
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * Android Lint约定插件
 *
 * 该插件用于统一配置项目的Lint检查规则，根据项目类型（应用、库或独立Lint项目）
 * 应用相应的Lint配置
 */
class AndroidLintConventionPlugin : Plugin<Project> {
    /**
     * 应用插件的主要入口函数
     *
     * @param target 需要应用此插件的Gradle项目
     */
    override fun apply(target: Project) {
        with(target) {
            // 根据项目应用的不同插件类型，采用不同的Lint配置策略
            when {
                // 如果是Android应用程序项目
                pluginManager.hasPlugin("com.android.application") ->
                    configure<ApplicationExtension> { lint(Lint::configure) }

                // 如果是Android库项目
                pluginManager.hasPlugin("com.android.library") ->
                    configure<LibraryExtension> { lint(Lint::configure) }

                // 如果是独立的Lint项目
                else -> {
                    apply(plugin = "com.android.lint")
                    configure<Lint>(Lint::configure)
                }
            }
        }
    }
}

/**
 * Lint配置扩展函数
 *
 * 统一配置Lint检查的行为和报告选项
 */
private fun Lint.configure() {
    // 启用XML格式的Lint报告
    xmlReport = true
    // 启用SARIF格式的Lint报告（用于GitHub Code Scanning等工具）
    sarifReport = true
    // 检查依赖项中的问题
    checkDependencies = true
    // 禁用Gradle依赖版本检查警告（因为可能会误报）
    disable += "GradleDependency"
}
