package ru.yoomoney.gradle.plugins.documentation.task

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.nio.file.Paths

/**
 * Тесты Gradle task'и DocumentationValidationTask
 * @author Igor Popov
 * @since 09.11.2020
 */
class DocumentationValidationTaskTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    @Test
    fun `should fail if anchors are not uniq`() {
        // given
        val docsDir = testProjectDir.newFolder("docs")

        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.writeText(DocumentationValidationTaskTest::class.java.getResource("/build.gradle").readText())

        copyDocsFrom(docsDir.absolutePath, "/validation/failed")

        // when
        val result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments("documentationValidation")
                .withDebug(true)
                .buildAndFail()

        // then
        assertEquals(TaskOutcome.FAILED, result.task(":documentationValidation")?.outcome)
    }

    private fun copyDocsFrom(dest: String, from: String) {
        Paths.get(dest, "c").toFile().mkdir()
        Paths.get(dest, "c", "c.adoc").toFile()
                .writeText(DocumentationValidationTaskTest::class.java.getResource("$from/c/c.adoc").readText())
        Paths.get(dest, "a.adoc").toFile()
                .writeText(DocumentationValidationTaskTest::class.java.getResource("$from/a.adoc").readText())
        Paths.get(dest, "b.adoc").toFile()
                .writeText(DocumentationValidationTaskTest::class.java.getResource("$from/b.adoc").readText())
    }

    @Test
    fun `should success if anchors are uniq`() {
        // given
        val docsDir = testProjectDir.newFolder("docs")

        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.writeText(DocumentationValidationTaskTest::class.java.getResource("/build.gradle").readText())

        copyDocsFrom(docsDir.absolutePath, "/validation/success")

        // when
        val result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments("documentationValidation")
                .withDebug(true)
                .build()

        // then
        assertEquals(TaskOutcome.SUCCESS, result.task(":documentationValidation")?.outcome)
    }
}