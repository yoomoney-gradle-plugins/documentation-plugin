package ru.yoomoney.gradle.plugins.documentation.render

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

/**
 * Gradle task для валидации файлов документации
 *
 * @author Igor Popov
 * @since 06.11.2020
 */
open class DocumentationValidation : DefaultTask() {
    companion object {
        val ANCHOR_PATTERN = "^\\[\\[([^]]+)]]$".toRegex()
    }

    @TaskAction
    fun taskAction() {
        val anchors = mutableSetOf<String>()
        val rootPath = project.file(".").toPath()
        Files.walk(rootPath)
            .filter {
                it.fileName.toString().endsWith(".adoc")
            }
            .forEach { file ->
                val adocFile = project.file(file)
                adocFile.forEachLine { line ->
                    val anchor = ANCHOR_PATTERN.matchEntire(line)?.groupValues?.get(1)
                    if (anchor != null) {
                        if (anchors.contains(anchor)) {
                            throw GradleException("Duplicate anchor '$anchor' in '${adocFile.name}'")
                        } else {
                            anchors.add(anchor)
                        }
                    }
                }
            }
    }
}
