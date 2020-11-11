package ru.yandex.money.gradle.plugins.documentation.render

import org.amshove.kluent.`should be equal to`
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.rules.TemporaryFolder
import java.nio.file.Paths

/**
 * Тесты Gradle task'и DocumentationPreprocessTask
 * @author Igor Popov
 * @since 09.11.2020
 */
class DocumentationPreprocessTaskTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    @Test
    fun `should preprocess works correctly`() {
        // given
        val docsDir = testProjectDir.newFolder("docs")

        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.writeText(DocumentationValidationTaskTest::class.java.getResource("/build.gradle").readText())

        Paths.get(docsDir.absolutePath, "a.adoc").toFile()
                .writeText(DocumentationPreprocessTaskTest::class.java.getResource("/preprocess/raw/a.adoc").readText())

        Paths.get(docsDir.absolutePath, "b").toFile().mkdir()
        Paths.get(docsDir.absolutePath, "b", "b.adoc").toFile()
                .writeText(DocumentationPreprocessTaskTest::class.java.getResource("/preprocess/raw/b/b.adoc").readText())
        Paths.get(docsDir.absolutePath, "b", "b.txt").toFile()
                .writeText(DocumentationPreprocessTaskTest::class.java.getResource("/preprocess/raw/b/b.txt").readText())

        Paths.get(docsDir.absolutePath, "b", "c").toFile().mkdir()
        Paths.get(docsDir.absolutePath, "b", "c", "c.adoc").toFile()
                .writeText(DocumentationPreprocessTaskTest::class.java.getResource("/preprocess/raw/b/c/c.adoc").readText())

        // when
        val result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments("documentationPreprocess")
                .withDebug(true)
                .build()

        // then
        assertEquals(TaskOutcome.SUCCESS, result.task(":documentationPreprocess")?.outcome)

        Paths.get(docsDir.absolutePath, "a.adoc").toFile().readText() `should be equal to`
                DocumentationPreprocessTaskTest::class.java.getResource("/preprocess/processed/a.adoc").readText()
        Paths.get(docsDir.absolutePath, "b", "b.adoc").toFile().readText() `should be equal to`
                DocumentationPreprocessTaskTest::class.java.getResource("/preprocess/processed/b/b.adoc").readText()
        Paths.get(docsDir.absolutePath, "b", "b.txt").toFile().readText() `should be equal to`
                DocumentationPreprocessTaskTest::class.java.getResource("/preprocess/processed/b/b.txt").readText()
        Paths.get(docsDir.absolutePath, "b", "c", "c.adoc").toFile().readText() `should be equal to`
                DocumentationPreprocessTaskTest::class.java.getResource("/preprocess/processed/b/c/c.adoc").readText()
    }
}