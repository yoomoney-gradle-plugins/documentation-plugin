package ru.yandex.money.gradle.plugins.documentation.render

import org.asciidoctor.gradle.jvm.AsciidoctorJBasePlugin
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Конфигурация плагина
 * @author Igor Popov
 * @since 06.11.2020
 */
class DocumentationRenderPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(AsciidoctorJBasePlugin::class.java)
            val pluginSettings = extensions.create("documentation", DocumentationRenderExtension::class.java)
            tasks.create("documentationValidation", DocumentationRenderValidation::class.java) {
                it.group = "documentation"
                it.description = "Validate documentation files"
            }
            tasks.create("convertDiagrams", DocumentationRenderConvertDiagramTask::class.java) {
                it.group = "documentation"
                it.description = "Convert plantuml diagrams into png images"
            }
            tasks.create("documentationPreprocess", DocumentationRenderPreprocessTask::class.java) {
                it.group = "documentation"
                it.description = "Make a preprocessing for the documentation files"
            }
            val documentationRender = tasks.create("documentationRender", AsciidoctorTask::class.java) {
                it.sourceDir(file("."))
                it.setOutputDir(file("."))
            }
            val commitEditedDocumentation = tasks.create("commitEditedDocumentation", DocumentationRenderCommitTask::class.java) {
                it.group = "documentation"
                it.description = "Commit modified documentations"
            }
            afterEvaluate {
                documentationRender.sources {
                    pluginSettings.rootFiles.forEach { file -> it.include(file) }
                }
                commitEditedDocumentation.rootFiles = pluginSettings.rootFiles
            }
        }
    }
}
