package ru.yoomoney.gradle.plugins.documentation.render

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

/**
 * Gradle task для препроцессинга файлов документации:
 * * Добавляет путь до директории файла относительно корня проекта при обращении к ресурсам
 * @author Igor Popov
 * @since 06.11.2020
 */
open class DocumentationPreprocessTask : DefaultTask() {
    @TaskAction
    fun taskAction() {
        val rootPath = project.file(".").toPath()
        Files.walk(rootPath)
                .filter {
                    it.fileName.toString().endsWith(".adoc")
                }
                .forEach { file ->
                    val adocFile = project.file(file.toString())
                    val pathToDir = rootPath.relativize(file.parent).toString().replace("\\", "/")
                    adocFile.writeText(adocFile.readText().replace("resources/", "$pathToDir/resources/"))
                }
    }
}
