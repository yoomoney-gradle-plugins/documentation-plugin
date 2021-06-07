package ru.yoomoney.gradle.plugins.documentation.render

import org.gradle.api.provider.Property

/**
 * Объект расширение DSL gradle для конфигурации плагина
 *
 * @author Igor Popov
 * @since 06.11.2020
 */
abstract class DocumentationExtension {
    /**
     * Рутовые файлы, которые необходимо отрендерить в .html
     */
    var rootFiles: MutableList<String> = mutableListOf()

    /**
     * Email пользователя, под которым будет произведен коммит измененных файлов в репозиторий.
     * Выполнение gradle task [DocumentationCommitTask] невозможно без указания значения данной настройки.
     */
    abstract val gitUserEmail: Property<String>

    /**
     * Имя пользователя, под которым будет произведен коммит измененных файлов в репозиторий
     * Выполнение gradle task [DocumentationCommitTask] невозможно без указания значения данной настройки.
     */
    abstract val gitUserName: Property<String>
}
