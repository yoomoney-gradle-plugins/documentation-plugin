package ru.yoomoney.gradle.plugins.documentation.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.eclipse.jgit.api.AddCommand
import org.eclipse.jgit.api.CommitCommand
import org.eclipse.jgit.api.DiffCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PushCommand
import org.eclipse.jgit.api.StatusCommand
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.Transport
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.util.StringUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Objects

/**
 * Класс для работы с git
 *
 * @author Oleg Kandaurov
 * @since 28.01.2019
 */
class GitRepo internal constructor(private val git: Git, private val settings: GitSettings) : AutoCloseable {

    /**
     * Прокси для вызова [PushCommand]
     *
     * @param command команда для выполнения
     * @return сообщение об ошибке, в случае неуспешного выполнения
     */
    fun push(command: (PushCommand) -> Unit): String? {
        Objects.requireNonNull(command, "command")
        try {
            ByteArrayOutputStream().use { out ->
                val pushCommand = git.push()
                command(pushCommand)
                configureTransport(pushCommand, settings)
                pushCommand.setOutputStream(out)
                pushCommand.call()
                val resultMessage = out.toString()
                return if (StringUtils.isEmptyOrNull(resultMessage) ||
                        resultMessage.contains("Create pull request") ||
                        resultMessage.contains("View pull request")) {
                    null
                } else {
                    resultMessage
                }
            }
        } catch (exc: GitAPIException) {
            return exc.message
        } catch (exc: IOException) {
            return exc.message
        }
    }

    /**
     * Получение объекта с данными по репозитории
     *
     * @return данные репозитория
     */
    val repository: Repository
        get() = git.repository

    /**
     * Прокси для вызова [DiffCommand]
     */
    fun diff(): DiffCommand {
        return git.diff()
    }

    /**
     * Определяет имя текущей ветки
     *
     * @return имя текущей ветки в формате "release/3.234", "master"
     */
    val currentBranchName: String
        get() = git.repository.branch

    /**
     * Прокси для вызова [CommitCommand]
     */
    fun commit(): CommitCommand {
        val commit = git.commit()
        commit.setAuthor(settings.username, settings.email)
        return commit
    }

    /**
     * Прокси для вызова [StatusCommand]
     */
    fun status(): StatusCommand {
        return git.status()
    }

    /**
     * Прокси для вызова [AddCommand]
     */
    fun add(): AddCommand {
        return git.add()
    }

    override fun close() {
        git.close()
    }

    companion object {
        /**
         * Конфигурирование взаимодействия с гитом
         *
         * @param command команда, для которой нужна конфигурация
         * @param settings настройки для конфигурации
         */
        fun configureTransport(command: TransportCommand<*, *>, settings: GitSettings) {
            if (settings.sshKeyPath == null) {
                return
            }
            val sessionFactory: JschConfigSessionFactory = object : JschConfigSessionFactory() {
                override fun configure(hc: OpenSshConfig.Host, session: Session) {
                    session.setConfig("StrictHostKeyChecking", "no")
                }

                override fun getJSch(hc: OpenSshConfig.Host, fs: FS): JSch {
                    val jsch = super.getJSch(hc, fs)
                    jsch.removeAllIdentity()
                    jsch.addIdentity(settings.sshKeyPath, settings.passphraseSshKey)
                    return jsch
                }
            }
            command.setTransportConfigCallback { transport: Transport? ->
                if (transport is SshTransport) {
                    transport.sshSessionFactory = sessionFactory
                }
            }
        }
    }
}
