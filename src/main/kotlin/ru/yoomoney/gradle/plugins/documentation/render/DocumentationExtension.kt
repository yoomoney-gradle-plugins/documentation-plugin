package ru.yoomoney.gradle.plugins.documentation.render

/**
 * Объект расширение DSL gradle для конфигурации плагина
 *
 * @author Igor Popov
 * @since 06.11.2020
 */
open class DocumentationExtension {
    /**
     * Рутовые файлы, которые необходимо отрендерить в .html
     */
    var rootFiles: MutableList<String> = mutableListOf()

    /**
     * Email пользователя, под которым будет произведен коммит измененных файлов в репозиторий
     */
    var gitUserEmail: String = "SvcReleaserBackend@yoomoney.ru"

    /**
     * Имя пользователя, под которым будет произведен коммит измененных файлов в репозиторий
     */
    var gitUserName: String = "SvcReleaserBackend"
}
