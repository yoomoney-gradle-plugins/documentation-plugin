package ru.yoomoney.gradle.plugins.documentation.render

import org.eclipse.jgit.api.errors.GitAPIException
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import ru.yoomoney.gradle.plugins.documentation.git.GitRepo
import ru.yoomoney.gradle.plugins.documentation.git.GitRepoFactory
import ru.yoomoney.gradle.plugins.documentation.git.GitSettings
import java.io.File
import java.io.IOException
import java.util.Objects.requireNonNull

/**
 * Gradle task для коммита измененных файлов документации
 * @author Igor Popov
 * @since 06.11.2020
 */
open class DocumentationCommitTask : DefaultTask() {
    companion object {
        const val GIT_USER_EMAIL = "SvcReleaserBackend@yoomoney.ru"
        const val GIT_USER_NAME = "SvcReleaserBackend"
    }

    @get:Input
    lateinit var rootFiles: MutableList<String>

    @TaskAction
    fun taskAction() {
        val git = createGitRepo()
        addFiles(git)
        if (hasChanges(git)) {
            commit(git)
            push(git)
        } else {
            project.logger.lifecycle("Nothing to commit")
        }
    }

    private fun createGitRepo(): GitRepo {
        val gitPrivateSshKeyPath = requireNonNull(System.getenv("GIT_PRIVATE_SSH_KEY_PATH"), "gitPrivateSshKeyPath")
        val gitSettings = GitSettings(
                email = GIT_USER_EMAIL,
                username = GIT_USER_NAME,
                sshKeyPath = gitPrivateSshKeyPath
        )
        return GitRepoFactory(gitSettings).createFromExistingDirectory(File("."))
    }

    private fun addFiles(git: GitRepo) {
        val addCommand = git.add()
        rootFiles.forEach { addCommand.addFilepattern(it.replace(".adoc", ".html")) }
        git.status().call().untracked
                .filter { it.endsWith(".png") }
                .forEach { addCommand.addFilepattern(it) }
        addCommand.call()
    }

    private fun hasChanges(git: GitRepo): Boolean {
        return git.diff().call().isNotEmpty()
    }

    private fun commit(git: GitRepo) {
        try {
            project.logger.lifecycle("Commit files from the index")
            val command = git.commit().setMessage("[Gradle Documentation Plugin] Commit with a rendered new or modified docs")
            rootFiles.forEach { command.setOnly(it.replace(".adoc", ".html")) }
            val statusResult = git.status().call()
            statusResult.added
                    .plus(statusResult.modified)
                    .plus(statusResult.changed)
                    .filter { it.endsWith(".png") }
                    .forEach { command.setOnly(it) }
            command.call()
        } catch (ex: GitAPIException) {
            throw GradleException("Can't commit changes", ex)
        }
    }

    private fun push(git: GitRepo) {
        try {
            val branchName = git.repository.fullBranch
            project.logger.lifecycle("Push to the branch={}", git.currentBranchName)
            git.push {
                it.add(branchName).setPushTags().remote = "origin"
            }?.let { resultMessage -> throw GradleException("Can't push: $resultMessage") }
        } catch (exc: IOException) {
            throw GradleException("Can't push", exc)
        }
    }
}
