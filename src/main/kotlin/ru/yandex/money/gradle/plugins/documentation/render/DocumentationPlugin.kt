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
class DocumentationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(AsciidoctorJBasePlugin::class.java)
            val pluginSettings = extensions.create("documentation", DocumentationExtension::class.java)
            tasks.create("documentationValidation", DocumentationValidation::class.java) {
                it.group = "documentation"
                it.description = "Validate documentation files"
            }
            tasks.create("convertDiagrams", DocumentationConvertDiagramTask::class.java) {
                it.group = "documentation"
                it.description = "Convert plantuml diagrams into png images"
            }
            tasks.create("documentationPreprocess", DocumentationPreprocessTask::class.java) {
                it.group = "documentation"
                it.description = "Make a preprocessing for the documentation files"
            }
            val documentationRender = tasks.create("documentationRender", AsciidoctorTask::class.java) {
                it.sourceDir(file("."))
                it.setOutputDir(file("."))
            }
            val commitEditedDocumentation = tasks.create("commitEditedDocumentation", DocumentationCommitTask::class.java) {
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
