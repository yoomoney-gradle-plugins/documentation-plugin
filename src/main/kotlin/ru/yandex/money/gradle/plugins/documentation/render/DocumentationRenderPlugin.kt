package ru.yandex.money.gradle.plugins.documentation.render

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
            val pluginSettings = extensions.create("documentations", DocumentationRenderExtension::class.java)
            afterEvaluate {
                tasks.create("documentationValidation", DocumentationRenderValidation::class.java) {
                    it.group = "documentation"
                    it.description = "Validate documentation files"
                }
            }
            afterEvaluate {
                tasks.create("convertDiagrams", DocumentationRenderConvertDiagramTask::class.java) {
                    it.group = "documentation"
                    it.description = "Convert plantuml diagrams into png images"
                }
            }
            afterEvaluate {
                tasks.create("documentationPreprocess", DocumentationRenderPreprocessTask::class.java) {
                    it.group = "documentation"
                    it.description = "Make a preprocessing for the documentation files"
                }
            }
            afterEvaluate {
                tasks.create("documentationRender", AsciidoctorTask::class.java) {
                    it.sourceDir(file("."))
                    it.setOutputDir(file("."))
                    it.sources {
                        pluginSettings.rootFiles.forEach { file -> it.include(file) }
                    }
                }
            }
            afterEvaluate {
                tasks.create("commitEditedDocumentation", DocumentationRenderCommitTask::class.java) {
                    it.group = "documentation"
                    it.description = "Commit modified documentations"
                    it.rootFiles = pluginSettings.rootFiles
                }
            }
        }
    }
}
