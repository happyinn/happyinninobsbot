/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//*


// 添加OptIn注解以使用实验性API
@file:OptIn(kotlin.ExperimentalStdlibApi::class)

package com.obsbot.happyinn.apps.happyinninobsbot

import com.android.utils.associateWithNotNull
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity.NONE
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import kotlin.text.RegexOption.DOT_MATCHES_ALL
import com.obsbot.happyinn.apps.happyinninobsbot.PluginType.Unknown

*/
/**
 * 生成模块依赖图的任务，包括 `graphDump` 和 `graphUpdate` 两个任务
 *
 * 实现说明：
 * - [Graph.invoke] 使用递归方式搜索项目依赖（虽然实际上不会导致栈溢出）
 * - [Graph.invoke] 每次都会重新执行整个过程，不复用中间结果
 * - [Graph.invoke] 总是在Gradle配置阶段执行（通常每个项目耗时不到1毫秒）
 *
 * 可通过 `graph.ignoredProjects` 和 `graph.supportedConfigurations` 属性配置图表生成
 *//*

private class Graph(
    private val root: Project,
    private val dependencies: MutableMap<Project, Set<Pair<Configuration, Project>>> = mutableMapOf(),
    private val plugins: MutableMap<Project, PluginType> = mutableMapOf(),
    private val seen: MutableSet<String> = mutableSetOf(),
) {

    // 忽略的项目列表，默认为空
    private val ignoredProjects = root.providers.gradleProperty("graph.ignoredProjects")
        .map { it.split(",").toSet() }
        .orElse(emptySet())

    // 支持的配置类型，默认包括api、implementation等
    private val supportedConfigurations =
        root.providers.gradleProperty("graph.supportedConfigurations")
            .map { it.split(",").toSet() }
            .orElse(setOf("api", "implementation", "baselineProfile", "testedApks"))

    */
/**
     * 遍历并收集项目的依赖关系
     *//*

    operator fun invoke(project: Project = root): Graph {
        if (project.path in seen) return this // 如果已经处理过该项目，则直接返回
        seen += project.path // 标记为已处理

        // 获取项目应用的插件类型
        plugins.putIfAbsent(
            project,
            PluginType.entries.firstOrNull { project.pluginManager.hasPlugin(it.id) } ?: Unknown,
        )

        dependencies.compute(project) { _, u -> u.orEmpty() } // 初始化当前项目的依赖集合

        // 遍历支持的配置，查找项目依赖
        project.configurations
            .matching { it.name in supportedConfigurations.get() }
            .associateWithNotNull { it.dependencies.withType<ProjectDependency>().ifEmpty { null } }
            .flatMap { (c, value) -> value.map { dep -> c to project.project(dep.path) } }
            .filter { (_, p) -> p.path !in ignoredProjects.get() }
            .forEach { (configuration: Configuration, projectDependency: Project) ->
                dependencies.compute(project) { _, u -> u.orEmpty() + (configuration to projectDependency) }
                invoke(projectDependency) // 递归处理依赖项目
            }
        return this
    }
    */
/**
     * 返回依赖关系映射表
     *//*

    fun dependencies(): Map<String, Set<Pair<String, String>>> = dependencies
        .mapKeys { it.key.path }
        .mapValues { it.value.mapTo(mutableSetOf()) { (c, p) -> c.name to p.path } }

    */
/**
     * 返回插件类型映射表
     *//*

    fun plugins() = plugins.mapKeys { it.key.path }
}

*/
/**
 * 插件类型枚举，定义了不同类型的模块及其样式
 * 声明顺序很重要，因为只有第一个匹配项会被保留
 *//*

internal enum class PluginType(val id: String, val ref: String, val style: String) {
    AndroidApplication(
        id = "nowinandroid.android.application",
        ref = "android-application",
        style = "fill:#CAFFBF,stroke:#000,stroke-width:2px,color:#000",
    ),
    AndroidFeature(
        id = "nowinandroid.android.feature",
        ref = "android-feature",
        style = "fill:#FFD6A5,stroke:#000,stroke-width:2px,color:#000",
    ),
    AndroidLibrary(
        id = "nowinandroid.android.library",
        ref = "android-library",
        style = "fill:#9BF6FF,stroke:#000,stroke-width:2px,color:#000",
    ),
    AndroidTest(
        id = "nowinandroid.android.test",
        ref = "android-test",
        style = "fill:#A0C4FF,stroke:#000,stroke-width:2px,color:#000",
    ),
    Jvm(
        id = "nowinandroid.jvm.library",
        ref = "jvm-library",
        style = "fill:#BDB2FF,stroke:#000,stroke-width:2px,color:#000",
    ),
    Unknown(
        id = "?",
        ref = "unknown",
        style = "fill:#FFADAD,stroke:#000,stroke-width:2px,color:#000",
    ),
}

*/
/**
 * 为项目配置图形化依赖任务
 *//*

internal fun Project.configureGraphTasks() {
    if (!buildFile.exists()) return // 忽略没有构建文件的根模块

    // 注册graphDump任务，用于生成依赖图数据
    val dumpTask = tasks.register<GraphDumpTask>("graphDump") {
        val graph = Graph(this@configureGraphTasks).invoke()
        projectPath = this@configureGraphTasks.path
        dependencies = graph.dependencies()
        plugins = graph.plugins()
        output = this@configureGraphTasks.layout.buildDirectory.file("mermaid/graph.txt")
        legend = this@configureGraphTasks.layout.buildDirectory.file("mermaid/legend.txt")
    }

    // 注册graphUpdate任务，用于更新README.md中的依赖图
    tasks.register<GraphUpdateTask>("graphUpdate") {
        projectPath = this@configureGraphTasks.path
        input = dumpTask.flatMap { it.output }
        legend = dumpTask.flatMap { it.legend }
        output = this@configureGraphTasks.layout.projectDirectory.file("README.md")
    }
}

*/
/**
 * 图形转储任务，将依赖关系导出为Mermaid格式
 *//*

@CacheableTask
private abstract class GraphDumpTask : DefaultTask() {

    @get:Input
    abstract val projectPath: Property<String>

    @get:Input
    abstract val dependencies: MapProperty<String, Set<Pair<String, String>>>

    @get:Input
    abstract val plugins: MapProperty<String, PluginType>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @get:OutputFile
    abstract val legend: RegularFileProperty

    override fun getDescription() = "Dumps project dependencies to a mermaid file."

    @TaskAction
    operator fun invoke() {
        output.get().asFile.writeText(mermaid())
        legend.get().asFile.writeText(legend())
        logger.lifecycle(output.get().asFile.toPath().toUri().toString())
    }

    */
/**
     * 生成Mermaid格式的依赖图
     *//*

    private fun mermaid() = buildString {
        val dependencies: Set<Dependency> = dependencies.get()
            .flatMapTo(mutableSetOf()) { (project, entries) -> entries.map { it.toDependency(project) } }
        // FrontMatter configuration (not supported yet on GitHub.com)
        // 添加前端配置（GitHub尚不支持）
        appendLine(
            // language=YAML
            """
            ---
            config:
              layout: elk
              elk:
                nodePlacementStrategy: SIMPLE
            ---
            """.trimIndent(),
        )
        // Graph declaration 图表声明
        appendLine("graph TB")

        // Nodes and subgraphs 节点和子图定义
        val (rootProjects, nestedProjects) = dependencies
            .map { listOf(it.project, it.dependency) }.flatten().toSet()
            .plus(projectPath.get()) // Special case when this specific module has no other dependency
            .groupBy { it.substringBeforeLast(":") }
            .entries.partition { it.key.isEmpty() }

        val orderedGroups = nestedProjects.groupBy {
            if (it.key.count { char -> char == ':' } > 1) it.key.substringBeforeLast(":") else ""
        }

        orderedGroups.forEach { (outerGroup, innerGroups) ->
            if (outerGroup.isNotEmpty()) {
                appendLine("  subgraph $outerGroup")
                appendLine("    direction TB")
            }
            innerGroups.sortedWith(
                compareBy(
                    { (group, _) ->
                        dependencies.filter { dep ->
                            val toGroup = dep.dependency.substringBeforeLast(":")
                            toGroup == group && dep.project.substringBeforeLast(":") != group
                        }.count()
                    },
                    { -it.value.size },
                ),
            ).forEach { (group, projects) ->
                val indent = if (outerGroup.isNotEmpty()) 4 else 2
                appendLine(" ".repeat(indent) + "subgraph $group")
                appendLine(" ".repeat(indent) + "  direction TB")
                projects.sorted().forEach {
                    appendLine(it.alias(indent = indent + 2, plugins.get().getValue(it)))
                }
                appendLine(" ".repeat(indent) + "end")
            }
            if (outerGroup.isNotEmpty()) {
                appendLine("  end")
            }
        }

        rootProjects.flatMap { it.value }.sortedDescending().forEach {
            appendLine(it.alias(indent = 2, plugins.get().getValue(it)))
        }
        // Links 连接线
        if (dependencies.isNotEmpty()) appendLine()
        dependencies
            .sortedWith(compareBy({ it.project }, { it.dependency }, { it.configuration }))
            .forEach { appendLine(it.link(indent = 2)) }
        // Classes 类定义
        appendLine()
        PluginType.entries.forEach { appendLine(it.classDef()) }
    }
    */
/**
     * 生成图例
     *//*

    private fun legend() = buildString {
        appendLine("graph TB")
        listOf(
            "application" to PluginType.AndroidApplication,
            "feature" to PluginType.AndroidFeature,
            "library" to PluginType.AndroidLibrary,
            "jvm" to PluginType.Jvm,
        ).forEach { (name, type) ->
            appendLine(name.alias(indent = 2, type))
        }
        appendLine()
        listOf(
            Dependency("application", "implementation", "feature"),
            Dependency("library", "api", "jvm"),
        ).forEach {
            appendLine(it.link(indent = 2))
        }
        appendLine()
        PluginType.entries.forEach { appendLine(it.classDef()) }
    }

    private class Dependency(val project: String, val configuration: String, val dependency: String)

    private fun Pair<String, String>.toDependency(project: String) =
        Dependency(project, configuration = first, dependency = second)

    private fun String.alias(indent: Int, pluginType: PluginType): String = buildString {
        append(" ".repeat(indent))
        append(this@alias)
        append("[").append(substringAfterLast(":")).append("]:::")
        append(pluginType.ref)
    }

    private fun Dependency.link(indent: Int) = buildString {
        append(" ".repeat(indent))
        append(project).append(" ")
        append(
            when (configuration) {
                "api" -> "-->"          // API依赖使用实线箭头
                "implementation" -> "-.->"  // Implementation依赖使用虚线箭头
                else -> "-.->|$configuration|" // 其他配置使用带标签的虚线箭头
            },
        )
        append(" ").append(dependency)
    }

    private fun PluginType.classDef() = "classDef $ref $style;"
}
*/
/**
 * 更新任务，将生成的依赖图插入到README.md中
 *//*

@CacheableTask
private abstract class GraphUpdateTask : DefaultTask() {

    @get:Input
    abstract val projectPath: Property<String>

    @get:InputFile
    @get:PathSensitive(NONE)
    abstract val input: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(NONE)
    abstract val legend: RegularFileProperty

    @get:OutputFile
    abstract val output: RegularFileProperty

    override fun getDescription() = "Updates Markdown file with the corresponding dependency graph."

    @TaskAction
    operator fun invoke() = with(output.get().asFile) {
        if (!exists()) {
            createNewFile()
            writeText(
                """
                # `${projectPath.get()}`

                ## Module dependency graph

                <!--region graph--> <!--endregion-->

                """.trimIndent(),
            )
        }
        val mermaid = input.get().asFile.readText().trimTrailingNewLines()
        val legend = legend.get().asFile.readText().trimTrailingNewLines()
        val regex = """(<!--region graph-->)(.*?)(<!--endregion-->)""".toRegex(DOT_MATCHES_ALL)
        val text = readText().replace(regex) { match ->
            val (start, _, end) = match.destructured
            """
            |$start
            |```mermaid
            |$mermaid
            |```
            |
            |<details><summary>📋 Graph legend</summary>
            |
            |```mermaid
            |$legend
            |```
            |
            |</details>
            |$end
            """.trimMargin()
        }
        writeText(text)
    }

    private fun String.trimTrailingNewLines() = lines()
        .dropLastWhile(String::isBlank)
        .joinToString(System.lineSeparator())
}

*/
