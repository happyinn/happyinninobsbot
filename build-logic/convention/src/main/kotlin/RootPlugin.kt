import com.obsbot.happyinn.apps.happyinninobsbot.configureGraphTasks
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 根项目插件，用于配置整个项目的通用设置
 */
class RootPlugin : Plugin<Project> {
    /**
     * 应用插件时执行的逻辑
     * @param target 当前应用插件的项目对象
     */
    override fun apply(target: Project) {
        // 确保此插件只能应用于根项目（路径为":"表示根项目）
        require(target.path == ":")

        // 对所有子项目应用 configureGraphTasks 配置
        target.subprojects {
            configureGraphTasks()
        }
    }
}
