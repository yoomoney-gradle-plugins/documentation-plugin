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
open class DocumentationRenderConvertDiagramTask : DefaultTask() {
    @TaskAction
    fun taskAction() {
        project.fileTree(".").filter {
            it.name.endsWith(".puml") || it.name.endsWith(".plantuml")
        }.forEach { sourceFile ->
            val sourceFileName = sourceFile.name.substring(0, sourceFile.name.lastIndexOf('.')) + ".png"
            val compiledFile = project.file(Paths.get(sourceFile.parent, sourceFileName).toString())
            if (!compiledFile.exists() || compiledFile.lastModified() < sourceFile.lastModified()) {
                if (!compiledFile.exists()) {
                    compiledFile.createNewFile()
                }
                val source = SourceStringReader(sourceFile.readText())
                val out = FileOutputStream(compiledFile)
                val compileResult = source.outputImage(out, FileFormatOption(FileFormat.PNG, false)).description
                out.close()
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
