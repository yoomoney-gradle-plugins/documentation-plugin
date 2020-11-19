package ru.yandex.money.gradle.plugins.documentation.render

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths

/**
 * Gradle task для конвертации plantuml диаграмм в .png
 * @author Igor Popov
 * @since 09.11.2020
 */
open class DocumentationConvertDiagramTask : DefaultTask() {
    @TaskAction
    fun taskAction() {
        project.fileTree(".").filter {
            it.parentFile.name == "resources" && (it.name.endsWith(".puml") || it.name.endsWith(".plantuml"))
        }.forEach { sourceFile ->
            val sourceFileName = sourceFile.name.substring(0, sourceFile.name.lastIndexOf('.')) + ".png"
            val compiledFile = project.file(Paths.get(sourceFile.parent, sourceFileName).toString())
            if (!compiledFile.exists() || compiledFile.lastModified() < sourceFile.lastModified()) {
                if (!compiledFile.exists()) {
                    compiledFile.createNewFile()
                }
                System.setProperty("plantuml.include.path", sourceFile.parent)
                val source = SourceStringReader(sourceFile.readText())
                val compileResult = FileOutputStream(compiledFile).use { out ->
                    source.outputImage(out, FileFormatOption(FileFormat.PNG, false)).description
                }
                if (!compiledFile.exists()) {
                    throw IOException("PlantUML diagram compilation failed: $sourceFile")
                }
                if (compileResult.contains("Error")) {
                    val errFile =
                            File(compiledFile.path.substring(0, compiledFile.path.lastIndexOf('.')) + ".error.png")
                    compiledFile.renameTo(errFile)
                    throw IOException("PlantUML diagram compilation errors, see image file: " + errFile.path)
                }
            }
        }
    }
}
